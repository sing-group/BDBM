/*
 * #%L
 * BDBM GUI
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
package es.uvigo.ei.sing.bdbm.gui.repository;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileChangeWatcherRunnable implements Runnable {
	private final static Logger LOG = LoggerFactory.getLogger(FileChangeWatcherRunnable.class);
	
	protected final Path file;
	
	public FileChangeWatcherRunnable(File file) 
	throws IllegalArgumentException {
		this(file.toPath());
	}
	
	public FileChangeWatcherRunnable(Path file) 
	throws IllegalArgumentException {
		if (!file.toFile().isFile())
			throw new IllegalArgumentException("Watched file must be a regular file");
		
		this.file = file;
	}
	
	public static Thread watchFile(final File file, final Runnable changeCallback) {
		return new Thread(new FileChangeWatcherRunnable(file) {
			@Override
			protected void fileChanged() {
				changeCallback.run();
			}
		});
	}
	
	protected abstract void fileChanged();

	@Override
	public void run() {
		try {
			final WatchService watchService = FileSystems.getDefault().newWatchService();
			this.file.getParent().register(
				watchService, StandardWatchEventKinds.ENTRY_MODIFY
			);
			
			while (true) {
			    final WatchKey wk = watchService.take();
			    for (WatchEvent<?> event : wk.pollEvents()) {
			        //we only register "ENTRY_MODIFY" so the context is always a Path.
			        final Path changed = (Path) event.context();
			        
			        if (changed.getFileName().equals(this.file.getFileName())) {
			        	this.fileChanged();
			        }
			    }
			    
			    wk.reset();
			}
		} catch (Exception e) {
			LOG.error("WatchService error", e);
		}
	}
}
