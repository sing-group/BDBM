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

package es.uvigo.ei.sing.bdbm.fasta.naming;

public enum FastaSequenceRenameMode {
	NONE("None"),
	KNOWN_SEQUENCE_NAMES("Known sequence names"),
	MULTIPART_NAME("Multipart name"),
	PREFIX("Add prefix");
	
	private final String descriptiveName;
	
	private FastaSequenceRenameMode(String descriptiveName) {
		this.descriptiveName = descriptiveName;
	}
	
	@Override
	public String toString() {
		return this.descriptiveName;
	}
}
