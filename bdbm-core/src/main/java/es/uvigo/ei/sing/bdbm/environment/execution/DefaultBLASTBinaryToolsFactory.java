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

import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesChecker;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinaryToolsFactory;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;

public class DefaultBLASTBinaryToolsFactory implements BLASTBinaryToolsFactory {
	private BLASTBinaries bBinaries;
	
	@Override
	public boolean isValidFor(BLASTBinaries bBinaries) {
		return false;
	}

	@Override
	public void setBinaries(BLASTBinaries bBinaries)
	throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkAll(bBinaries);
		
		this.bBinaries = bBinaries;
	}

	@Override
	public BLASTBinariesChecker createChecker() {
		return new DefaultBLASTBinariesChecker(this.bBinaries);
	}

	@Override
	public BLASTBinariesExecutor createExecutor() {
		try {
			return new DefaultBLASTBinariesExecutor(this.bBinaries);
		} catch (BinaryCheckException e) {
			throw new RuntimeException(e);
		}
	}
}
