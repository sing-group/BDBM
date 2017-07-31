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

package es.uvigo.ei.sing.bdbm.fasta.naming;

import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.ComposedSequenceRenameConfiguration;

public class GenericComposedSequenceNameSummarizer implements SequenceNameSummarizer<ComposedSequenceRenameConfiguration> {
	@Override
	public boolean recognizes(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		final String separator = configuration.getQuotedSeparator();
		final String regex = "^>[\\p{Graph}]+(" + separator + "[\\p{Graph}]*)*";
		
		return sequenceName.matches(regex);
	}
	
	public SequenceNameParts extractParts(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		sequenceName = sequenceName.substring(1).replace('\t', ' ');
		
		final String name;
		final String description;
		if (sequenceName.contains(" ")) {
			final String[] nameAndDescription = sequenceName.split(" ", 2);
			name = nameAndDescription[0];
			description = nameAndDescription[1].trim();
		} else {
			name = sequenceName;
			description = "";
		}
		
		final String[] parts = name.split(configuration.getQuotedSeparator());
		
		return new SequenceNameParts(parts, description);
	}

	@Override
	public String summarize(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		final SequenceNameParts nameParts = this.extractParts(sequenceName, configuration);
		
		String summary = ">" + configuration.join(nameParts.getParts());
		if (configuration.isKeepDescription() && nameParts.hasDescription())
			summary += " " + nameParts.getDescription();
		
		return summary;
	}
	
	public static class SequenceNameParts {
		private String[] parts;
		private String description;

		public SequenceNameParts(String[] parts, String description) {
			this.parts = parts;
			this.description = description;
		}

		public String[] getParts() {
			return parts;
		}
		
		public void setParts(String[] parts) {
			this.parts = parts;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public boolean hasDescription() {
			return !this.description.isEmpty();
		}
	}
}
