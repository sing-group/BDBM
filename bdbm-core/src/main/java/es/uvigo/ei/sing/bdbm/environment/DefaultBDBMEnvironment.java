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

package es.uvigo.ei.sing.bdbm.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.BedToolsBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.CompartBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.DefaultBLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.DefaultBedToolsBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.DefaultCompartBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.DefaultEMBOSSBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.DefaultSplignBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.EMBOSSBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.SplignBinaries;
import es.uvigo.ei.sing.bdbm.environment.paths.DefaultRepositoryPaths;
import es.uvigo.ei.sing.bdbm.environment.paths.RepositoryPaths;

public class DefaultBDBMEnvironment implements BDBMEnvironment {
	private final DefaultRepositoryPaths repositoryPaths;
	private final DefaultBLASTBinaries blastBinaries;
	private final DefaultEMBOSSBinaries embossBinaries;
	private final DefaultBedToolsBinaries bedToolsBinaries;
	private final DefaultSplignBinaries splignBinaries;
	private final DefaultCompartBinaries compartBinaries;
	
	private final File propertiesFile;
	private final Properties defaultProperties;
	
	public DefaultBDBMEnvironment() {
		this.blastBinaries = this.createBLASTBinaries(null);
		this.embossBinaries = this.createEMBOSSBinaries(null);
		this.bedToolsBinaries = this.createBedToolsBinaries(null);
		this.splignBinaries = this.createSplignBinaries(null);
		this.compartBinaries = this.createCompartBinaries(null);
		this.repositoryPaths = new DefaultRepositoryPaths(new File("."));
		
		this.propertiesFile = null;
		this.defaultProperties = new Properties();
	}
	
	public DefaultBDBMEnvironment(File propertiesFile)
	throws FileNotFoundException, IOException, IllegalStateException {
		this.propertiesFile = propertiesFile;
		this.defaultProperties = new Properties();
		
		if (this.propertiesFile != null) {
			this.defaultProperties.load(new FileReader(this.propertiesFile));
		}
		
		for (String property : new String[] {
			RepositoryPaths.BASE_DIRECTORY_PROP,
			BLASTBinaries.BASE_DIRECTORY_PROP,
			EMBOSSBinaries.BASE_DIRECTORY_PROP,
			BedToolsBinaries.BASE_DIRECTORY_PROP
		}) {
			if (!this.hasProperty(property)) {
				throw new IllegalStateException(
					"Missing property in configuration file: " + property
				);
			}
		}
		
		this.repositoryPaths = new DefaultRepositoryPaths(
			new File(this.getProperty(RepositoryPaths.BASE_DIRECTORY_PROP))
		);
		this.blastBinaries = new DefaultBLASTBinaries(
			this.getProperty(BLASTBinaries.BASE_DIRECTORY_PROP),
			defaultPropertiesFor(BLASTBinaries.BLAST_PREFIX)
		);
		this.embossBinaries = new DefaultEMBOSSBinaries(
			this.getProperty(EMBOSSBinaries.BASE_DIRECTORY_PROP)
		);
		this.bedToolsBinaries = new DefaultBedToolsBinaries(
			this.getProperty(BedToolsBinaries.BASE_DIRECTORY_PROP)
		);
		this.splignBinaries = new DefaultSplignBinaries(
			this.getProperty(SplignBinaries.BASE_DIRECTORY_PROP)
		);
		this.compartBinaries = new DefaultCompartBinaries(
			this.getProperty(CompartBinaries.BASE_DIRECTORY_PROP)
		);
	}
	
	private boolean changeProperty(String propertyName, String propertyValue, boolean persist)
	throws IOException {
		if (propertyValue == null)
			throw new IllegalArgumentException("New value can't be null");
		
		final String currentValue = this.getProperty(propertyName);
		
		if (currentValue == null || !currentValue.equals(propertyValue)) {
			this.defaultProperties.put(propertyName, propertyValue);
			
			if (persist) {
				saveToProperties();
			}
			
			final Map<String, String> propertyMap = 
				Collections.singletonMap(propertyName, propertyValue);
			
			this.blastBinaries.setProperties(propertyMap);
			this.embossBinaries.setProperties(propertyMap);
			this.bedToolsBinaries.setProperties(propertyMap);
			this.repositoryPaths.setProperties(propertyMap);
			
			return true;
		} else {
			return false;
		}
	}

	public void saveToProperties() throws IOException {
		this.defaultProperties.store(
			new FileWriter(this.propertiesFile), 
			"Configuration modified by BDBM"
		);
	}
	
	private Map<String, String> defaultPropertiesFor(String toolPrefix) {
		final Map<String, String> properties = new HashMap<>();
		
		for (Object key : defaultProperties.keySet()) {
			final String keyStr = key.toString();
			final String prefix = toolPrefix + "default.";
			
			if (keyStr.startsWith(prefix)) {
				final String param = keyStr.substring(prefix.length());
				
				if (!param.isEmpty()) {
					properties.put(param, defaultProperties.getProperty(keyStr));
				}
			}
		}
		
		return properties;
	}
	
	@Override
	public String getProperty(String propertyName) {
		String propertyValue = System.getProperty(propertyName);
		
		if (propertyValue == null)
			propertyValue = this.defaultProperties.getProperty(propertyName);
		
		return propertyValue;
	}
	
	@Override
	public boolean hasProperty(String propertyName) {
		return System.getProperty(propertyName) != null ||
			this.defaultProperties.containsKey(propertyName);
	}

	@Override
	public BLASTBinaries getBLASTBinaries() {
		return this.blastBinaries;
	}
	
	@Override
	public EMBOSSBinaries getEMBOSSBinaries() {
		return this.embossBinaries;
	}

	@Override
	public BedToolsBinaries getBedToolsBinaries() {
		return this.bedToolsBinaries;
	}

	@Override
	public SplignBinaries getSplignBinaries() {
		return this.splignBinaries;
	}

	@Override
	public CompartBinaries getCompartBinaries() {
		return this.compartBinaries;
	}

	@Override
	public RepositoryPaths getRepositoryPaths() {
		return this.repositoryPaths;
	}
	
	public boolean initializeRepositoryPaths() throws IOException {
		if (this.repositoryPaths.isValid()) {
			return false;	// Already exists
		} else {
			this.repositoryPaths.buildBaseDirectory(this.repositoryPaths.getBaseDirectory());
			return true;	// Created
		}
	}

	@Override
	public boolean changeRepositoryPath(File repositoryPath) throws IOException {
		return this.changeRepositoryPath(repositoryPath, true);
	}

	@Override
	public boolean changeRepositoryPath(File repositoryPath, boolean persist)
	throws IOException {
		if (this.changeProperty(
			RepositoryPaths.BASE_DIRECTORY_PROP, 
			repositoryPath.getAbsolutePath(),
			persist
		)) {
			this.repositoryPaths.setBaseDirectory(repositoryPath);
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean changeBLASTPath(File blastPath) throws IOException {
		return this.changeBLASTPath(blastPath, true);
	}

	@Override
	public boolean changeBLASTPath(File blastPath, boolean persist)
	throws IOException {
		if (this.changeProperty(
			BLASTBinaries.BASE_DIRECTORY_PROP, 
			blastPath.getAbsolutePath(),
			persist
		)) {
			this.blastBinaries.setBaseDirectory(blastPath);
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean changeEMBOSSPath(File embossPath) throws IOException {
		return this.changeEMBOSSPath(embossPath, true);
	}

	@Override
	public boolean changeEMBOSSPath(File embossPath, boolean persist) throws IOException {
		if (this.changeProperty(
			EMBOSSBinaries.BASE_DIRECTORY_PROP,
			embossPath.getAbsolutePath(),
			persist
		)) {
			this.embossBinaries.setBaseDirectory(embossPath);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean changeBedToolsPath(File blastPath) throws IOException {
		return this.changeBedToolsPath(blastPath, true);
	}

	@Override
	public boolean changeBedToolsPath(File bedToolsPath, boolean persist)
	throws IOException {
		if (this.changeProperty(
			BedToolsBinaries.BASE_DIRECTORY_PROP,
			bedToolsPath.getAbsolutePath(),
			persist
		)) {
			this.bedToolsBinaries.setBaseDirectory(bedToolsPath);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean changeSplignPath(File splignPath) throws IOException {
		return this.changeSplignPath(splignPath, true);
	}

	@Override
	public boolean changeSplignPath(File splignPath, boolean persist)
	throws IOException {
		if (this.changeProperty(
			SplignBinaries.BASE_DIRECTORY_PROP,
			splignPath.getAbsolutePath(),
			persist
		)) {
			this.splignBinaries.setBaseDirectory(splignPath);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean changeCompartPath(File compartPath) throws IOException {
		return this.changeCompartPath(compartPath, true);
	}

	@Override
	public boolean changeCompartPath(File compartPath, boolean persist)
		throws IOException {
		if (this.changeProperty(
			CompartBinaries.BASE_DIRECTORY_PROP,
			compartPath.getAbsolutePath(),
			persist
		)) {
			this.compartBinaries.setBaseDirectory(compartPath);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean changePaths(
		File repositoryPath, File blastPath, File embossPath,
		File bedToolsPath, File splignPath, File compartPath
	) throws IOException {
		return this.changeProperty(RepositoryPaths.BASE_DIRECTORY_PROP, repositoryPath.getAbsolutePath(), true)
			|| this.changeProperty(BLASTBinaries.BASE_DIRECTORY_PROP, blastPath == null ? "" : blastPath.getAbsolutePath(), true)
			|| this.changeProperty(EMBOSSBinaries.BASE_DIRECTORY_PROP, embossPath == null ? "" : embossPath.getAbsolutePath(), true)
			|| this.changeProperty(BedToolsBinaries.BASE_DIRECTORY_PROP, bedToolsPath == null ? "" : bedToolsPath.getAbsolutePath(), true)
			|| this.changeProperty(SplignBinaries.BASE_DIRECTORY_PROP, splignPath == null ? "" : splignPath.getAbsolutePath(), true)
			|| this.changeProperty(CompartBinaries.BASE_DIRECTORY_PROP, compartPath == null ? "" : compartPath.getAbsolutePath(), true);
	}

	@Override
	public boolean isAccessionInferEnabled() {
		return Boolean.parseBoolean(this.getProperty("retrievesearchentry.accession.infer"));
	}

	@Override
	public DefaultBLASTBinaries createBLASTBinaries(String path) {
		return new DefaultBLASTBinaries(path, defaultPropertiesFor(BLASTBinaries.BLAST_PREFIX));
	}

	@Override
	public DefaultEMBOSSBinaries createEMBOSSBinaries(String path) {
		return new DefaultEMBOSSBinaries(path);
	}

	@Override
	public DefaultBedToolsBinaries createBedToolsBinaries(String path) {
		return new DefaultBedToolsBinaries(path);
	}
	
	@Override
	public DefaultSplignBinaries createSplignBinaries(String path) {
		return new DefaultSplignBinaries(path);
	}
	
	@Override
	public DefaultCompartBinaries createCompartBinaries(String path) {
		return new DefaultCompartBinaries(path);
	}
}
