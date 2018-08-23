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

package es.uvigo.ei.sing.bdbm.fasta.naming.standard;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.sing.bdbm.fasta.naming.GenericComposedSequenceNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.GenericComposedSequenceNameSummarizer.SequenceNameParts;
import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.ComposedSequenceRenameConfiguration;

public abstract class AbstractStandardNameSummarizer implements StandardSequenceNameSummarizer {
	protected GenericComposedSequenceNameSummarizer summarizer;
	
	public AbstractStandardNameSummarizer() {
		this.summarizer = new GenericComposedSequenceNameSummarizer();
	}
	
	public abstract List<MatcherNameField> getNameFields();
	
	@Override
	public boolean recognizes(String sequenceName) {
		return this.recognizes(sequenceName, new ComposedSequenceRenameConfiguration());
	}
	
	@Override
	public boolean recognizes(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		try {
			identifyNameFields(sequenceName, configuration);
			return true;
		} catch (IllegalArgumentException iae) {
			return false;
		}
	}
	
	@Override
	public String summarize(String sequenceName) {
		return summarize(sequenceName, new ComposedSequenceRenameConfiguration());
	}

	@Override
	public String summarize(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		return this.summarizer.summarize(sequenceName, configuration);
	}
	
	@Override
	public List<MatcherNameField> identifyNameFields(String sequenceName) {
		return this.identifyNameFields(sequenceName, new ComposedSequenceRenameConfiguration());
	}
	
	@Override
	public List<MatcherNameField> identifyNameFields(String sequenceName, ComposedSequenceRenameConfiguration configuration) {
		final SequenceNameParts nameParts = this.summarizer.extractParts(sequenceName, configuration);
		
		final List<MatcherNameField> identifiedFields = new ArrayList<>();
		
		final String[] parts = nameParts.getParts();
		
		if (!parts[0].equals(this.getPrefix()))
			throw new IllegalArgumentException(format("Invalid prefix '%s'. Expected '%s'.", parts[0], this.getPrefix()));
		
		for (int i = 1; i < parts.length; i++) {
			final String part = parts[i];
			final MatcherNameField identifiedField = this.getField(i - 1);
			
			if (identifiedField.matches(part))
				identifiedFields.add(identifiedField);
			else if (!identifiedField.isOptional())
				throw new IllegalArgumentException(format("Invalid value '%s' for field with index %d", part, i));
		}
		
		return identifiedFields;
	}
	
	protected MatcherNameField getField(int index) {
		final List<MatcherNameField> fields = this.getNameFields();
		
		for (MatcherNameField field : fields) {
			if (field.getIndex() == index) {
				return field;
			}
		}
		
		throw new IllegalArgumentException(format("No field found for index %d", index));
	}

	public static class MatcherNameField implements NameField, Comparable<MatcherNameField> {
		private final int index;
		private final String name;
		private final boolean optional;
		private final String regexp;
		
		public MatcherNameField(int index, String name) {
			this(index, name, false);
		}
		
		public MatcherNameField(int index, String name, boolean optional) {
			this(index, name, optional, ".+");
		}
		
		public MatcherNameField(int index, String name, boolean optional, String regexp) {
			this.index = index;
			this.name = name;
			this.optional = optional;
			this.regexp = regexp;
		}

		@Override
		public int getIndex() {
			return this.index;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isOptional() {
			return this.optional;
		}
		
		public String getRegexp() {
			return regexp;
		}
		
		public boolean matches(String fieldName) {
			return fieldName.matches(this.getRegexp());
		}

		@Override
		public int compareTo(MatcherNameField o) {
			if (o == null) {
				return 1;
			} else if (this.getIndex() == o.getIndex()) {
				return this.getIndex() - o.getIndex();
			} else {
				throw new IllegalStateException("name fields can't have same index");
			}
		}
	}
}
