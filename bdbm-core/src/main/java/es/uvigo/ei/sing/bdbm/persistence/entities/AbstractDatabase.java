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
package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;

public abstract class AbstractDatabase extends AbstractSequenceEntity implements Comparable<Database>, Database {
	public static Database newDatabase(SequenceType sequenceType, File directory) {
		switch (sequenceType) {
		case PROTEIN:
			return new DefaultProteinDatabase(directory);
		case NUCLEOTIDE:
			return new DefaultNucleotideDatabase(directory);
		default:
			throw new IllegalStateException("Unknown sequence type");
		}
	}
	
	protected AbstractDatabase(SequenceType type, File directory) {
		super(type, directory);
	}

	@Override
	public File getDirectory() {
		return this.getBaseFile();
	}
	
	@Override
	public boolean isAggregated() {
		return AbstractDatabase.isAggregatedDatabase(this);
	}
	
	@Override
	public boolean isRegular() {
		return AbstractDatabase.isNormalDatabase(this);
	}
	
	@Override
	public List<String> listAccessions() {
		if (this.isRegular()) {
			final String sdFileName = this.getName() + "." + getSequenceTypeChar(this.getType()) + "sd";
			final File sdFile = new File(this.getDirectory(), sdFileName);
			
			try {
				final List<String> accessions = new ArrayList<String>();
				
				for (String line : FileUtils.readLines(sdFile)) {
					if (line.startsWith("lcl|")) {
						break;
					} else {
						final int index = line.indexOf(0x02);
						if (index > 0) {
							accessions.add(line.substring(0, index));
						} else {
							accessions.add(line);
						}
					}
				}
				
				return accessions;
			} catch (IOException e) {
				throw new RuntimeException("Error reading sd file", e);
			}
		} else {
			final String alFileName = this.getName() + "." + getSequenceTypeChar(this.getType()) + "al";
			final File alFile = new File(this.getDirectory(), alFileName);
			
			try {
				final String dbListPrefix = "DBLIST ";
				
				final List<String> lines = FileUtils.readLines(alFile);
				
				final List<String> accessions = new ArrayList<String>();
				for (String line : lines) {
					if (line.startsWith(dbListPrefix)) {
						line = line.trim();
						// DBLIST and initial and final " are removed
						line = line.substring(dbListPrefix.length() + 1, line.length() - 1); 
						
						for (String dbPath : line.split("\"\\s+\"")) {
							final File dbDirectory = new File(dbPath).getParentFile();
							final Database subDatabase = AbstractDatabase.newDatabase(this.getType(), dbDirectory);
							
							accessions.addAll(subDatabase.listAccessions());
						}
						
						break;
					}
				}
				
				return accessions;
			} catch (IOException e) {
				throw new RuntimeException("Error reading sd file", e);
			}
		}
	}
	
	@Override
	public int compareTo(Database o) {
		final int typeCmp = this.getType().compareTo(o.getType());
		
		if (typeCmp == 0) {
			return this.getDirectory().compareTo(o.getDirectory());
		} else {
			return typeCmp;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getDirectory() == null) ? 0 : this.getDirectory().hashCode());
		result = prime * result + ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Database))
			return false;

		Database other = (Database) obj;
		if (this.getDirectory() == null) {
			if (other.getDirectory()!= null)
				return false;
		} else if (!this.getDirectory().getAbsoluteFile().equals(other.getDirectory().getAbsoluteFile()))
			return false;
		if (this.getType() != other.getType())
			return false;
		return true;
	}
//	
//	@Override
//	public String toString() {
//		return "DATABASE: " + super.toString();
//	}
	
	protected static char getSequenceTypeChar(SequenceType type) {
		return type == SequenceType.NUCLEOTIDE ? 'n' : 'p';
	}

	public static boolean isNormalDatabase(Database database) {
		final String[] suffixes = new String[] { "hr", "in", "og", "sd", "si", "sq" };
		final char typeChar = getSequenceTypeChar(database.getType());
		
		for (String suffix : suffixes) {
			final String subFileName = database.getName() + "." + typeChar + suffix;
			
			if (!new File(database.getDirectory(), subFileName).isFile()) {
				return false;
			}
		}
				
		return true;
	}

	public static boolean isAggregatedDatabase(Database database) {
		final char typeChar = getSequenceTypeChar(database.getType());
		final String subFileName = database.getName() + "." + typeChar + "al";
		
		return new File(database.getDirectory(), subFileName).isFile();
	}
}
