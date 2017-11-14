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

package es.uvigo.ei.sing.bdbm.environment.execution;

import es.uvigo.ei.sing.bdbm.environment.binaries.ProCompartBinaries;

public class DefaultProCompartBinaryToolsFactory implements ProCompartBinaryToolsFactory {
	private ProCompartBinaries nBinaries;
	
	@Override
	public boolean isValidFor(ProCompartBinaries nBinaries) {
		return false;
	}

	@Override
	public void setBinaries(ProCompartBinaries nBinaries)
	throws BinaryCheckException {
		DefaultProCompartBinariesChecker.checkAll(nBinaries);
		
		this.nBinaries = nBinaries;
	}

	@Override
	public ProCompartBinariesChecker createChecker() {
		return new DefaultProCompartBinariesChecker(this.nBinaries);
	}

	@Override
	public ProCompartBinariesExecutor createExecutor() {
		try {
			return new DefaultProCompartBinariesExecutor(this.nBinaries);
		} catch (BinaryCheckException e) {
			throw new RuntimeException(e);
		}
	}
}
