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
package es.uvigo.ei.sing.bdbm.environment.binaries;

import static es.uvigo.ei.sing.bdbm.util.DirectoryUtils.getAbsolutePath;

import java.io.File;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.SplignEnvironmentFactory;

public class DefaultSplignBinaries implements SplignBinaries {
	private File baseDirectory;
	private String splign;
	
	public DefaultSplignBinaries() {
		this.setBaseDirectory((File) null);
	}
	
	public DefaultSplignBinaries(File baseDirectory) {
		this.setBaseDirectory(baseDirectory);
	}
	
	public DefaultSplignBinaries(String baseDirectoryPath) {
		this.setBaseDirectory(baseDirectoryPath);
	}
	
	public DefaultSplignBinaries(File baseDirectory, String splign) {
		this.baseDirectory = baseDirectory;
		this.splign = splign;
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
		
		this.splign = getAbsolutePath(this.baseDirectory, defaultSplign());
	}

	@Override
	public String getSplign() {
		return this.splign;
	}
	
	public void setSplign(String splign) {
		this.splign = splign;
	}

	private static String defaultSplign() {
		return SplignEnvironmentFactory.createEnvironment().getDefaultSplign();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(SplignBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(SplignBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(SplignBinaries.SPLIGN_PROP)) {
			this.setSplign(props.get(SplignBinaries.SPLIGN_PROP));
		}
	}
}
