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
package es.uvigo.ei.sing.bdbm.persistence;

import java.io.File;
import java.io.IOException;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

abstract class EntityBuilder<T extends SequenceEntity> {
	public abstract T create(SequenceType sequenceType, File file);
	
	public static SearchEntry createUnwatchedSearchEntry(SequenceType sequenceType, File file) 
	throws IOException {
		return AbstractSearchEntry.newSearchEntry(sequenceType, file, false);
	}
	
	public final static EntityBuilder<Database> db() {
		return new EntityBuilder<Database>() {
			@Override
			public Database create(SequenceType sequenceType, File file) {
				return AbstractDatabase.newDatabase(sequenceType, file);
			}
		};
	}
	
	public final static EntityBuilder<SearchEntry> searchEntry() {
		return new EntityBuilder<SearchEntry>() {
			@Override
			public SearchEntry create(SequenceType sequenceType, File file) {
				try {
					return AbstractSearchEntry.newSearchEntry(sequenceType, file);
				} catch (IOException e) {
					throw new RuntimeException("Error creating search entry", e);
				}
			}
		};
	}
	
	public final static EntityBuilder<Export> export() {
		return new EntityBuilder<Export>() {
			@Override
			public Export create(SequenceType sequenceType, File file) {
				return AbstractExport.newExport(sequenceType, file);
			}
		};
	}
	
	public final static EntityBuilder<Fasta> fasta() {
		return new EntityBuilder<Fasta>() {
			@Override
			public Fasta create(SequenceType sequenceType, File file) {
				return AbstractFasta.newFasta(sequenceType, file);
			}
		};
	}
}