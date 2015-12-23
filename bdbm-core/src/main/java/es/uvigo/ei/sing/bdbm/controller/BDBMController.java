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
package es.uvigo.ei.sing.bdbm.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTType;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.EMBOSSBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.SplignBinariesExecutor;
import es.uvigo.ei.sing.bdbm.fasta.FastaParseException;
import es.uvigo.ei.sing.bdbm.fasta.ReformatFastaParameters;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils.RenameMode;
import es.uvigo.ei.sing.bdbm.persistence.BDBMRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.EntityAlreadyExistsException;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry.NucleotideQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;

public interface BDBMController {
	public abstract void setRepositoryManager(BDBMRepositoryManager repositoryManager);
	public abstract void setBlastBinariesExecutor(BLASTBinariesExecutor binariesExecutor);
	public abstract void setEmbossBinariesExecutor(EMBOSSBinariesExecutor eBinariesExecutor);
	public abstract void setBedToolsBinariesExecutor(BedToolsBinariesExecutor bBinariesExecutor);
	public abstract void setSplignBinariesExecutor(SplignBinariesExecutor sBinariesExecutor);
	public abstract void setCompartBinariesExecutor(CompartBinariesExecutor cBinariesExecutor);

	public abstract boolean exists(SequenceEntity entity);

	public abstract boolean delete(SequenceEntity entity) throws IOException;
	public abstract boolean delete(Database database) throws IOException;
	public abstract boolean delete(Fasta fasta) throws IOException;
	public abstract boolean delete(SearchEntry search) throws IOException;
	public abstract boolean delete(Query query) throws IOException;
	public abstract boolean delete(Export export) throws IOException;
	public abstract boolean delete(ExportEntry exportEntry) throws IOException;

	public abstract ProteinDatabase[] listProteinDatabases();
	public abstract ProteinFasta[] listProteinFastas();
	public abstract ProteinSearchEntry[] listProteinSearchEntries();
	public abstract ProteinExport[] listProteinExports();
	
	public abstract NucleotideDatabase[] listNucleotideDatabases();
	public abstract NucleotideFasta[] listNucleotideFastas();
	public abstract NucleotideSearchEntry[] listNucleotideSearchEntries();
	public abstract NucleotideExport[] listNucleotideExports();

	public abstract Fasta importFasta(SequenceType sequenceType, File file)
		throws EntityAlreadyExistsException, IOException;

	public abstract Database makeBlastDB(Fasta inputFasta, String outputDBName)
		throws ExecutionException, IllegalArgumentException, EntityAlreadyExistsException, IOException, InterruptedException;

	public abstract Database blastdbAliasTool(Database[] databases, String outputDBName)
		throws EntityAlreadyExistsException, IOException, InterruptedException, ExecutionException;

	public abstract SearchEntry retrieveSearchEntry(Database database, String accession)
		throws InterruptedException, ExecutionException, IOException;

	public abstract NucleotideExport blastn(
		NucleotideDatabase database,
		NucleotideQuery query,
		BigDecimal expectedValue, 
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;
	
	public abstract NucleotideExport blastn(
		NucleotideDatabase database,
		File queryFile,
		BigDecimal expectedValue, 
		boolean filter, 
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;

	public abstract ProteinExport blastp(
		ProteinDatabase database,
		ProteinQuery query, 
		BigDecimal expectedValue, 
		boolean keepSingleSequenceFiles,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;
	
	public abstract ProteinExport blastp(
		ProteinDatabase database,
		File queryFile,
		BigDecimal expectedValue, 
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;

	public abstract NucleotideExport tblastx(
		NucleotideDatabase database,
		NucleotideQuery query,
		BigDecimal expectedValue, 
		boolean filter, 
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;

	public abstract NucleotideExport tblastx(
		NucleotideDatabase database,
		File queryFile,
		BigDecimal expectedValue, 
		boolean filter, 
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;

	public abstract NucleotideExport tblastn(
		NucleotideDatabase database,
		ProteinQuery query,
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;

	public abstract NucleotideExport tblastn(
		NucleotideDatabase database,
		File queryFile,
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException;
	
	public Map<String, String> getBlastAdditionalParameters(BLASTType blastType);

	public abstract NucleotideFasta getORF(
		NucleotideFasta fasta, 
		int minSize, int maxSize, 
		boolean noNewLine,
		String outputName
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException, FastaParseException;

	public abstract NucleotideFasta splignCompart(
		NucleotideFasta genomeFasta,
		NucleotideFasta cdsFasta,
		boolean concatenateExons,
		String outputName
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException, FastaParseException;
	
	public abstract void reformatFasta(
		RenameMode mode, Fasta fasta, int fragmentLength, Map<ReformatFastaParameters, Object> additionalParameters
	) throws FastaParseException, IOException;
	
	public abstract void mergeFastas(Fasta[] fastas, String outputFasta) throws FastaParseException, IOException;
}