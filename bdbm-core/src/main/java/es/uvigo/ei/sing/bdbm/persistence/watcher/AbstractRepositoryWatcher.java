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
package es.uvigo.ei.sing.bdbm.persistence.watcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherListener;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherEvent.Type;

public abstract class AbstractRepositoryWatcher implements RepositoryWatcher {
	protected final List<RepositoryWatcherListener> listeners;
	
	public AbstractRepositoryWatcher() {
		this.listeners = Collections.synchronizedList(
			new LinkedList<RepositoryWatcherListener>()
		);
	}
	
	@Override
	public void addRepositoryWatcherListener(RepositoryWatcherListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public boolean removeRepositoryWatcherListener(RepositoryWatcherListener listener) {
		return this.listeners.remove(listener);
	}

	@Override
	public boolean containsRepositoryWatcherListener(RepositoryWatcherListener listener) {
		return this.listeners.contains(listener);
	}

	protected void fireFileCreated(File file) {
		fireEvent(file, RepositoryWatcherEvent.Type.CREATE);
	}

	protected void fireFileDeleted(File file) {
		fireEvent(file, RepositoryWatcherEvent.Type.DELETE);
	}

	protected void fireEvent(File file, final Type type) {
		final List<RepositoryWatcherListener> listeners = 
			new ArrayList<RepositoryWatcherListener>(this.listeners);
		
		for (RepositoryWatcherListener listener : listeners) {
			listener.repositoryChanged(new RepositoryWatcherEvent(file, type));
		}
	}
}