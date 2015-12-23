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

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractStandardNameSummarizer
implements StandardNameSummarizer {
	protected final static String PART_PATTERN = "\\|[\\p{Graph}]+";
	protected final static String OPTIONAL_PART_PATTERN = "\\|[\\p{Graph}]*";
	
	protected String separator = "_";
	
	protected abstract int getNumOfParts();
	protected abstract int[] getSelectedIndexes();
	
	protected int[][] getSelectedIndexesAlternatives() {
		return new int[][] { this.getSelectedIndexes() };
	}

	protected Set<Integer> getOptionalIndexes() {
		return Collections.emptySet();
	}
	
	@Override
	public String getSeparator() {
		return separator;
	}
	
	@Override
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	protected String getPattern() {
		final StringBuilder pattern = new StringBuilder("^>" + this.getPrefix());
		final Set<Integer> optionalIndexes = this.getOptionalIndexes();
		
		for (int i = 0; i < this.getNumOfParts(); i++) {
			if (optionalIndexes.contains(i)) {
				pattern.append(OPTIONAL_PART_PATTERN);
			} else {
				pattern.append(PART_PATTERN);
			}
		}
		pattern.append("(\\p{Blank}.*)?$");
		
		return pattern.toString();
	}
	
	@Override
	public boolean recognizes(String name) {
		return Pattern.matches(this.getPattern(), name);
	}
	
	@Override
	public String summarize(String name) {
		if (this.recognizes(name)) {
			name = name.replaceAll("\t", " ");
			if (name.contains(" ")) 
				name = name.substring(0, name.indexOf(' '));
			
			final String[] parts = name.split("[| \t]");
			
			for (int[] partsToReturn : getSelectedIndexesAlternatives()) {
				final StringBuilder sbName = new StringBuilder(">");
				
				for (int partIndex : partsToReturn) {
					final String part = parts[partIndex + 1].trim();
					
					if (!part.isEmpty()) {
						if (sbName.length() > 1) sbName.append(this.getSeparator());
						sbName.append(part);
					}
				}
				
				if (sbName.length() > 1) return sbName.toString();
			}
			
			throw new IllegalArgumentException("Invalid sequence name");
		} else {
			throw new IllegalArgumentException("Invalid sequence name");
		}
	}
}