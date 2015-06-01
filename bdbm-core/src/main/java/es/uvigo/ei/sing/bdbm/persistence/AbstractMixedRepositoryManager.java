/*
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2015 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Florentino Fdez-Riverola and Jorge Vieira
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.sing.bdbm.persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.environment.paths.RepositoryPaths;
import es.uvigo.ei.sing.bdbm.persistence.EntityAlreadyExistsException;
import es.uvigo.ei.sing.bdbm.persistence.EntityValidationException;
import es.uvigo.ei.sing.bdbm.persistence.MixedRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.watcher.PollingRepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryListener;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherListener;

public abstract class AbstractMixedRepositoryManager<
	SE extends SequenceEntity, 
	P extends ProteinSequenceEntity, 
	N extends NucleotideSequenceEntity
> implements MixedRepositoryManager<SE, P, N> {
	protected RepositoryPaths repositoryPaths;
	protected List<RepositoryListener> listeners;
	
//	protected RepositoryWatcher protWatcherThread;
//	protected RepositoryWatcher nuclWatcherThread;
	protected final RepositoryWatcher protWatcher;
	protected final RepositoryWatcher nuclWatcher;
	protected CustomRepositoryWatcherListener protListener;
	protected CustomRepositoryWatcherListener nuclListener;
	
	public AbstractMixedRepositoryManager() {
		this.listeners = Collections.synchronizedList(
			new LinkedList<RepositoryListener>()
		);
		
		this.protWatcher = new PollingRepositoryWatcher();
		this.nuclWatcher = new PollingRepositoryWatcher();
	}
	
	@Override
	public synchronized void shutdown() {
		this.protWatcher.clear();
		this.protWatcher.removeRepositoryWatcherListener(this.protListener);
		
		this.nuclWatcher.clear();
		this.nuclWatcher.removeRepositoryWatcherListener(this.nuclListener);
	}
	
	@Override
	public synchronized void setRepositoryPaths(RepositoryPaths repositoryPaths) throws IOException {
		this.repositoryPaths = repositoryPaths;
		
		if (this.protListener != null) {
			this.protWatcher.clear();
			this.protWatcher.removeRepositoryWatcherListener(this.protListener);
			this.protListener = null;
		}
		if (this.nuclListener != null) {
			this.nuclWatcher.clear();
			this.nuclWatcher.removeRepositoryWatcherListener(this.nuclListener);
			this.nuclListener = null;
		}
		
		final File protDirectory = this.getDirectory(SequenceType.PROTEIN);
		final File nuclDirectory = this.getDirectory(SequenceType.NUCLEOTIDE);
		
		this.protWatcher.register(protDirectory);
		this.nuclWatcher.register(nuclDirectory);
		
		this.protWatcher.addRepositoryWatcherListener(
			this.protListener = new CustomRepositoryWatcherListener(
				SequenceType.PROTEIN, protDirectory
			)
		);
		
		this.nuclWatcher.addRepositoryWatcherListener(
			this.nuclListener = new CustomRepositoryWatcherListener(
				SequenceType.NUCLEOTIDE, nuclDirectory
			)
		);
		
//		this.stopWatchers();
//		this.checkWatchers();
	}

	private final class CustomRepositoryWatcherListener implements RepositoryWatcherListener {
		private final File repositoryFile;
		private final SequenceType sequenceType;
		
		public CustomRepositoryWatcherListener(SequenceType sequenceType, File repositoryFile) {
			this.repositoryFile = repositoryFile;
			this.sequenceType = sequenceType;
		}
		
		@Override
		public void repositoryChanged(RepositoryWatcherEvent event) {
			final File file = event.getFile();
			final SE entity = this.createEntity(file);
			
			if (file.equals(entity.getBaseFile())) {
				final RepositoryEvent.Type type = event.getType() == RepositoryWatcherEvent.Type.CREATE ?
					RepositoryEvent.Type.CREATE : RepositoryEvent.Type.DELETE; 
				
				AbstractMixedRepositoryManager.this.fireRepositoryChanged(entity, type);
			} else if (event.getType() == RepositoryWatcherEvent.Type.DELETE) {
				// Events fired by files without parent (e.g. deleting a directory with files)
				// are filtered.
				if (file.getParentFile().exists()) {
					try {
						if (Boolean.valueOf(System.getProperty("entities.validate", "true")))
							AbstractMixedRepositoryManager.this.getEntityValidator().validate(entity);
						
						AbstractMixedRepositoryManager.this.fireRepositoryChanged(entity, file);
					} catch (EntityValidationException eve) {
						AbstractMixedRepositoryManager.this.fireRepositoryChanged(entity, RepositoryEvent.Type.INVALIDATED);
					}
				}
			} else {
				AbstractMixedRepositoryManager.this.fireRepositoryChanged(entity, file);
			}
		}

		private File baseFile(File file) {
			File baseFile = file;
			File parentFile = baseFile.getParentFile();
			
			while (parentFile != null && !parentFile.equals(this.repositoryFile)) {
				baseFile = parentFile;
				parentFile = baseFile.getParentFile();
			}
			
			if (this.repositoryFile.equals(baseFile.getParentFile())) {
				return baseFile;
			} else {
				throw new IllegalArgumentException("Invalid path: " + file);
			}
			
//			final Path path = file.toPath();
//			if (path.startsWith(this.repositoryPath)) {
//				final Path basePath = (path.getNameCount() == this.repositoryPath.getNameCount() + 1)?
//					path : path.getRoot().resolve(path.subpath(0, this.repositoryPath.getNameCount()+1));
//				
//				return basePath.toFile().getAbsoluteFile();
//			} else {
//				throw new IllegalArgumentException("Invalid path: " + path);
//			}
		}
		
		private SE createEntity(File child) {
			final SE entity = AbstractMixedRepositoryManager.this.getEntityBuilder()
				.create(this.sequenceType, this.baseFile(child));
			
			return entity;
		}
	}
	
//	protected synchronized void checkWatchers() throws IllegalStateException {
//		if (this.listeners.isEmpty()) {
//			try {
//				this.stopWatchers();
//			} catch (IOException ioe) {
//				throw new IllegalStateException("Watchers could not be stopped", ioe);
//			}
//		} else if (this.protWatcherThread == null && this.nuclWatcherThread == null) {
//			try {
//				this.startWatchers();
//			} catch (IOException ioe) {
//				throw new IllegalStateException("Watchers could not be started", ioe);
//			}
//		}
//	}
//	
//	protected synchronized void startWatchers() throws IOException {
//		if (this.protWatcherThread == null && this.nuclWatcherThread == null) {
//			this.protWatcherThread = new RepositoryWatcher(SequenceType.PROTEIN);
//			this.nuclWatcherThread = new RepositoryWatcher(SequenceType.NUCLEOTIDE);
//			
////			this.protWatcherThread.setDaemon(true);
////			this.nuclWatcherThread.setDaemon(true);
//			
//			this.protWatcherThread.start();
//			this.nuclWatcherThread.start();
//		}
//	}
//
//	protected synchronized void stopWatchers() throws IOException {
//		if (this.protWatcherThread != null) {
//			this.protWatcherThread.interrupt();
//		}
//		
//		if (this.nuclWatcherThread != null) {
//			this.nuclWatcherThread.interrupt();
//		}
//		
//		try {
//			if (this.protWatcherThread != null)
//				this.protWatcherThread.join();
//			this.protWatcherThread = null;
//		} catch (InterruptedException e) {
//			this.getLogger().warn("Error stopping protein watcher", e);
//		}
//		
//		try {
//			if (this.protWatcherThread != null)
//				this.protWatcherThread.join();
//			this.nuclWatcherThread = null;
//		} catch (InterruptedException e) {
//			this.getLogger().warn("Error stopping nucleotide watcher", e);
//		}
//	}
	
	protected abstract Logger getLogger();
	
	protected abstract EntityBuilder<SE> getEntityBuilder();
	protected abstract EntityValidator<SE> getEntityValidator();
	
	@SuppressWarnings("unchecked")
	private Class<? extends SE> getSequenceEntityClass() {
		return (Class<? extends SE>)
			((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends P> getProteinClass() {
		return (Class<? extends P>)
			((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends N> getNucleotideClass() {
		return (Class<? extends N>)
			((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}
	

	@SuppressWarnings("unchecked")
	protected SE[] createEntities(File[] files, SequenceType seqType) {
		final EntityBuilder<SE> builder = this.getEntityBuilder();
		final EntityValidator<SE> validator = this.getEntityValidator();
		
		final Class<? extends SE> entityClass = this.getSequenceEntityClass();
//			(Class<?>) ((ParameterizedType) builder.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		final SortedSet<SE> entities = new TreeSet<SE>();
		for (int i = 0; i < files.length; i++) {
			try {
				final SE entity = builder.create(seqType, files[i]);

				if (Boolean.valueOf(System.getProperty("entities.validate", "true")))
					validator.validate(entity);
				
				entities.add(entity);
			} catch (EntityValidationException eve) {
				this.getLogger().warn("Entity validation error: " + eve.getEntity(), eve);
			}
		}
		
		return entities.toArray((SE[]) Array.newInstance(entityClass, entities.size()));
	}
	
	protected abstract File getDirectory(SequenceType sequenceType);
	protected abstract FileFilter getDirectoryFilter();
	protected abstract boolean createEntityFiles(SE entity);
	
	protected SE createEntity(SequenceType sequenceType, String name) {
		return this.getEntityBuilder().create(
			sequenceType, 
			new File(this.getDirectory(sequenceType), name)
		);
	}
	
	@Override
	public SE create(SequenceType sequenceType, String name)
	throws EntityAlreadyExistsException, IOException {
		if (this.exists(sequenceType, name)) {
			throw new EntityAlreadyExistsException(null);
		} else {
			final SE entity = this.createEntity(sequenceType, name);
			
			if (this.createEntityFiles(entity)) {
				return entity;
			} else {
				throw new IOException("Entity could not be created");
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public N createNucleotide(String name) throws EntityAlreadyExistsException, IOException {
		return (N) this.create(SequenceType.NUCLEOTIDE, name);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public P createProtein(String name) throws EntityAlreadyExistsException, IOException {
		return (P) this.create(SequenceType.PROTEIN, name);
	}
	
	@Override
	public SE get(SequenceType sequenceType, String name) throws IOException {
		if (this.exists(sequenceType, name)) {
			return this.createEntity(sequenceType, name);
		} else {
			return this.create(sequenceType, name);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public N getNucleotide(String name) throws IOException {
		return (N) this.get(SequenceType.NUCLEOTIDE, name);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public P getProtein(String name) throws IOException {
		return (P) this.get(SequenceType.PROTEIN, name);
	}
	
	@Override
	public boolean delete(SE entity) throws IOException, IllegalArgumentException {
		if (!entity.getBaseFile().exists()) {
			return false;
		} else if (entity.getBaseFile().isFile()) {
			return entity.getBaseFile().delete();
		} else if (entity.getBaseFile().isDirectory()) {
			FileUtils.deleteDirectory(entity.getBaseFile());
			return !entity.getBaseFile().exists();
		} else {
			throw new IllegalArgumentException("Unknown file type: " + entity.getBaseFile());
		}
	}
	
	@Override
	public void validateEntityPath(SequenceType type, File entityPath) throws EntityValidationException {
		this.getEntityValidator().validate(
			this.getEntityBuilder().create(type, entityPath)
		);
	}
	
	@Override
	public void validate(SE entity) throws EntityValidationException {
		final EntityValidator<SE> entityValidator = this.getEntityValidator();
		
		entityValidator.validate(entity);
	}
	
	@Override
	public SE[] list(SequenceType sequenceType) {
		final File directory = this.getDirectory(sequenceType);
		
		if (directory.isDirectory() && directory.canRead()) {
			final File[] files = directory.listFiles(this.getDirectoryFilter());
			
			return this.createEntities(files, sequenceType);
		} else {
			throw new IllegalArgumentException("Directory " + directory.getAbsolutePath() + " is not valid");
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public P[] listProtein() {
		final SE[] proteinEntities = this.list(SequenceType.PROTEIN);

		final P[] castedProtEntities = (P[]) Array.newInstance(this.getProteinClass(), proteinEntities.length);
		System.arraycopy(proteinEntities, 0, castedProtEntities, 0, proteinEntities.length);
		
		return castedProtEntities;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public N[] listNucleotide() {
		final SE[] nucleotideEntities = this.list(SequenceType.NUCLEOTIDE);

		final N[] castedNuclEntities = (N[]) Array.newInstance(this.getNucleotideClass(), nucleotideEntities.length);
		System.arraycopy(nucleotideEntities, 0, castedNuclEntities, 0, nucleotideEntities.length);
		
		return castedNuclEntities;
	}
	
	@Override
	public boolean exists(SE entity) {
		if (entity.getBaseFile().exists()) {
			try {
				this.getEntityValidator().validate(entity);
				return true;
			} catch (EntityValidationException eve) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean exists(SequenceType sequenceType, String name) {
		final File file = new File(this.getDirectory(sequenceType), name);
		final SE entity = this.getEntityBuilder().create(sequenceType, file);
		
		return this.exists(entity);
	}
	
	@Override
	public boolean existsProtein(String name) {
		return this.exists(SequenceType.PROTEIN, name);
	}
	
	@Override
	public boolean existsNucleotide(String name) {
		return this.exists(SequenceType.NUCLEOTIDE, name);
	}
	
	@Override
	public void addRepositoryListener(RepositoryListener listener) {
		this.listeners.add(listener);
		
//		this.checkWatchers();
	}
	
	@Override
	public boolean removeRepositoryListener(RepositoryListener listener) {
		final boolean result = this.listeners.remove(listener);
		
//		this.checkWatchers();
		
		return result;
	}
	
	@Override
	public boolean containsRepositoryListener(RepositoryListener listener) {
		return this.listeners.contains(listener);
	}
	
	@Override
	public List<RepositoryListener> listRepositoryListeners() {
		return Collections.unmodifiableList(this.listeners);
	}
	
	protected void fireRepositoryChanged(SequenceEntity entity, File changedFile) {
		final List<RepositoryListener> listeners = new ArrayList<RepositoryListener>();
		
		synchronized(this.listeners) {
			listeners.addAll(this.listeners);
		}
		
		final RepositoryEvent event = new RepositoryEvent(entity, changedFile);
		for (RepositoryListener listener : listeners) {
			listener.repositoryChanged(event);
		}
	}
	
	protected void fireRepositoryChanged(SequenceEntity entity, RepositoryEvent.Type type) {
		final List<RepositoryListener> listeners = new ArrayList<RepositoryListener>();
		
		synchronized(this.listeners) {
			listeners.addAll(this.listeners);
		}
		
		for (RepositoryListener listener : listeners) {
			listener.repositoryChanged(new RepositoryEvent(entity, type));
		}
	}
	
//	private final class RepositoryWatcher extends WatchServiceThread {
//		private final SequenceType sequenceType;
//		private final Path repositoryPath;
//		
////		private final WatchService watcher;
//		private final Map<WatchKey, Path> keys;
//		
//		public RepositoryWatcher(SequenceType sequenceType) throws IOException {
//			super();
//			this.sequenceType = sequenceType;
//			this.repositoryPath = AbstractRepositoryManager.this.getDirectory(sequenceType).toPath();
//			
////			this.watcher = FileSystems.getDefault().newWatchService();
//			this.keys = new HashMap<WatchKey, Path>();
//			
//			this.registerAll(this.repositoryPath);
//		}
//		
//		private Logger getLogger() {
//			return AbstractRepositoryManager.this.getLogger();
//		}
//		
//		private void unregister(Path dir) {
//			WatchKey key = null;
//			for (Map.Entry<WatchKey, Path> keysEntry : keys.entrySet()) {
//				if (keysEntry.getValue().equals(dir)) {
//					key = keysEntry.getKey();
//					break;
//				}
//			}
//			
//			if (key == null) {
//				this.getLogger().warn("Missing key for: " + dir);
//			} else {
//				this.getLogger().debug("Unregistered: " + this.keys.remove(key));
//			}
//		}
//		
//		private void register(Path dir) throws IOException {
//			if (!this.keys.containsValue(dir)) {
//				final WatchKey key = dir.register(this.watcher, 
//					StandardWatchEventKinds.ENTRY_CREATE,
//					StandardWatchEventKinds.ENTRY_DELETE,
//					StandardWatchEventKinds.ENTRY_MODIFY
//				);
//				
//				this.keys.put(key, dir);
//				this.getLogger().debug("Registered: " + dir);
//			} else {
//				this.getLogger().warn("Already Registered: " + dir);
//			}
//		}
//		
//		private void registerAll(Path dir) throws IOException {
//			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
//				@Override
//				public FileVisitResult preVisitDirectory(
//					Path dir, BasicFileAttributes attrs
//				) throws IOException {
//					register(dir);
//					return FileVisitResult.CONTINUE;
//				}
//			});
//		}
//		
//		private RepositoryEvent.Type convert(WatchEvent.Kind<Path> kind) {
//			if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
//				return RepositoryEvent.Type.CREATE;
//			} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//				return RepositoryEvent.Type.DELETE;
//			} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//				return RepositoryEvent.Type.MODIFY;
//			} else {
//				throw new IllegalArgumentException("Unsupported path event kind: " + kind);
//			}
//		}
//		
//		private File baseFile(Path path) {
//			if (path.startsWith(this.repositoryPath)) {
//				final Path basePath = (path.getNameCount() == this.repositoryPath.getNameCount() + 1)?
//					path : path.getRoot().resolve(path.subpath(0, this.repositoryPath.getNameCount()+1));
//				
//				return basePath.toFile().getAbsoluteFile();
//			} else {
//				throw new IllegalArgumentException("Invalid path: " + path);
//			}
//		}
//		
//		private SE createEntity(Path child, boolean validate) {
//			final SE entity = AbstractRepositoryManager.this.getEntityBuilder()
//				.create(this.sequenceType, this.baseFile(child));
//			
//			if (validate) {
//				try {
//					AbstractRepositoryManager.this.getEntityValidator().validate(entity);
//				} catch (EntityValidationException eve) {
//					this.getLogger().debug("Error validating entity: " + entity, eve);
//					
//					return null;
//				}
//			}
//			return entity;
//		}
//		
//		@Override
//		protected boolean processKey(WatchKey key) {
//			for (WatchEvent<?> event : key.pollEvents()) {
//				final Kind<?> kind = event.kind();
//				
//				if (kind != StandardWatchEventKinds.OVERFLOW) {
//					@SuppressWarnings("unchecked")
//					final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
//					
//					final Path changedDir = this.keys.containsKey(key)?
//						this.keys.get(key) : 
//						this.repositoryPath;
//					
//					final Path directory = pathEvent.context();
//					final Path resolvedDir = changedDir.resolve(directory);
//					
//					final SE entity;
//					if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
//						entity = this.createEntity(resolvedDir, true);
//						if (Files.isDirectory(resolvedDir, LinkOption.NOFOLLOW_LINKS)) {
//							try {
//								registerAll(resolvedDir);
//							} catch (IOException e) {}
//						}
//					} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
//						entity = this.createEntity(resolvedDir, false);
//						this.unregister(resolvedDir);
//					} else {
//						continue;
//					}
//					
//					if (entity != null) {
//						AbstractRepositoryManager.this.fireRepositoryChanged(
//							entity, this.convert(pathEvent.kind())
//						);
//					}
//				}
//			}
//			
//			if (!key.reset()) {
//				this.keys.remove(key);
//			}
//			
//			return !this.keys.isEmpty();
//		}
//	
////		@Override
////		public void run() {
////			try {
////				for (;;) {
////					final WatchKey key;
////					
////					try {
////						key = this.watcher.take();
////					} catch (InterruptedException e) {
////						this.getLogger().debug("Watcher interrupted: " + this);
////						return;
////					}
////					
////					for (WatchEvent<?> event : key.pollEvents()) {
////						final Kind<?> kind = event.kind();
////						
////						if (kind != StandardWatchEventKinds.OVERFLOW) {
////							@SuppressWarnings("unchecked")
////							final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
////							
////							final Path changedDir = this.keys.containsKey(key)?
////								this.keys.get(key) : 
////								this.repositoryPath;
////							
////							final Path directory = pathEvent.context();
////							final Path resolvedDir = changedDir.resolve(directory);
////							
////							final SE entity;
////							if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
////								entity = this.createEntity(resolvedDir, true);
////								if (Files.isDirectory(resolvedDir, LinkOption.NOFOLLOW_LINKS)) {
////									try {
////										registerAll(resolvedDir);
////									} catch (IOException e) {}
////								}
////							} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
////								entity = this.createEntity(resolvedDir, false);
////								this.unregister(resolvedDir);
////							} else {
////								continue;
////							}
////							
////							if (entity != null) {
////								AbstractRepositoryManager.this.fireRepositoryChanged(
////									entity, this.convert(pathEvent.kind())
////								);
////							}
////						}
////					}
////					
////					if (!key.reset()) {
////						this.keys.remove(key);
////					}
////				}
////			} finally {
////				try {
////					this.watcher.close();
////				} catch (IOException e) {
////					e.printStackTrace();
////				}
////			}
////		}
//	}
}
