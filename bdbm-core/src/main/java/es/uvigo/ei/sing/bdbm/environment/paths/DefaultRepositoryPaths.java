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
package es.uvigo.ei.sing.bdbm.environment.paths;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;


public class DefaultRepositoryPaths implements RepositoryPaths {
	public static final String DB_DIRECTORY_NAME = "db";
	public static final String FASTA_DIRECTORY_NAME = "fasta";
	public static final String ENTRY_DIRECTORY_NAME = "entry";
	public static final String EXPORT_DIRECTORY_NAME = "export";
	
	private File baseDirectory;
	private final Map<SequenceType, File> dbDirectories;
	private final Map<SequenceType, File> fastaDirectories;
	private final Map<SequenceType, File> entryDirectories;
	private final Map<SequenceType, File> exportDirectories;
	private final Map<SequenceType, File> orfDirectories;
	
	public DefaultRepositoryPaths(File baseDirectory) 
	throws IllegalArgumentException {
		if (baseDirectory == null)
			throw new IllegalArgumentException("baseDirectory can't be null");
		
		this.baseDirectory = baseDirectory;
		
		this.dbDirectories = new HashMap<>();
		this.fastaDirectories = new HashMap<>();
		this.entryDirectories = new HashMap<>();
		this.exportDirectories = new HashMap<>();
		this.orfDirectories = new HashMap<>();
		
		this.updateDefaultDirectories();
	}

	protected void updateDefaultDirectories() {
		this.dbDirectories.clear();
		this.fastaDirectories.clear();
		this.entryDirectories.clear();
		this.exportDirectories.clear();
		this.orfDirectories.clear();
		
		for (SequenceType sequence : SequenceType.values()) {
			this.dbDirectories.put(sequence, this.defaultDBDirectory(sequence));
			this.fastaDirectories.put(sequence, this.defaultFastaDirectory(sequence));
			this.entryDirectories.put(sequence, this.defaultEntryDirectory(sequence));
			this.exportDirectories.put(sequence, this.defaultExportDirectory(sequence));
		}
	}
	
	protected File defaultDirectory(String dirName, SequenceType sequenceType) {
		return new File(
			new File(this.getBaseDirectory(), dirName),
			sequenceType.getDirectoryExtension()
		);
	}
	
	protected File defaultDBDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.DB_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultFastaDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.FASTA_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultEntryDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.ENTRY_DIRECTORY_NAME, sequenceType);
	}
	
	protected File defaultExportDirectory(SequenceType sequenceType) {
		return this.defaultDirectory(DefaultRepositoryPaths.EXPORT_DIRECTORY_NAME, sequenceType);
	}
	
	@Override
	public File getBaseDirectory() {
		return this.baseDirectory;
	}
	
	@Override
	public boolean checkBaseDirectory(File baseDirectory) {
		return baseDirectory.isDirectory() &&
			checkRepositoryPaths(new DefaultRepositoryPaths(baseDirectory));
	}
	
	@Override
	public boolean isValid() {
		return checkRepositoryPaths(this);
	}
	
	private static boolean checkRepositoryPaths(DefaultRepositoryPaths paths) {
		return paths.getDBNucleotidesDirectory().isDirectory() &&
			paths.getDBProteinsDirectory().isDirectory() &&
			paths.getFastaNucleotidesDirectory().isDirectory() &&
			paths.getFastaProteinsDirectory().isDirectory() &&
			paths.getSearchEntryNucleotidesDirectory().isDirectory() &&
			paths.getSearchEntryProteinsDirectory().isDirectory() &&
			paths.getExportNucleotidesDirectory().isDirectory() &&
			paths.getExportProteinsDirectory().isDirectory();
	}
	
	@Override
	public void buildBaseDirectory(File baseDirectory) 
	throws IOException {
		if (!baseDirectory.isDirectory() && !baseDirectory.mkdirs()) {
			throw new IOException("Base directory " + baseDirectory + " could not be created");
		} else {
			final DefaultRepositoryPaths stub = 
				new DefaultRepositoryPaths(baseDirectory);
			
			final File[] subdirectories = new File[] {
				stub.getDBNucleotidesDirectory(),
				stub.getDBProteinsDirectory(),
				stub.getFastaNucleotidesDirectory(),
				stub.getFastaProteinsDirectory(),
				stub.getSearchEntryNucleotidesDirectory(),
				stub.getSearchEntryProteinsDirectory(),
				stub.getExportNucleotidesDirectory(),
				stub.getExportProteinsDirectory()
			};
			
			for (File subdirectory : subdirectories) {
				if (!subdirectory.isDirectory() && !subdirectory.mkdirs()) {
					throw new IOException("Directory " + subdirectory + " could not be created");
				}
			}
		}
	}
	
	public void setBaseDirectory(File baseDirectory) {
		this.baseDirectory = baseDirectory;
		
		this.updateDefaultDirectories();
	}
	
	@Override
	public File getDBProteinsDirectory() {
		return this.dbDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setDBProteinsDirectory(File directory) {
		this.dbDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getDBNucleotidesDirectory() {
		return this.dbDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setDBNucleotidesDirectory(File directory) {
		this.dbDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getFastaProteinsDirectory() {
		return this.fastaDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setFastaProteinsDirectory(File directory) {
		this.fastaDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getFastaNucleotidesDirectory() {
		return this.fastaDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setFastaNucleotidesDirectory(File directory) {
		this.fastaDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getSearchEntryProteinsDirectory() {
		return this.entryDirectories.get(SequenceType.PROTEIN);
	}
	
	public void setEntryProteinsDirectory(File directory) {
		this.entryDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getSearchEntryNucleotidesDirectory() {
		return this.entryDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setEntryNucleotidesDirectory(File directory) {
		this.entryDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	@Override
	public File getExportProteinsDirectory() {
		return this.exportDirectories.get(SequenceType.PROTEIN);
	}

	public void setExportProteinsDirectory(File directory) {
		this.exportDirectories.put(SequenceType.PROTEIN, directory);
	}
	
	@Override
	public File getExportNucleotidesDirectory() {
		return this.exportDirectories.get(SequenceType.NUCLEOTIDE);
	}
	
	public void setExportNucleotidesDirectory(File directory) {
		this.exportDirectories.put(SequenceType.NUCLEOTIDE, directory);
	}
	
	public void setProperties(Map<String, String> props) {
		if (props.containsKey(RepositoryPaths.BASE_DIRECTORY_PROP)) {
			this.setBaseDirectory(
				new File(props.get(RepositoryPaths.BASE_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.DB_PROTEINS_DIRECTORY_PROP)) {
			this.setDBProteinsDirectory(
				new File(props.get(RepositoryPaths.DB_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.DB_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setDBNucleotidesDirectory(
				new File(props.get(RepositoryPaths.DB_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.FASTA_PROTEINS_DIRECTORY_PROP)) {
			this.setFastaProteinsDirectory(
				new File(props.get(RepositoryPaths.FASTA_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.FASTA_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setFastaNucleotidesDirectory(
				new File(props.get(RepositoryPaths.FASTA_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.ENTRY_PROTEINS_DIRECTORY_PROP)) {
			this.setEntryProteinsDirectory(
				new File(props.get(RepositoryPaths.ENTRY_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.ENTRY_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setEntryNucleotidesDirectory(
				new File(props.get(RepositoryPaths.ENTRY_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.EXPORT_PROTEINS_DIRECTORY_PROP)) {
			this.setExportProteinsDirectory(
				new File(props.get(RepositoryPaths.EXPORT_PROTEINS_DIRECTORY_PROP))
			);
		}
		if (props.containsKey(RepositoryPaths.EXPORT_NUCLEOTIDES_DIRECTORY_PROP)) {
			this.setExportNucleotidesDirectory(
				new File(props.get(RepositoryPaths.EXPORT_NUCLEOTIDES_DIRECTORY_PROP))
			);
		}
	}
}
