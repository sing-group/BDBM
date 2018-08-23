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

package es.uvigo.ei.sing.bdbm.fasta.naming.configuration;

import static java.util.Objects.requireNonNull;

public abstract class AbstractSequenceRenameConfiguration implements SequenceRenameConfiguration {
	private String joinerString;
	private boolean keepDescription;

	public AbstractSequenceRenameConfiguration() {
		this.joinerString = "_";
		this.keepDescription = true;
	}
	
	public void setJoinerString(String joinerString) {
		this.joinerString = requireNonNull(joinerString, "Joiner string can't be null");
	}
	
	public String getJoinerString() {
		return joinerString;
	}
	
	public void setKeepDescription(boolean keepDescription) {
		this.keepDescription = keepDescription;
	}
	
	public boolean isKeepDescription() {
		return keepDescription;
	}
}
