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

package es.uvigo.ei.sing.bdbm.environment.execution;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.binaries.BedToolsBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;

public class DefaultBedToolsBinariesExecutor
extends AbstractBinariesExecutor<BedToolsBinaries>
implements BedToolsBinariesExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(BedToolsBinariesExecutor.class);
	
	public DefaultBedToolsBinariesExecutor() {}

	public DefaultBedToolsBinariesExecutor(BedToolsBinaries nBinaries)
	throws BinaryCheckException {
		this.setBinaries(nBinaries);
	}

	@Override
	public void setBinaries(
		BedToolsBinaries nBinaries
	) throws BinaryCheckException {
		DefaultBedToolsBinariesChecker.checkAll(nBinaries);
		
		this.binaries = nBinaries;
	}
	
	@Override
	public boolean checkBedToolsBinaries(BedToolsBinaries bBinaries) {
		try {
			DefaultBedToolsBinariesChecker.checkAll(bBinaries);
			
			return true;
		} catch (BinaryCheckException bce) {
			return false;
		}
	}
	
	@Override
	public ExecutionResult getfasta(
		String fi, String bed, String fo, InputLineCallback ... callbacks
	) throws ExecutionException, InterruptedException {
		return AbstractBinariesExecutor.executeCommand(
			LOG,
			false,
			Arrays.asList(callbacks),
			this.binaries.getBedtools(),
			"getfasta", 
			"-fi", fi,
			"-bed", bed,
			"-fo", fo,
			"-name"
		);
	}
}
