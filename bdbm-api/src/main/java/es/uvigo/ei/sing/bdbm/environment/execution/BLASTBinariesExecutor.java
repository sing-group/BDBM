/*
 * #%L
 * BDBM API
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTType;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;

public interface BLASTBinariesExecutor extends BinariesExecutor<BLASTBinaries> {
	public boolean checkBLASTBinaries(BLASTBinaries bBinaries);
	
	public ExecutionResult executeMakeBlastDB(File inputFasta, Database database)
	throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeBlastDBAliasTool(Database database, Database[] databases)
	throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeBlastDBCMD(Database database, SearchEntry searchEntry, String entry)
	throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeBlastDBCMD(Database database, ExportEntry exportEntry, String entry)
	throws InterruptedException, ExecutionException, IOException;

	public ExecutionResult executeBlastN(
		NucleotideDatabase database, 
		File queryFile,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeBlastN(
		NucleotideDatabase database, 
		NucleotideSearchEntry.NucleotideQuery query,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeBlastP(
		ProteinDatabase database,
		File queryFile,
		ProteinExport export,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;

	public ExecutionResult executeBlastP(
		ProteinDatabase database,
		ProteinQuery query,
		ProteinExport export,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeTBlastX(
		NucleotideDatabase database, 
		File queryFile,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeTBlastX(
		NucleotideDatabase database, 
		NucleotideSearchEntry.NucleotideQuery query,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeTBlastN(
		NucleotideDatabase database, 
		File queryFile,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public ExecutionResult executeTBlastN(
		NucleotideDatabase database, 
		ProteinSearchEntry.ProteinQuery query,
		NucleotideExport export,
		BigDecimal expectedValue, 
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException;
	
	public List<String> extractSignificantSequences(Export.ExportEntry entry)
	throws InterruptedException, ExecutionException, IOException;

	public Map<String, String> getBlastAdditionalParameters(BLASTType blastType);
}
