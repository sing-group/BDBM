/*-
 * #%L
 * BDBM API
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

import java.util.Map;
import java.util.Optional;

import es.uvigo.ei.sing.bdbm.environment.binaries.EMBOSSBinaries;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;

public interface EMBOSSBinariesExecutor extends BinariesExecutor<EMBOSSBinaries> {
	public boolean checkEMBOSSBinaries(EMBOSSBinaries eBinaries);
	
	public ExecutionResult executeGetORF(
		NucleotideFasta fasta,
		NucleotideFasta orf,
		int minSize,
		int maxSize,
		int find
	) throws InterruptedException, ExecutionException;

	public ExecutionResult executeGetORF(
	  NucleotideFasta fasta,
	  NucleotideFasta orf,
	  int minSize,
	  int maxSize,
	  int find,
	  Map<String, Optional<String>> additionalParameters
    ) throws InterruptedException, ExecutionException;
	
	public ExecutionResult executeRevseq(
		NucleotideFasta fasta,
		NucleotideFasta outputFasta
	) throws InterruptedException, ExecutionException;
}
