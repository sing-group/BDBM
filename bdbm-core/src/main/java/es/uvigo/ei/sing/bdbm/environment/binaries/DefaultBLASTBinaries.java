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
package es.uvigo.ei.sing.bdbm.environment.binaries;

import static es.uvigo.ei.sing.bdbm.util.DirectoryUtils.getAbsolutePath;
import static java.util.Collections.emptyMap;

import java.io.File;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.BLASTEnvironmentFactory;

public class DefaultBLASTBinaries implements BLASTBinaries {
	private File baseDirectory;
	private String makeBlastDB;
	private String blastDBAliasTool;
	private String blastDBCmd;
	private String blastN;
	private String blastP;
	private String tBlastX;
	private String tBlastN;
	
	private final Map<String, String> configurationParameters;
	
	public DefaultBLASTBinaries() {
		this.setBaseDirectory((File) null);
		this.configurationParameters = emptyMap();
	}
	
	public DefaultBLASTBinaries(File baseDirectory, Map<String, String> configurationParameters) {
		this.setBaseDirectory(baseDirectory);
		this.configurationParameters = configurationParameters;
	}
	
	public DefaultBLASTBinaries(String baseDirectoryPath, Map<String, String> configurationParameters) {
		this.setBaseDirectory(baseDirectoryPath);
		this.configurationParameters = configurationParameters;
	}
	
	public DefaultBLASTBinaries(
		File baseDirectory,
		String makeBlastDB,
		String blastDBAliasTool,
		String blastDBCmd,
		String blastN,
		String blastP,
		String tBlastX,
		String tBlastN,
		Map<String, String> configurationParameters
	) {
		this.baseDirectory = baseDirectory;
		this.makeBlastDB = makeBlastDB;
		this.blastDBAliasTool = blastDBAliasTool;
		this.blastDBCmd = blastDBCmd;
		this.blastN = blastN;
		this.blastP = blastP;
		this.tBlastX = tBlastX;
		this.tBlastN = tBlastN;
		this.configurationParameters = configurationParameters;
	}
	
	public void setBaseDirectory(String path) {
		this.setBaseDirectory(path == null || path.isEmpty() ? null : new File(path));
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		this.makeBlastDB = getAbsolutePath(this.baseDirectory, defaultMakeBlastDB());
		this.blastDBAliasTool = getAbsolutePath(this.baseDirectory, defaultBlastDBAliasTool());
		this.blastDBCmd = getAbsolutePath(this.baseDirectory, defaultBlastDBCmd());
		this.blastN = getAbsolutePath(this.baseDirectory, defaultBlastN());
		this.blastP = getAbsolutePath(this.baseDirectory, defaultBlastP());
		this.tBlastX = getAbsolutePath(this.baseDirectory, defaultTBlastX());
		this.tBlastN = getAbsolutePath(this.baseDirectory, defaultTBlastN());
	}
	
	@Override
	public Map<String, String> getConfigurationParameters() {
		return this.configurationParameters;
	}

	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	@Override
	public String getMakeBlastDB() {
		return this.makeBlastDB;
	}

	@Override
	public String getBlastDBAliasTool() {
		return this.blastDBAliasTool;
	}

	@Override
	public String getBlastDBCmd() {
		return this.blastDBCmd;
	}

	@Override
	public String getBlastN() {
		return this.blastN;
	}
	
	@Override
	public String getBlastP() {
		return this.blastP;
	}

	@Override
	public String getTBlastX() {
		return this.tBlastX;
	}

	@Override
	public String getBlast(BLASTType blastType) {
		return blastType.getBinary(this);
	}

	@Override
	public String getTBlastN() {
		return this.tBlastN;
	}

	public void setTBlastX(String tBlastX) {
		this.tBlastX = tBlastX;
	}

	public void setTBlastN(String tBlastN) {
		this.tBlastN = tBlastN;
	}

	public void setMakeBlastDB(String makeBlastDB) {
		this.makeBlastDB = makeBlastDB;
	}

	public void setBlastDBAliasTool(String blastDBAliasTool) {
		this.blastDBAliasTool = blastDBAliasTool;
	}

	public void setBlastDBCmd(String blastDBCmd) {
		this.blastDBCmd = blastDBCmd;
	}

	public void setBlastN(String blastN) {
		this.blastN = blastN;
	}
	
	private static String defaultMakeBlastDB() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultMakeBlastDB();
	}
	
	private static String defaultBlastDBAliasTool() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastDBAliasTool();
	}

	private static String defaultBlastDBCmd() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastDBCmd();
	}
	
	private static String defaultBlastN() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastN();
	}
	
	private static String defaultBlastP() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultBlastP();
	}
	
	private static String defaultTBlastX() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultTBlastX();
	}
	
	private static String defaultTBlastN() {
		return BLASTEnvironmentFactory.createEnvironment().getDefaultTBlastN();
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(BLASTBinaries.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(props.get(BLASTBinaries.BASE_DIRECTORY_PROP));
		}
		
		if (props.containsKey(BLASTBinaries.MAKE_BLAST_DB_PROP)) {
			this.setMakeBlastDB(props.get(BLASTBinaries.MAKE_BLAST_DB_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLASTDB_ALIASTOOL_PROP)) {
			this.setBlastDBAliasTool(props.get(BLASTBinaries.BLASTDB_ALIASTOOL_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLASTDB_CMD_PROP)) {
			this.setBlastDBCmd(props.get(BLASTBinaries.BLASTDB_CMD_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLAST_N_PROP)) {
			this.setBlastN(props.get(BLASTBinaries.BLAST_N_PROP));
		}
		if (props.containsKey(BLASTBinaries.BLAST_P_PROP)) {
			this.setBlastN(props.get(BLASTBinaries.BLAST_P_PROP));
		}
		if (props.containsKey(BLASTBinaries.T_BLAST_X_PROP)) {
			this.setTBlastX(props.get(BLASTBinaries.T_BLAST_X_PROP));
		}
		if (props.containsKey(BLASTBinaries.T_BLAST_N_PROP)) {
			this.setTBlastN(props.get(BLASTBinaries.T_BLAST_N_PROP));
		}
	}
}
