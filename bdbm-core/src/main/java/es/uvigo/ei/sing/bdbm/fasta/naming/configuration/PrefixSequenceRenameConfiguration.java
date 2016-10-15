/*
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2016 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

public class PrefixSequenceRenameConfiguration extends AbstractSequenceRenameConfiguration {
	private String prefix;
	private boolean addIndex;
	private boolean keepNames;

	public PrefixSequenceRenameConfiguration() {
		this.prefix = null;
		this.keepNames = true;
		this.addIndex = true;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public boolean hasPrefix() {
		return this.prefix != null && !this.prefix.isEmpty();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isAddIndex() {
		return addIndex;
	}

	public void setAddIndex(boolean addIndex) {
		this.addIndex = addIndex;
	}

	public boolean isKeepNames() {
		return keepNames;
	}

	public void setKeepNames(boolean keepNames) {
		this.keepNames = keepNames;
	}
	
	public boolean isValidConfiguration() {
		return prefix == null && !addIndex && !keepNames;
	}
}
