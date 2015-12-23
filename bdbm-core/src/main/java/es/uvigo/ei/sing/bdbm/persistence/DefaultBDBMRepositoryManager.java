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

import java.io.IOException;

import es.uvigo.ei.sing.bdbm.environment.paths.RepositoryPaths;
import es.uvigo.ei.sing.bdbm.persistence.BDBMRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.DatabaseRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.ExportRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.FastaRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.RepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.SearchEntryRepositoryManager;

public class DefaultBDBMRepositoryManager implements BDBMRepositoryManager {
	private final DatabaseRepositoryManager databaseManager;
	private final ExportRepositoryManager exportManager;
	private final FastaRepositoryManager fastaManager;
	private final SearchEntryRepositoryManager searchEntryManager;
	
	private final RepositoryManager<?>[] repositories;
	
	public DefaultBDBMRepositoryManager() {
		this.repositories = new RepositoryManager[] {
			this.databaseManager = new DefaultDatabaseRepositoryManager(),
			this.exportManager = new DefaultExportRepositoryManager(),
			this.fastaManager = new DefaultFastaRespositoryManager(),
			this.searchEntryManager = new DefaultSearchEntryRepositoryManager(),
		};
	}
	
	public DefaultBDBMRepositoryManager(RepositoryPaths repositoryPaths) throws IOException {
		this();
		
		this.setRepositoryPaths(repositoryPaths);
	}

	@Override
	public void setRepositoryPaths(RepositoryPaths repositoryPaths) throws IOException {
		for (RepositoryManager<?> repository : this.repositories) {
			repository.setRepositoryPaths(repositoryPaths);
		}
	}
	
	@Override
	public FastaRepositoryManager fasta() {
		return this.fastaManager;
	}

	@Override
	public DatabaseRepositoryManager database() {
		return this.databaseManager;
	}
	
	@Override
	public ExportRepositoryManager export() {
		return this.exportManager;
	}
	
	@Override
	public SearchEntryRepositoryManager searchEntry() {
		return this.searchEntryManager;
	}
	
	@Override
	public void shutdown() {
		for (RepositoryManager<?> repository : this.repositories) {
			repository.shutdown();
		}
	}
}
