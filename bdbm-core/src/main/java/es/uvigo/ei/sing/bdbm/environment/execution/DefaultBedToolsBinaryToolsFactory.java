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
package es.uvigo.ei.sing.bdbm.environment.execution;

import es.uvigo.ei.sing.bdbm.environment.binaries.BedToolsBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesChecker;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinaryToolsFactory;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;

public class DefaultBedToolsBinaryToolsFactory implements BedToolsBinaryToolsFactory {
	private BedToolsBinaries nBinaries;
	
	@Override
	public boolean isValidFor(BedToolsBinaries nBinaries) {
		return false;
	}

	@Override
	public void setBinaries(BedToolsBinaries nBinaries)
	throws BinaryCheckException {
		DefaultBedToolsBinariesChecker.checkAll(nBinaries);
		
		this.nBinaries = nBinaries;
	}

	@Override
	public BedToolsBinariesChecker createChecker() {
		return new DefaultBedToolsBinariesChecker(this.nBinaries);
	}

	@Override
	public BedToolsBinariesExecutor createExecutor() {
		try {
			return new DefaultBedToolsBinariesExecutor(this.nBinaries);
		} catch (BinaryCheckException e) {
			throw new RuntimeException(e);
		}
	}
}
