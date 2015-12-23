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


public class ComposedGenInfoNameSummarizer
implements NameSummarizer {
	protected final static String NAME_PATTERN = "^gi|\\p{Alnum}+|(\\|\\p{Alnum}*)+(\\p{Space}.*)?$";
	
	private int[] selectedIndexes = new int[] { 0 };
	
	public int[] getSelectedIndexes() {
		return selectedIndexes;
	}
	
	public void setSelectedIndexes(int[] selectedIndexes) {
		this.selectedIndexes = selectedIndexes;
	}
	
	@Override
	public boolean recognizes(String name) {
		if (name.startsWith("gi|") && name.length() > 3) {
			final int barIndex = name.indexOf('|', 3);
			if (barIndex != -1) {
				final String subname = name.substring(barIndex + 1);
				
				return NameSummarizerFactory.createNameSummarizer(subname) != null;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String summarize(String name) {
		if (this.recognizes(name)) {
			final int barIndex = name.indexOf('|', 3);
			final String subname = name.substring(barIndex + 1);
			final NameSummarizer summarizer = NameSummarizerFactory.createNameSummarizer(subname);
			
			if (barIndex > 3)
				return name.substring(3, barIndex) + "_" + summarizer.summarize(subname);
			else
				return summarizer.summarize(subname);
		} else {
			throw new IllegalArgumentException("Invalid sequence name");
		}
	}
}