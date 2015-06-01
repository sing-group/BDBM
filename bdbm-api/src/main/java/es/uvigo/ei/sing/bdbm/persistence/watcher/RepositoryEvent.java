/*
 * #%L
 * BDBM API
 * %%
 * Copyright (C) 2014 - 2015 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Florentino Fdez-Riverola and Jorge Vieira
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
package es.uvigo.ei.sing.bdbm.persistence.watcher;

import java.io.File;

import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

public class RepositoryEvent {
	public enum Type {
		CREATE,
		DELETE,
		MODIFY,
		INVALIDATED
	};
	
	private final SequenceEntity entity;
	private final File modifiedFile;
	private final Type type;
	
	public RepositoryEvent(SequenceEntity entity, File modifiedFile) {
		this.entity = entity;
		this.modifiedFile = modifiedFile;
		this.type = Type.MODIFY;
	}
	
	public RepositoryEvent(SequenceEntity entity, Type type) {
		this.entity = entity;
		this.modifiedFile = null;
		this.type = type;
	}
	
	public SequenceEntity getEntity() {
		return entity;
	}
	
	public File getModifiedFile() {
		return modifiedFile;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return this.getType().name() + ": " + this.getEntity();
	}
}
