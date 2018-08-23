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

package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.FileFileFilter;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;

public class DefaultNucleotideSearchEntry
extends AbstractSearchEntry
implements NucleotideSearchEntry {
	public DefaultNucleotideSearchEntry(File file) throws IOException {
		super(SequenceType.NUCLEOTIDE, file);
	}
	
	public DefaultNucleotideSearchEntry(File file, boolean watch) throws IOException {
		super(SequenceType.NUCLEOTIDE, file, watch);
	}
	
	@Override
	public List<NucleotideQuery> listQueries() {
		final File[] files = this.getDirectory().listFiles((FileFilter) FileFileFilter.FILE);
		final List<NucleotideQuery> queries = new ArrayList<NucleotideQuery>(files.length);
		
		for (File file : files) {
			queries.add(new DefaultNucleotideQuery(file));
		}
		
		return queries;
	}
	
	@Override
	public NucleotideQuery getQuery(String name) {
		return (NucleotideQuery) super.getQuery(name);
	}
	
	public class DefaultNucleotideQuery extends DefaultQuery implements NucleotideQuery {
		public DefaultNucleotideQuery(File file) {
			super(file, DefaultNucleotideSearchEntry.this);
		}
	}
}
