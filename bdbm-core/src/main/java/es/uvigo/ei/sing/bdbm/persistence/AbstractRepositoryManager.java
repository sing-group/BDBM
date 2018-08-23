/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.environment.paths.RepositoryPaths;
import es.uvigo.ei.sing.bdbm.persistence.EntityAlreadyExistsException;
import es.uvigo.ei.sing.bdbm.persistence.EntityValidationException;
import es.uvigo.ei.sing.bdbm.persistence.RepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.watcher.PollingRepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryListener;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherListener;

public abstract class AbstractRepositoryManager<SE extends SequenceEntity>
implements RepositoryManager<SE> {
	protected RepositoryPaths repositoryPaths;
	protected List<RepositoryListener> listeners;
	
	protected final RepositoryWatcher watcher;
	protected CustomRepositoryWatcherListener listener;
	
	public AbstractRepositoryManager() {
		this.listeners = new CopyOnWriteArrayList<>();
		
		this.watcher = new PollingRepositoryWatcher();
	}
	
	protected abstract Logger getLogger();
	
	protected abstract EntityBuilder<SE> getEntityBuilder();
	protected abstract EntityValidator<SE> getEntityValidator();
	
	protected abstract File getDirectory();
	protected abstract FileFilter getDirectoryFilter();
	protected abstract boolean createEntityFiles(SE entity);

	protected abstract SequenceType getSequenceType();
	
	@Override
	public synchronized void shutdown() {
		this.watcher.clear();
		this.watcher.removeRepositoryWatcherListener(this.listener);
	}
	
	@Override
	public synchronized void setRepositoryPaths(RepositoryPaths repositoryPaths) throws IOException {
		this.repositoryPaths = repositoryPaths;
		
		if (this.listener != null) {
			this.watcher.clear();
			this.watcher.removeRepositoryWatcherListener(this.listener);
			this.listener = null;
		}
		
		final File directory = this.getDirectory();
		
		this.watcher.register(directory);
		
		this.watcher.addRepositoryWatcherListener(
			this.listener = new CustomRepositoryWatcherListener(directory)
		);
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends SE> getSequenceEntityClass() {
		return (Class<? extends SE>)
			((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@SuppressWarnings("unchecked")
	protected SE[] createEntities(File[] files) {
		final EntityBuilder<SE> builder = this.getEntityBuilder();
		final EntityValidator<SE> validator = this.getEntityValidator();
		
		final Class<? extends SE> entityClass = this.getSequenceEntityClass();
		
		final SortedSet<SE> entities = new TreeSet<SE>();
		for (int i = 0; i < files.length; i++) {
			try {
				final SE entity = builder.create(this.getSequenceType(), files[i]);
				
				validator.validate(entity);
				
				entities.add(entity);
			} catch (EntityValidationException eve) {
				this.getLogger().warn("Entity validation error: " + eve.getEntity(), eve);
			}
		}
		
		return entities.toArray((SE[]) Array.newInstance(entityClass, entities.size()));
	}

	protected void checkSequenceType(SequenceType sequenceType)
	throws IllegalArgumentException {
		if (!this.getSequenceType().equals(sequenceType)) {
			throw new IllegalArgumentException("sequenceType must be " + this.getSequenceType());
		}
	}
	
	protected SE createEntity(File file) {
		return this.getEntityBuilder().create(
			this.getSequenceType(), file
		);
	}
	
	protected SE createEntity(String name) {
		return this.createEntity(new File(this.getDirectory(), name));
	}
	
	public SE create(String name) throws EntityAlreadyExistsException, IOException {
		return this.create(this.getSequenceType(), name);
	}
	
	@Override
	public SE create(SequenceType sequenceType, String name)
	throws EntityAlreadyExistsException, IOException {
		this.checkSequenceType(sequenceType);
		
		if (this.exists(sequenceType, name)) {
			throw new EntityAlreadyExistsException(null);
		} else {
			final SE entity = this.createEntity(name);
			
			if (this.createEntityFiles(entity)) {
				return entity;
			} else {
				throw new IOException("Entity could not be created");
			}
		}
	}
	
	public SE get(String name) throws IOException {
		return this.get(this.getSequenceType(), name);
	}
	
	@Override
	public SE get(SequenceType sequenceType, String name) throws IOException {
		this.checkSequenceType(sequenceType);
		
		if (this.exists(sequenceType, name)) {
			return this.createEntity(name);
		} else {
			return this.create(sequenceType, name);
		}
	}
	
	@Override
	public boolean delete(SE entity) throws IOException, IllegalArgumentException {
		this.checkSequenceType(entity.getType());
		
		if (entity.getBaseFile().isFile()) {
			return entity.getBaseFile().delete();
		} else if (entity.getBaseFile().isDirectory()) {
			FileUtils.deleteDirectory(entity.getBaseFile());
			return !entity.getBaseFile().exists();
		} else {
			throw new IllegalArgumentException("Unknown file type: " + entity.getBaseFile());
		}
	}
	
	@Override
	public void validateEntityPath(SequenceType sequenceType, File entityPath) throws EntityValidationException {
		this.checkSequenceType(sequenceType);
		
		final EntityValidator<SE> entityValidator = this.getEntityValidator();
		final EntityBuilder<SE> entityBuilder = this.getEntityBuilder();
		
		entityValidator.validate(entityBuilder.create(sequenceType, entityPath));
	}
	
	public SE[] list() {
		return this.list(this.getSequenceType());
	}
	
	@Override
	public SE[] list(SequenceType sequenceType) {
		this.checkSequenceType(sequenceType);
		
		final File directory = this.getDirectory();
		
		if (directory.isDirectory() && directory.canRead()) {
			final File[] files = directory.listFiles(this.getDirectoryFilter());
			
			return this.createEntities(files);
		} else {
			throw new IllegalArgumentException("Directory " + directory.getAbsolutePath() + " is not valid");
		}
	}
	
	@Override
	public boolean exists(SE entity) {
		this.checkSequenceType(entity.getType());
		
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
	
	public boolean exists(String name) {
		return this.exists(this.getSequenceType(), name);
	}
	
	@Override
	public boolean exists(SequenceType sequenceType, String name) {
		this.checkSequenceType(sequenceType);
		
		final File file = new File(this.getDirectory(), name);
		final SE entity = this.getEntityBuilder().create(sequenceType, file);
		
		return this.exists(entity);
	}

	private final class CustomRepositoryWatcherListener implements RepositoryWatcherListener {
		private final File repositoryFile;
		
		public CustomRepositoryWatcherListener(File repositoryFile) {
			this.repositoryFile = repositoryFile;
		}
		
		@Override
		public void repositoryChanged(RepositoryWatcherEvent event) {
			final File file = event.getFile();
			final SE entity = AbstractRepositoryManager.this.createEntity(this.baseFile(file));
			
			if (file.equals(entity.getBaseFile())) {
				final RepositoryEvent.Type type = event.getType() == RepositoryWatcherEvent.Type.CREATE ?
					RepositoryEvent.Type.CREATE : RepositoryEvent.Type.DELETE;
				
				AbstractRepositoryManager.this.fireRepositoryChanged(entity, type);
			} else if (event.getType() == RepositoryWatcherEvent.Type.DELETE) {
				// Events fired by files without parent (e.g. deleting a directory with files)
				// are filtered.
				if (file.getParentFile().exists()) {
					try {
						AbstractRepositoryManager.this.getEntityValidator().validate(entity);
						AbstractRepositoryManager.this.fireRepositoryChanged(entity, file);
					} catch (EntityValidationException eve) {
						AbstractRepositoryManager.this.fireRepositoryChanged(entity, RepositoryEvent.Type.INVALIDATED);
					}
				}
			} else {
				AbstractRepositoryManager.this.fireRepositoryChanged(entity, file);
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
		}
	}
	
	@Override
	public void addRepositoryListener(RepositoryListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public boolean removeRepositoryListener(RepositoryListener listener) {
		final boolean result = this.listeners.remove(listener);
		
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
		final RepositoryEvent event = new RepositoryEvent(entity, changedFile);
		
		for (RepositoryListener listener : this.listeners) {
			listener.repositoryChanged(event);
		}
	}
	
	protected void fireRepositoryChanged(SequenceEntity entity, RepositoryEvent.Type type) {
		final RepositoryEvent event = new RepositoryEvent(entity, type);
		
		for (RepositoryListener listener : this.listeners) {
			listener.repositoryChanged(event);
		}
	}
}
