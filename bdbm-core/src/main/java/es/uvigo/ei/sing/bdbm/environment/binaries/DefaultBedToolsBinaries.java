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

package es.uvigo.ei.sing.bdbm.environment.binaries;

import static es.uvigo.ei.sing.bdbm.util.DirectoryUtils.getAbsolutePath;

import java.io.File;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.BedToolsEnvironmentFactory;

public class DefaultBedToolsBinaries implements BedToolsBinaries {
	private File baseDirectory;
	private String bedtools;
	
	public DefaultBedToolsBinaries() {
		this.setBaseDirectory((File) null);
	}
	
	public DefaultBedToolsBinaries(File baseDirectory) {
		this.setBaseDirectory(baseDirectory);
	}
	
	public DefaultBedToolsBinaries(String baseDirectoryPath) {
		this.setBaseDirectory(baseDirectoryPath);
	}
	
	public DefaultBedToolsBinaries(File baseDirectory, String bedtools) {
		this.baseDirectory = baseDirectory;
		this.bedtools = bedtools;
	}

	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	public void setBaseDirectory(String path) {
		this.setBaseDirectory(path == null || path.isEmpty() ? null : new File(path));
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		this.bedtools = getAbsolutePath(this.baseDirectory, defaultBedtools());
	}

	@Override
	public String getBedtools() {
		return this.bedtools;
	}
	
	public void setBedtools(String bedtools) {
		this.bedtools = bedtools;
	}
	
	private static String defaultBedtools() {
		return BedToolsEnvironmentFactory.createEnvironment().getDefaultBedtools();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(BedToolsBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(BedToolsBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(BedToolsBinaries.BEDTOOLS_PROP)) {
			this.setBedtools(props.get(BedToolsBinaries.BEDTOOLS_PROP));
		}
	}
}
