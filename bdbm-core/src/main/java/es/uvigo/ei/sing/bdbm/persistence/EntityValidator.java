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
package es.uvigo.ei.sing.bdbm.persistence;

import java.util.List;

import es.uvigo.ei.sing.bdbm.persistence.EntityValidationException;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;

abstract class EntityValidator<T> {
	public abstract void validate(T entity)
	throws EntityValidationException;
	
	public static EntityValidator<Fasta> fasta() {
		return new EntityValidator<Fasta>() {
			@Override
			public void validate(Fasta fasta)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.fasta.validate", "true"))) {
					if (fasta.getFile().isFile()) {
						try {
							if (!fasta.getType().isValidFastaFile(fasta.getFile())) {
								throw new EntityValidationException("Invalid fasta file: " + fasta.getFile(), fasta);
							}
						} catch (IllegalArgumentException e) {
							throw new EntityValidationException("Unable to load Fasta file: " + fasta.getFile(), e, fasta);
						}
					} else {
						throw new EntityValidationException("Fasta file doesn't exists or isn't a file: "  + fasta.getFile());
					}
				}
			}
		};
	}
	
	public static EntityValidator<Database> db() {
		return new EntityValidator<Database>() {
			protected boolean suffixValidation(Database database) {
				return AbstractDatabase.isAggregatedDatabase(database) ||
						AbstractDatabase.isNormalDatabase(database);
			}
			
			@Override
			public void validate(Database database)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.database.validate", "true"))) {
					if (database.getDirectory().isDirectory()) {
						switch (database.getType()) {
						case NUCLEOTIDE:
							if (!this.suffixValidation(database))
								throw new EntityValidationException("Missing database files in " + database.getName(), database);
							break;
						case PROTEIN:
							if (!this.suffixValidation(database))
								throw new EntityValidationException("Missing database files in " + database.getName(), database);
							break;
						default:
							throw new IllegalStateException("Unknown database type");
						}
					} else {
						throw new EntityValidationException("Database file doesn't exists or isn't a directory: " + database.getDirectory(), database);
					}
				}
			}
		};
	}

	//TODO: Improve directory validation
	public static EntityValidator<SearchEntry> searchEntry() {
		return new EntityValidator<SearchEntry>() {
			@Override
			public void validate(SearchEntry searchEntry)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.searchentry.validate", "true"))) {
					if (searchEntry.getDirectory().isDirectory()) {
						final List<? extends Query> queries = searchEntry.listQueries();
						
						if (queries.isEmpty()) {
							throw new EntityValidationException("Search entry file hasn't any query: " + searchEntry.getDirectory(), searchEntry);
						} else {
							for (Query query : queries) {
								if (query.getBaseFile().isFile()) {
									return;
								}
							}
							
							throw new EntityValidationException("Search entry file hasn't any valid query: " + searchEntry.getDirectory(), searchEntry);
						}
					} else {
						throw new EntityValidationException("Search entry file doesn't exists or isn't a directory: " + searchEntry.getDirectory(), searchEntry);
					}
				}
			}
		};
	}

	//TODO: Improve directory validation
	public static EntityValidator<Export> export() {
		return new EntityValidator<Export>() {
			@Override
			public void validate(Export export)
			throws EntityValidationException {
				if (Boolean.valueOf(System.getProperty("entities.export.validate", "true"))) {
					if (export.getDirectory().isDirectory()) {
						final List<? extends ExportEntry> entries = export.listEntries();
						
						if (entries.isEmpty()) {
							throw new EntityValidationException("Export file hasn't any entry: " + export.getDirectory(), export);
						} else {
							for (ExportEntry entry : entries) {
								if (entry.getOutFile().isFile()/* && entry.getSequenceFiles().length > 0*/) {
									return;
								}
							}
							
							throw new EntityValidationException("Export file hasn't any valid entry.");
						}
					} else {
						throw new EntityValidationException("Export file doesn't exists or isn't a directory: " + export.getDirectory(), export);
					}
				}
			}
		};
	}
}
