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
package es.uvigo.ei.sing.bdbm.fasta.naming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class RefSeq2NameSummarizer 
extends AbstractStandardNameSummarizer {
	@Override
	public String getPrefix() {
		return "ref";
	}
	
	@Override
	public String getDescription() {
		return "RefSeq 2";
	}
	
	@Override
	protected Set<Integer> getOptionalIndexes() {
		return new HashSet<Integer>(Arrays.asList(0, 1));
	}
	
	@Override
	protected int getNumOfParts() {
		return 2;
	}
	
	@Override
	protected int[] getSelectedIndexes() {
		return new int[0]; // Unused
	}
	
	@Override
	protected int[][] getSelectedIndexesAlternatives() {
		return new int[][] {
			{ 0 }, { 1 }
		};
	}
}
