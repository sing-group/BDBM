/*
 * #%L
 * BDBM API
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
import java.util.List;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryListener;

public interface RepositoryManager<SE extends SequenceEntity> extends Repository {
	// Returns the corresponding entity. If the entity does not exists, it
	// would be created
	public abstract SE get(SequenceType sequenceType, String name)
	throws IOException;
	
	// Creates the corresponding entity. If the entity already exists, an
	// EntityAlreadyExists are thrown
	public abstract SE create(SequenceType sequenceType, String name)
	throws EntityAlreadyExistsException, IOException;
	
	// Deletes an entity, even if it is not a valid file
	public abstract boolean delete(SE entity)
	throws IOException;
	
	public void validateEntityPath(SequenceType type, File entityPath) throws EntityValidationException;
	public void validate(SE entity) throws EntityValidationException;
	
	public abstract SE[] list(SequenceType sequenceType);
	
	public abstract boolean exists(SE entity);
	public abstract boolean exists(SequenceType sequenceType, String name);
	
	public abstract void addRepositoryListener(RepositoryListener listener);
	public abstract boolean removeRepositoryListener(RepositoryListener listener);
	public abstract boolean containsRepositoryListener(RepositoryListener listener);
	public abstract List<RepositoryListener> listRepositoryListeners();
}