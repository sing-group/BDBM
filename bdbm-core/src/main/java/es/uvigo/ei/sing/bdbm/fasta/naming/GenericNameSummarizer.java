/*
 * #%L
 * BDBM Core
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
package es.uvigo.ei.sing.bdbm.fasta.naming;

import java.util.regex.Pattern;

public class GenericNameSummarizer
implements NameSummarizer {
	protected final static String NAME_PATTERN = "^>\\p{Graph}+(\\|\\p{Graph}*)+(\\p{Blank}.*)?$";

	private int[] selectedIndexes;
	
	protected String separator = "_";
	
	public GenericNameSummarizer() {
		this(0);
	}
	
	public GenericNameSummarizer(int ... selectedIndexes) {
		this.selectedIndexes = selectedIndexes;
	}
	
	public String getSeparator() {
		return separator;
	}
	
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public int[] getSelectedIndexes() {
		return selectedIndexes;
	}
	
	public void setSelectedIndexes(int[] selectedIndexes) {
		this.selectedIndexes = selectedIndexes;
	}
	
	@Override
	public boolean recognizes(String name) {
		return Pattern.matches(NAME_PATTERN, name);
	}
	
	@Override
	public String summarize(String name) {
		if (this.recognizes(name)) {
			name = name.replaceAll("\t", " ");
			if (name.contains(" ")) 
				name = name.substring(0, name.indexOf(' '));
			
			final String[] parts = name.split("[| \t]");
			
			final StringBuilder sbName = new StringBuilder(">");
			for (int partIndex : getSelectedIndexes()) {
				if (partIndex <= 0 || partIndex >= parts.length)
					throw new ArrayIndexOutOfBoundsException(
						"Invalid index " + partIndex + ". Index must be in the range [1, " + (parts.length-1) + "]");
				
				final String part = parts[partIndex].trim();
				
				if (!part.isEmpty()) {
					if (sbName.length() > 1) sbName.append(this.separator);
					sbName.append(part);
				}
			}
			
			if (sbName.length() > 1) return sbName.toString();
			else throw new IllegalArgumentException("Invalid sequence name");
		} else {
			throw new IllegalArgumentException("Invalid sequence name: " + name);
		}
	}
}