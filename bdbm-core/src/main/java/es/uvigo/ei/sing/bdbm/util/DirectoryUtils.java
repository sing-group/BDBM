/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

package es.uvigo.ei.sing.bdbm.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class DirectoryUtils {
	private DirectoryUtils() {}
	
	public static String getAbsolutePath(File parent, String child) {
		if (parent == null) {
			return child;
		} else {
			return new File(parent, child).getAbsolutePath();
		}
	}
	
	public static void deleteIfExists(File directory) throws IOException {
		deleteIfExists(directory.toPath());
	}
	
	public static void deleteIfExists(Path directory) throws IOException {
		if (Files.exists(directory)) {
			if (!Files.isDirectory(directory)) {
				throw new IllegalArgumentException("input path must be a directory");
			} else {
				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,	BasicFileAttributes attrs) 
					throws IOException {
						Files.delete(file);
						
						return FileVisitResult.CONTINUE;
					}
					
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
						Files.delete(dir);
						
						return FileVisitResult.CONTINUE;
					}
				});
			}
		}
	}
}
