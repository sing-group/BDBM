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

import es.uvigo.ei.sing.bdbm.environment.CompartEnvironmentFactory;

public class DefaultCompartBinaries implements CompartBinaries {
	private File baseDirectory;
	private String compart;
	
	public DefaultCompartBinaries() {
		this.setBaseDirectory((File) null);
	}
	
	public DefaultCompartBinaries(File baseDirectory) {
		this.setBaseDirectory(baseDirectory);
	}
	
	public DefaultCompartBinaries(String baseDirectoryPath) {
		this.setBaseDirectory(baseDirectoryPath);
	}
	
	public DefaultCompartBinaries(File baseDirectory, String compart) {
		this.baseDirectory = baseDirectory;
		this.compart = compart;
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
		
		this.compart = getAbsolutePath(this.baseDirectory, defaultCompart());
	}

	@Override
	public String getCompart() {
		return this.compart;
	}
	
	public void setCompart(String compart) {
		this.compart = compart;
	}
	
	private static String defaultCompart() {
		return CompartEnvironmentFactory.createEnvironment().getDefaultCompart();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(CompartBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(CompartBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(CompartBinaries.COMPART_PROP)) {
			this.setCompart(props.get(CompartBinaries.COMPART_PROP));
		}
	}
}
