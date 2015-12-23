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
package es.uvigo.ei.sing.bdbm.environment.execution;

import es.uvigo.ei.sing.bdbm.environment.binaries.CompartBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinariesChecker;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinaryToolsFactory;

public class DefaultCompartBinaryToolsFactory implements CompartBinaryToolsFactory {
	private CompartBinaries nBinaries;
	
	@Override
	public boolean isValidFor(CompartBinaries nBinaries) {
		return false;
	}

	@Override
	public void setBinaries(CompartBinaries nBinaries)
	throws BinaryCheckException {
		DefaultCompartBinariesChecker.checkAll(nBinaries);
		
		this.nBinaries = nBinaries;
	}

	@Override
	public CompartBinariesChecker createChecker() {
		return new DefaultCompartBinariesChecker(this.nBinaries);
	}

	@Override
	public CompartBinariesExecutor createExecutor() {
		try {
			return new DefaultCompartBinariesExecutor(this.nBinaries);
		} catch (BinaryCheckException e) {
			throw new RuntimeException(e);
		}
	}
}
