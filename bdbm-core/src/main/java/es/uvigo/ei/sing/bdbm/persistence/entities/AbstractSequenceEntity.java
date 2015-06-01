/*
 * #%L
 * BDBM Core
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
package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.util.Observable;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

public class AbstractSequenceEntity extends Observable implements SequenceEntity {
	protected final SequenceType type;
	protected final File baseFile;

	public AbstractSequenceEntity(SequenceType type, File baseFile) {
		this.type = type;
		this.baseFile = baseFile;
	}
	
	@Override
	public File getBaseFile() {
		return this.baseFile;
	}

	@Override
	public SequenceType getType() {
		return type;
	}

	@Override
	public String getName() {
		return this.baseFile.getName();
	}

	@Override
	public String toString() {
		return this.getName();
//		return new StringBuilder(this.getName())
//			.append(" [").append(this.getType()).append(']')
//		.toString();
	}
}