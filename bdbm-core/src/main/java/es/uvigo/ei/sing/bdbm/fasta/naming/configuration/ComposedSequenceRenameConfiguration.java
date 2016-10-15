/*
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2016 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
package es.uvigo.ei.sing.bdbm.fasta.naming.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ComposedSequenceRenameConfiguration extends AbstractSequenceRenameConfiguration {
	private String delimiterString;
	private int[] selectedIndexes;
	
	public ComposedSequenceRenameConfiguration() {
		this.delimiterString = "|";
		this.selectedIndexes = null;
	}
	
	public void setDelimiterString(String delimiterString) {
		this.delimiterString = delimiterString;
	}
	
	public String getDelimiterString() {
		return delimiterString;
	}
	
	public String getQuotedSeparator() {
		return Pattern.quote(this.getDelimiterString());
	}
	
	public boolean useAllIndexes() {
		return this.selectedIndexes == null;
	}
	
	public void selectAllIndexes() {
		this.setSelectedIndexes(null);
	}
	
	public void setSelectedIndexes(int[] selectedIndexes) {
		this.selectedIndexes = selectedIndexes;
	}
	
	public int[] getSelectedIndexes() {
		return selectedIndexes;
	}
	
	public String join(String ... parts) {
		if (this.selectedIndexes != null && this.selectedIndexes.length > 0) {
			final List<String> selectedParts = new ArrayList<>();
			
			for (int index : this.selectedIndexes) {
				if (index < parts.length) {
					selectedParts.add(parts[index]);
				}
			}
			
			if (selectedParts.isEmpty())
				throw new IllegalArgumentException("No parts selected. Review the selected indexes.");
			
			return String.join(this.getJoinerString(), selectedParts);
		} else {
			return String.join(this.getJoinerString(), parts);
		}
	}
}
