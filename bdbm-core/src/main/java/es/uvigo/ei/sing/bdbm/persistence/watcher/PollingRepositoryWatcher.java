/*
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2015 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
package es.uvigo.ei.sing.bdbm.persistence.watcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class PollingRepositoryWatcher extends AbstractRepositoryWatcher {
	protected final Set<File> watchedFiles;

	public PollingRepositoryWatcher() {
		this.watchedFiles = Collections.synchronizedSet(new HashSet<File>());
	}

	@Override
	public void register(File file) {
		final Lock readLock = PollingRepositoryWatchersPool.getInstance().getReadLock();
		
		readLock.lock();
		try {
			synchronized (this.watchedFiles) {
				if (this.watchedFiles.isEmpty()) {
					final Lock writeLock = PollingRepositoryWatchersPool.getInstance().getWriteLock();
					readLock.unlock();
					writeLock.lock();
					try {
						PollingRepositoryWatchersPool.getInstance().addWatcher(this);
					} finally {
						readLock.lock();
						writeLock.unlock();
					}
				}
				
				this.watchedFiles.add(file);
				
				if (file.isDirectory()) {
					for (File subFile : file.listFiles()) {
						this.register(subFile);
					}
				}
			}
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void unregister(File file) {
		final Lock readLock = PollingRepositoryWatchersPool.getInstance().getReadLock();
		
		readLock.lock();
		try {
			synchronized (this.watchedFiles) {
				this.watchedFiles.remove(file);
				
				if (file.isDirectory()) {
					for (File subFile : file.listFiles()) {
						this.unregister(subFile);
					}
				}
				
				if (this.watchedFiles.isEmpty()) {
					final Lock writeLock = PollingRepositoryWatchersPool.getInstance().getWriteLock();
					readLock.unlock();
					writeLock.lock();
					try {
						PollingRepositoryWatchersPool.getInstance().removeWatcher(this);
					} finally {
						readLock.lock();
						writeLock.unlock();
					}
				}
			}
		} finally {
			readLock.unlock();
		}
	}
	
	@Override
	public boolean isRegistered(File file) {
		return this.watchedFiles.contains(file);
	}
	
	@Override
	public List<File> listRegisteredFiles() {
		synchronized (this.watchedFiles) {
			return new ArrayList<File>(this.watchedFiles);
		}
	}
	
	@Override
	public void clear() {
		final Lock writeLock = PollingRepositoryWatchersPool.getInstance().getWriteLock();
		
		writeLock.lock();
		
		try {
			synchronized (this.watchedFiles) {
				this.watchedFiles.clear();
				PollingRepositoryWatchersPool.getInstance().removeWatcher(this);
			}
		} finally {
			writeLock.unlock();
		}
	}

	protected void fileCreated(final File file) {
		this.watchedFiles.add(file);
		this.fireFileCreated(file);
	}

	protected void fileDeleted(final File file) {
		this.watchedFiles.remove(file);
		this.fireFileDeleted(file);
	}
}
