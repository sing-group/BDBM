/*-
 * #%L
 * BDBM GUI
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

package es.uvigo.ei.sing.bdbm.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public final class FileWatcher {
	private FileWatcher() {}

	public static interface Watcher {
		public boolean fileModified();
	}
	
	public static Thread watchFile(final File file, final Watcher watcher) {
		if (!file.isFile())
			throw new IllegalArgumentException("Only regular files are allowed");
		if (!file.canRead())
			throw new IllegalArgumentException("File must be readable");
		
		final Path path = file.toPath().toAbsolutePath();
		try {
			final WatchService service = FileSystems.getDefault().newWatchService();
			path.getParent().register(service, StandardWatchEventKinds.ENTRY_MODIFY);
			
			final Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// Comparing last modified timestamp avoid duplicated events
					long lastModified = file.lastModified();
					try {
						mainloop: while (true) {
							final WatchKey key = service.take();
							for (WatchEvent<?> event : key.pollEvents()) {
								final Path changed = (Path) event.context();
								
								if (changed.getFileName().equals(path.getFileName())
									&& lastModified != file.lastModified()
								) {
									if (!watcher.fileModified()) {
										break mainloop;
									}
									
									lastModified = file.lastModified();
									break;
								}
							}
							
							if (!key.reset())
								break mainloop;
						}
					} catch (InterruptedException e) {}
				}
			});
			
			thread.start();
			
			return thread;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
