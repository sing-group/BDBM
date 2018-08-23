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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.binaries.ProSplignBinaries;

public class DefaultProSplignBinariesExecutor
extends AbstractBinariesExecutor<ProSplignBinaries>
implements ProSplignBinariesExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(ProSplignBinariesExecutor.class);
	
	public DefaultProSplignBinariesExecutor() {}

	public DefaultProSplignBinariesExecutor(ProSplignBinaries nBinaries)
	throws BinaryCheckException {
		this.setBinaries(nBinaries);
	}

	@Override
	public void setBinaries(
		ProSplignBinaries nBinaries
	) throws BinaryCheckException {
		DefaultProSplignBinariesChecker.checkAll(nBinaries);
		
		this.binaries = nBinaries;
	}
	
	@Override
	public boolean checkProSplignBinaries(ProSplignBinaries bBinaries) {
		try {
			DefaultProSplignBinariesChecker.checkAll(bBinaries);
			
			return true;
		} catch (BinaryCheckException bce) {
			return false;
		}
	}

	@Override
	public ExecutionResult proSplign(File compart, File nucleotidesFasta,
		File proteinQueryFasta, File outputDir, String outputFileName)
		throws ExecutionException, InterruptedException {
		return AbstractBinariesExecutor.executeCommand(
			LOG, 
			false, 
			this.binaries.getProSplign(), 
			"-i", compart.getAbsolutePath(),
			"-fasta", nucleotidesFasta.getAbsolutePath() + "," + proteinQueryFasta.getAbsolutePath(),
			"-nogenbank", 
			"-o", new File(outputDir, outputFileName + ".asn").getAbsolutePath(),
			"-eo", new File(outputDir, outputFileName + ".txt").getAbsolutePath()
		);
	}
}
