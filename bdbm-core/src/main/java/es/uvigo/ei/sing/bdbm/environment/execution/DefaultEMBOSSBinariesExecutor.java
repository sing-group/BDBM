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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.binaries.EMBOSSBinaries;
import es.uvigo.ei.sing.bdbm.environment.execution.BinaryCheckException;
import es.uvigo.ei.sing.bdbm.environment.execution.EMBOSSBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;

public class DefaultEMBOSSBinariesExecutor 
extends AbstractBinariesExecutor<EMBOSSBinaries> 
implements EMBOSSBinariesExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultEMBOSSBinariesExecutor.class);
	
	public DefaultEMBOSSBinariesExecutor() {}

	public DefaultEMBOSSBinariesExecutor(EMBOSSBinaries eBinaries)
	throws BinaryCheckException {
		this.setBinaries(eBinaries);
	}
	
	@Override
	public void setBinaries(EMBOSSBinaries binaries)
	throws BinaryCheckException {
		DefaultEMBOSSBinariesChecker.checkAll(binaries);
		
		super.setBinaries(binaries);
	}

	@Override
	public boolean checkEMBOSSBinaries(EMBOSSBinaries eBinaries) {
		try {
			DefaultEMBOSSBinariesChecker.checkAll(eBinaries);
			
			return true;
		} catch (BinaryCheckException bce) {
			return false;
		}
	}

	@Override
	public ExecutionResult executeGetORF(
		NucleotideFasta fasta, 
		NucleotideFasta orf,
		int minSize,
		int maxSize
	) throws InterruptedException, ExecutionException {
		return AbstractBinariesExecutor.executeCommand(
			LOG,
			this.binaries.getGetORF(), 
			"-snucleotide", fasta.getFile().getAbsolutePath(),
			"-outseq", orf.getFile().getAbsolutePath(),
			"-table", "0",
			"-minsize", Integer.toString(minSize),
			"-maxsize", Integer.toString(maxSize),
			"-find", "3"
		);
	}

	@Override
	public ExecutionResult executeRevseq(
		NucleotideFasta fasta,
		NucleotideFasta outputFasta
	) throws InterruptedException, ExecutionException {
		return AbstractBinariesExecutor.executeCommand(
			LOG,
			this.binaries.getRevseq(), 
			"-sequence", fasta.getFile().getAbsolutePath(),
			"-outseq", outputFasta.getFile().getAbsolutePath()
		);
	}
}
