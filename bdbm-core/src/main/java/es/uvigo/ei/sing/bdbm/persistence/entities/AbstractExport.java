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

package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;

public abstract class AbstractExport extends AbstractSequenceEntity implements Export {
	public static Export newExport(SequenceType sequenceType, File file) {
		switch (sequenceType) {
		case PROTEIN:
			return new DefaultProteinExport(file);
		case NUCLEOTIDE:
			return new DefaultNucleotideExport(file);
		default:
			throw new IllegalStateException("Unknown sequence type");
		}
	}
	
	protected AbstractExport(SequenceType type, File file) {
		super(type, file);
	}

	@Override
	public File getFile() {
		return this.getBaseFile();
	}
	
	@Override
	public int compareTo(Export o) {
		return this.getFile().compareTo(o.getFile());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getFile() == null) ? 0 : this.getFile().hashCode());
		result = prime * result + ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Export))
			return false;
		
		Export other = (Export) obj;
		if (this.getFile() == null) {
			if (other.getFile()!= null)
				return false;
		} else if (!this.getFile().equals(other.getFile()))
			return false;
		if (this.getType() != other.getType())
			return false;
		return true;
	}
}
