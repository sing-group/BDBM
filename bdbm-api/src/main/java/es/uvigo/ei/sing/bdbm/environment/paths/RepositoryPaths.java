/*
 * #%L
 * BDBM API
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
package es.uvigo.ei.sing.bdbm.environment.paths;

import java.io.File;
import java.io.IOException;

public interface RepositoryPaths {
	public final static String REPOSITORY_PREFIX = "repository.";
	public final static String DB_PREFIX = "db.";
	public final static String FASTA_PREFIX = "fasta.";
	public final static String SEARCH_ENTRY_PREFIX = "searchEntry.";
	public final static String EXPORT_PREFIX = "export.";
	public final static String PROTEINS_PREFIX = "prot.";
	public final static String NUCLEOTIDES_PREFIX = "nucl.";
	
	public final static String BASE_DIRECTORY_PROP =
		REPOSITORY_PREFIX + "baseDir";
	
	public final static String DB_PROTEINS_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + DB_PREFIX + PROTEINS_PREFIX;
	public final static String DB_NUCLEOTIDES_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + DB_PREFIX + NUCLEOTIDES_PREFIX;
	
	public final static String FASTA_PROTEINS_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + FASTA_PREFIX + PROTEINS_PREFIX;
	public final static String FASTA_NUCLEOTIDES_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + FASTA_PREFIX + NUCLEOTIDES_PREFIX;
	
	public final static String ENTRY_PROTEINS_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + SEARCH_ENTRY_PREFIX + PROTEINS_PREFIX;
	public final static String ENTRY_NUCLEOTIDES_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + SEARCH_ENTRY_PREFIX + NUCLEOTIDES_PREFIX;
	
	public final static String EXPORT_PROTEINS_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + EXPORT_PREFIX + PROTEINS_PREFIX;
	public final static String EXPORT_NUCLEOTIDES_DIRECTORY_PROP = 
		REPOSITORY_PREFIX + EXPORT_PREFIX + NUCLEOTIDES_PREFIX;
	
	public abstract File getBaseDirectory();
	public abstract boolean checkBaseDirectory(File baseDirectory);
	public abstract void buildBaseDirectory(File baseDirectory) 
	throws IOException;
	
	public abstract File getDBProteinsDirectory();
	public abstract File getDBNucleotidesDirectory();
	public abstract File getFastaProteinsDirectory();
	public abstract File getFastaNucleotidesDirectory();
	public abstract File getSearchEntryProteinsDirectory();
	public abstract File getSearchEntryNucleotidesDirectory();
	public abstract File getExportProteinsDirectory();
	public abstract File getExportNucleotidesDirectory();
}