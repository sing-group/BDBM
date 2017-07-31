/*-
 * #%L
 * BDBM API
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public enum SequenceType {
	NUCLEOTIDE("Nucleotide", "nucleotides", "nucl", new char[] {
		'A', 'C', 'T', 'G',
		'U', 'R', 'Y', 'K',
		'K', 'M', 'S', 'W',
		'B', 'D', 'H', 'V',
		'N', 'X', '-'
	}),
	PROTEIN("Protein", "proteins", "prot", new char[] {
		'A', 'B', 'C', 'D',
		'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L',
		'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X',
		'Y', 'Z', '*', '-'
	});
	
	private final String name;
	private final String directoryExtension;
	private final String dbType;
	private final char[] validChars;

	private SequenceType(String name, String directoryExtension, String dbType, char[] validChars) {
		this.name = name;
		this.directoryExtension = directoryExtension;
		this.dbType = dbType;
		this.validChars = validChars;
		
		Arrays.sort(this.validChars);
	}
	
	public static SequenceType forDBType(String type) 
	throws IllegalArgumentException {
		for (SequenceType sequenceType : SequenceType.values()) {
			if (sequenceType.getDBType().equals(type)) {
				return sequenceType;
			}
		}
		
		throw new IllegalArgumentException("Unknown db type: " + type);
	}
	
	public String getDirectoryExtension() {
		return directoryExtension;
	}
	
	public String getDBType() {
		return dbType;
	}
	
	public boolean isValidSequence(String sequence) {
		for (char letter : sequence.toCharArray()) {
			if (Arrays.binarySearch(this.validChars, letter) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isValidFastaFile(File fastaFile) throws IllegalArgumentException {
		try (BufferedReader br = new BufferedReader(new FileReader(fastaFile))) {
			String line = null;
			while ((line = br.readLine()) != null && !line.startsWith(">"));
			
			if (line == null) return false;
			
			String sequenceName = line.substring(1);
			String sequence = "";
			while (true) {
				line = br.readLine();
				
				if (line == null || line.startsWith(">")) {
					if (sequence.isEmpty()) {
						throw new IllegalArgumentException("Empty sequence: " + sequenceName);
					} else if (!this.isValidSequence(sequence)) {
						throw new IllegalArgumentException("Sequence contains invalid characters: " + sequenceName);
					}
					
					if (line == null) {
						break;
					} else {
						sequenceName = line.substring(1);
						sequence = "";
					}
				} else {
					sequence += line.replaceAll("\\s", "");
				}
			}
			
			return true;
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Error reading input FASTA file: " + fastaFile.getAbsolutePath(), ioe);
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
