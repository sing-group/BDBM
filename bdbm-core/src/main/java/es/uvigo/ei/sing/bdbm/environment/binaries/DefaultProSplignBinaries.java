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

import es.uvigo.ei.sing.bdbm.environment.ProSplignEnvironmentFactory;

public class DefaultProSplignBinaries implements ProSplignBinaries {
	private File baseDirectory;
	private String prosplign;
	
	public DefaultProSplignBinaries() {
		this.setBaseDirectory((File) null);
	}
	
	public DefaultProSplignBinaries(File baseDirectory) {
		this.setBaseDirectory(baseDirectory);
	}
	
	public DefaultProSplignBinaries(String baseDirectoryPath) {
		this.setBaseDirectory(baseDirectoryPath);
	}
	
	public DefaultProSplignBinaries(File baseDirectory, String prosplign) {
		this.baseDirectory = baseDirectory;
		this.prosplign = prosplign;
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
		
		this.prosplign = getAbsolutePath(this.baseDirectory, defaultProSplign());
	}

	@Override
	public String getProSplign() {
		return this.prosplign;
	}
	
	public void setProSplign(String prosplign) {
		this.prosplign = prosplign;
	}

	private static String defaultProSplign() {
		return ProSplignEnvironmentFactory.createEnvironment().getDefaultProSplign();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(ProSplignBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(ProSplignBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(ProSplignBinaries.SPLIGN_PROP)) {
			this.setProSplign(props.get(ProSplignBinaries.SPLIGN_PROP));
		}
	}
}
