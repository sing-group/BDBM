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

package es.uvigo.ei.sing.bdbm.fasta;

public class SequenceLengthConfiguration {
	public static SequenceLengthConfiguration buildNoChanges() {
		return new SequenceLengthConfiguration(-1);
	}
	
	public static SequenceLengthConfiguration buildRemoveLineBreaks() {
		return new SequenceLengthConfiguration(0);
	}
	
	public static SequenceLengthConfiguration buildChangeFragmentLength(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length must be a positive number");
		
		return new SequenceLengthConfiguration(length);
	}
	
	
	private final int fragmentLength;
	
	private SequenceLengthConfiguration(int fragmentLength) {
		this.fragmentLength = fragmentLength;
	}
	
	public boolean isNoChange() {
		return this.fragmentLength < 0;
	}
	
	public boolean isRemoveLineBreaks() {
		return this.fragmentLength == 0;
	}
	
	public boolean isChangeFragmentLength() {
		return this.fragmentLength > 0;
	}
	
	public int getFragmentLength() {
		return this.fragmentLength;
	}
}
