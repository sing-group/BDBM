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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.binaries.SplignBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.environment.execution.SplignBinariesExecutor;

public class DefaultSplignBinariesExecutor
extends AbstractBinariesExecutor<SplignBinaries>
implements SplignBinariesExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(SplignBinariesExecutor.class);
	
	public DefaultSplignBinariesExecutor() {}

	public DefaultSplignBinariesExecutor(SplignBinaries nBinaries)
	throws BinaryCheckException {
		this.setBinaries(nBinaries);
	}

	@Override
	public void setBinaries(
		SplignBinaries nBinaries
	) throws BinaryCheckException {
		DefaultSplignBinariesChecker.checkAll(nBinaries);
		
		this.binaries = nBinaries;
	}
	
	@Override
	public boolean checkSplignBinaries(SplignBinaries bBinaries) {
		try {
			DefaultSplignBinariesChecker.checkAll(bBinaries);
			
			return true;
		} catch (BinaryCheckException bce) {
			return false;
		}
	}
	
	@Override
	public ExecutionResult mklds(String path, InputLineCallback ... callbacks)
	throws ExecutionException, InterruptedException {
		return AbstractBinariesExecutor.executeCommand(
			LOG,
			false,
			Arrays.asList(callbacks),
			this.binaries.getSplign(), 
			"-mklds", path
		);
	}
	
	@Override
	public ExecutionResult ldsdir(
		String ldsdir, String comps,
		InputLineCallback... callbacks
	) throws ExecutionException, InterruptedException {
		return AbstractBinariesExecutor.executeCommand(
			LOG,
			false,
			Arrays.asList(callbacks),
			this.binaries.getSplign(),
			"-ldsdir", ldsdir,
			"-comps", comps
		);
	}
}
