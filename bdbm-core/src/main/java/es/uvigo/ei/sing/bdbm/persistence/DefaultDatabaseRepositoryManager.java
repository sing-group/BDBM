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
import java.io.FileFilter;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.DatabaseRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;

public class DefaultDatabaseRepositoryManager 
extends AbstractMixedRepositoryManager<Database, ProteinDatabase, NucleotideDatabase> 
implements DatabaseRepositoryManager {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultDatabaseRepositoryManager.class);
	
	protected File getDirectory(SequenceType sequenceType) {
		return sequenceType == SequenceType.PROTEIN ?
			this.repositoryPaths.getDBProteinsDirectory() :
			this.repositoryPaths.getDBNucleotidesDirectory();
	}
	
	@Override
	protected FileFilter getDirectoryFilter() {
		return DirectoryFileFilter.DIRECTORY;
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected EntityBuilder<Database> getEntityBuilder() {
		return EntityBuilder.db();
	}

	@Override
	protected EntityValidator<Database> getEntityValidator() {
		return EntityValidator.db();
	}

	@Override
	protected boolean createEntityFiles(Database entity) {
		return entity.getDirectory().mkdirs();
	}
}
