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

package es.uvigo.ei.sing.bdbm.environment.binaries;

import static es.uvigo.ei.sing.bdbm.util.DirectoryUtils.getAbsolutePath;

import java.io.File;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.EMBOSSEnvironmentFactory;

public class DefaultEMBOSSBinaries implements EMBOSSBinaries {
	private File baseDirectory;
	private String getORF;
	private String revseq;
	
	public DefaultEMBOSSBinaries() {
		this.setBaseDirectory((File) null);
	}
	
	public DefaultEMBOSSBinaries(String baseDirectoryPath) {
		this.setBaseDirectory(baseDirectoryPath);
	}
	
	public DefaultEMBOSSBinaries(File baseDirectory) {
		this.setBaseDirectory(baseDirectory);
	}
	
	public DefaultEMBOSSBinaries(
		File baseDirectory, String getORF, String revseq
	) {
		this.baseDirectory = baseDirectory;
		this.getORF = getORF;
		this.revseq = revseq;
	}
	
	public void setBaseDirectory(String path) {
		this.setBaseDirectory(path == null || path.isEmpty() ? null : new File(path));
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		this.getORF = getAbsolutePath(this.baseDirectory, defaultGetORF());
		this.revseq = getAbsolutePath(this.baseDirectory, defaultRevseq());
	}

	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	public void setGetORF(String getORF) {
		this.getORF = getORF;
	}

	@Override
	public String getGetORF() {
		return this.getORF;
	}
	
	public void setRevseq(String revseq) {
		this.revseq = revseq;
	}

	@Override
	public String getRevseq() {
		return this.revseq;
	}
	
	private static String defaultGetORF() {
		return EMBOSSEnvironmentFactory.createEnvironment().getDefaultGetORF();
	}
	
	private static String defaultRevseq() {
		return EMBOSSEnvironmentFactory.createEnvironment().getDefaultRevseq();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(EMBOSSBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(EMBOSSBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(EMBOSSBinaries.GETORF_PROP)) {
			this.setGetORF(props.get(EMBOSSBinaries.GETORF_PROP));
		}
		
		if (props.containsKey(EMBOSSBinaries.REVSEQ_PROP)) {
			this.setRevseq(props.get(EMBOSSBinaries.REVSEQ_PROP));
		}
	}
}
