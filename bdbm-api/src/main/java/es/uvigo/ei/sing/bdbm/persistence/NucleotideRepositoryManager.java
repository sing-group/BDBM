/*-
 * #%L
 * BDBM API
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

import java.io.IOException;

import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

public interface NucleotideRepositoryManager<SE extends SequenceEntity, N extends NucleotideSequenceEntity> 
extends RepositoryManager<SE> {
	public abstract N getNucleotide(String name) throws IOException;
	public abstract N createNucleotide(String name)
	throws EntityAlreadyExistsException, IOException;
	public abstract N[] listNucleotide();
	public abstract boolean existsNucleotide(String name);
}
