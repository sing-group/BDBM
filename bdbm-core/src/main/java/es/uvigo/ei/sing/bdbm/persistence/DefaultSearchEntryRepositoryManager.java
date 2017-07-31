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

package es.uvigo.ei.sing.bdbm.persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.EntityValidationException;
import es.uvigo.ei.sing.bdbm.persistence.SearchEntryRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;

public class DefaultSearchEntryRepositoryManager
extends AbstractMixedRepositoryManager<SearchEntry, ProteinSearchEntry, NucleotideSearchEntry>
implements SearchEntryRepositoryManager {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultSearchEntryRepositoryManager.class);

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected EntityBuilder<SearchEntry> getEntityBuilder() {
		return EntityBuilder.searchEntry();
	}

	@Override
	protected EntityValidator<SearchEntry> getEntityValidator() {
		return EntityValidator.searchEntry();
	}

	@Override
	protected FileFilter getDirectoryFilter() {
		return DirectoryFileFilter.DIRECTORY;
	}
	
	@Override
	protected File getDirectory(SequenceType sequenceType) {
		return sequenceType == SequenceType.PROTEIN ?
			this.repositoryPaths.getSearchEntryProteinsDirectory() :
			this.repositoryPaths.getSearchEntryNucleotidesDirectory();
	}

	@Override
	protected boolean createEntityFiles(SearchEntry entity) {
		return entity.getDirectory().mkdirs();
	}
	
	@Override
	public void validateEntityPath(SequenceType type, File entityPath) throws EntityValidationException {
		final EntityValidator<SearchEntry> entityValidator = this.getEntityValidator();
		try {
			entityValidator.validate(EntityBuilder.createUnwatchedSearchEntry(type, entityPath));
		} catch (IOException e) {
			throw new EntityValidationException("Error validating search entry", e, null);
		}
	}
}
