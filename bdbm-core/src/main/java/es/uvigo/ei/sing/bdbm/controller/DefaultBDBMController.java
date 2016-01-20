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
package es.uvigo.ei.sing.bdbm.controller;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTType;
import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.EMBOSSBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.environment.execution.SplignBinariesExecutor;
import es.uvigo.ei.sing.bdbm.fasta.FastaParseException;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils.RenameMode;
import es.uvigo.ei.sing.bdbm.fasta.ReformatFastaParameters;
import es.uvigo.ei.sing.bdbm.persistence.BDBMRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.DatabaseRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.EntityAlreadyExistsException;
import es.uvigo.ei.sing.bdbm.persistence.EntityValidationException;
import es.uvigo.ei.sing.bdbm.persistence.ExportRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.FastaRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.SearchEntryRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry.NucleotideQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

public class DefaultBDBMController implements BDBMController {
	private BDBMRepositoryManager repositoryManager;
	private BLASTBinariesExecutor blastBinariesExecutor;
	private EMBOSSBinariesExecutor embossBinariesExecutor;
	private BedToolsBinariesExecutor bedToolsBinariesExecutor;
	private SplignBinariesExecutor splignBinariesExecutor;
	private CompartBinariesExecutor compartBinariesExecutor;
	
	public DefaultBDBMController() {
		this(null, null, null, null, null, null);
	}
	
	public DefaultBDBMController(
		BDBMRepositoryManager repositoryManager, 
		BLASTBinariesExecutor blastBinariesExecutor,
		EMBOSSBinariesExecutor embossBinariesExecutor,
		BedToolsBinariesExecutor bedToolsBinariesExecutor,
		SplignBinariesExecutor splignBinariesExecutor,
		CompartBinariesExecutor compartBinariesExecutor
	) {
		this.repositoryManager = repositoryManager;
		this.blastBinariesExecutor = blastBinariesExecutor;
		this.embossBinariesExecutor = embossBinariesExecutor;
		this.bedToolsBinariesExecutor = bedToolsBinariesExecutor;
		this.splignBinariesExecutor = splignBinariesExecutor;
		this.compartBinariesExecutor = compartBinariesExecutor;
	}
	
	@Override
	public void setRepositoryManager(BDBMRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}
	
	@Override
	public void setBlastBinariesExecutor(BLASTBinariesExecutor binariesExecutor) {
		this.blastBinariesExecutor = binariesExecutor;
	}
	
	@Override
	public void setEmbossBinariesExecutor(EMBOSSBinariesExecutor eBinariesExecutor) {
		this.embossBinariesExecutor = eBinariesExecutor;
	}
	
	@Override
	public void setBedToolsBinariesExecutor(BedToolsBinariesExecutor bedToolsBinariesExecutor) {
		this.bedToolsBinariesExecutor = bedToolsBinariesExecutor;
	}
	
	@Override
	public void setSplignBinariesExecutor(SplignBinariesExecutor sBinariesExecutor) {
		this.splignBinariesExecutor = sBinariesExecutor;
	}
	
	@Override
	public void setCompartBinariesExecutor(CompartBinariesExecutor cBinariesExecutor) {
		this.compartBinariesExecutor = cBinariesExecutor;
	}
	
	@Override
	public boolean exists(SequenceEntity entity) {
		if (entity instanceof Database) {
			return this.repositoryManager.database().exists((Database) entity);
		} else if (entity instanceof Fasta) {
			return this.repositoryManager.fasta().exists((Fasta) entity);
		} else if (entity instanceof SearchEntry) {
			return this.repositoryManager.searchEntry().exists((SearchEntry) entity);
		} else if (entity instanceof Export) {
			return this.repositoryManager.export().exists((Export) entity);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean delete(SequenceEntity entity) throws IOException {
		if (entity instanceof Database) {
			return this.delete((Database) entity);
		} else if (entity instanceof Fasta) {
			return this.delete((Fasta) entity);
		} else if (entity instanceof SearchEntry) {
			return this.delete((SearchEntry) entity);
		} else if (entity instanceof Query) {
			return this.delete((Query) entity);
		} else if (entity instanceof Export) {
			return this.delete((Export) entity);
		} else if (entity instanceof ExportEntry) {
			return this.delete((ExportEntry) entity);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean delete(Database database) throws IOException {
		return this.repositoryManager.database().delete(database);
	}
	
	@Override
	public boolean delete(Fasta fasta) throws IOException {
		return this.repositoryManager.fasta().delete(fasta);
	}
	
	@Override
	public boolean delete(SearchEntry search) throws IOException {
		return this.repositoryManager.searchEntry().delete(search);
	}
	
	@Override
	public boolean delete(Query query) throws IOException {
		final SearchEntry searchEntry = query.getSearchEntry();
		
		searchEntry.deleteQuery(query);
		if (searchEntry.listQueries().isEmpty()) {
			return this.delete(searchEntry);
		} else {
			return true;
		}
	}
	
	@Override
	public boolean delete(Export export) throws IOException {
		return this.repositoryManager.export().delete(export);
	}
	
	@Override
	public boolean delete(ExportEntry exportEntry) throws IOException {
		final Export export = exportEntry.getExport();
		
		export.deleteExportEntry(exportEntry);
		if (export.listEntries().isEmpty()) {
			return this.delete(export);
		} else {
			return true;
		}
	}
	
	@Override
	public ProteinDatabase[] listProteinDatabases() {
		return this.repositoryManager.database().listProtein();
	}
	
	@Override
	public NucleotideDatabase[] listNucleotideDatabases() {
		return this.repositoryManager.database().listNucleotide();
	}
	
	@Override
	public ProteinFasta[] listProteinFastas() {
		return this.repositoryManager.fasta().listProtein();
	}
	
	@Override
	public NucleotideFasta[] listNucleotideFastas() {
		return this.repositoryManager.fasta().listNucleotide();
	}
	
	@Override
	public ProteinSearchEntry[] listProteinSearchEntries() {
		return this.repositoryManager.searchEntry().listProtein();
	}
	
	@Override
	public NucleotideSearchEntry[] listNucleotideSearchEntries() {
		return this.repositoryManager.searchEntry().listNucleotide();
	}
	
	@Override
	public ProteinExport[] listProteinExports() {
		return this.repositoryManager.export().listProtein();
	}
	
	@Override
	public NucleotideExport[] listNucleotideExports() {
		return this.repositoryManager.export().listNucleotide();
	}
	
	@Override
	public Fasta importFasta(SequenceType sequenceType, File file) throws EntityAlreadyExistsException, IOException {
		final FastaRepositoryManager fastaManager = this.repositoryManager.fasta();
		final Fasta fasta = fastaManager.create(sequenceType, file.getName());
		
		try {
			FileUtils.copyFile(file, fasta.getFile());
			
			return fasta;
		} finally {
			if (!fastaManager.exists(fasta))
				fastaManager.delete(fasta);
		}
	}
	
	@Override
	public Database makeBlastDB(Fasta inputFasta, String outputDBName)
	throws ExecutionException, IllegalArgumentException, EntityAlreadyExistsException, IOException, InterruptedException {
		final DatabaseRepositoryManager databaseManager = this.repositoryManager.database();
		
		if (databaseManager.exists(inputFasta.getType(), outputDBName)) {
			throw new IllegalArgumentException("Database already exists: " + outputDBName);
		} else {
			final Database database = databaseManager.create(
				inputFasta.getType(), outputDBName
			);
		
			try {
				final ExecutionResult result = this.blastBinariesExecutor.executeMakeBlastDB(inputFasta.getFile(), database);
				
				if (result.getExitStatus() != 0) {
					databaseManager.delete(database);
					throw new ExecutionException(result.getExitStatus(), "Error executing makeBlastDB. Please, check error log.", "");
				}
				
				databaseManager.validate(database);
				
				return database;
			} catch (EntityValidationException e) {
				throw new ExecutionException(0, "Error executing makeBlastDB. Please, check error log", "");
			} finally {
				if (!databaseManager.exists(database)) {
					databaseManager.delete(database);
				}
			}
		}
	}
	
	@Override
	public Database blastdbAliasTool(Database[] databases, String outputDBName)
	throws EntityAlreadyExistsException, IOException, InterruptedException, ExecutionException {
		if (databases.length == 0)
			throw new IllegalArgumentException("databases can't be empty");
		
		// Database array validation and sequence type inference
		SequenceType sequenceType = null;
		for (Database database : databases) {
			if (database == null)
				throw new NullPointerException("databases can't contain null values");
			
			if (sequenceType == null) {
				sequenceType = database.getType();
			} else if (sequenceType != database.getType()) {
				throw new IllegalArgumentException("databases can't contain different sequence types");
			}
		}
		
		final DatabaseRepositoryManager databaseManager = this.repositoryManager.database();
		if (databaseManager.exists(sequenceType, outputDBName)) {
			throw new IllegalArgumentException("Database already exists: " + outputDBName);
		} else {
			final Database database = databaseManager.create(
				sequenceType, outputDBName
			);
			
			try {
				this.blastBinariesExecutor.executeBlastDBAliasTool(database, databases);
				
				return database;
			} finally {
				if (!databaseManager.exists(database)) {
					databaseManager.delete(database);
				}
			}
		}
	}
	
	@Override
	public SearchEntry retrieveSearchEntry(Database database, String accession)
	throws InterruptedException, ExecutionException, IOException {
		final SearchEntryRepositoryManager searchEntryManager = 
			this.repositoryManager.searchEntry();

		boolean dbHasAccession = false;
		for (String dbAccession : database.listAccessions()) {
			if (dbAccession.equalsIgnoreCase(accession)) {
				dbHasAccession = true;
				break;
			}
		}
		
		if (!dbHasAccession)
			throw new IllegalArgumentException(
				"Database " + database.getName() + " does not have accession " + accession
			);
		
		final SearchEntry entry = searchEntryManager
			.get(database.getType(), database.getName());
		
		try {
			this.blastBinariesExecutor.executeBlastDBCMD(database, entry, accession);
			
			return entry;
		} finally {
			if (!searchEntryManager.exists(entry)) {
				searchEntryManager.delete(entry);
			}
		}
	}
	
	@Override
	public NucleotideExport blastn(
		NucleotideDatabase database,
		File queryFile, 
		BigDecimal expectedValue, 
		boolean filter, 
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.repositoryManager.fasta().validateEntityPath(SequenceType.NUCLEOTIDE, queryFile);
			
			this.blastBinariesExecutor.executeBlastN(database, queryFile, export, expectedValue, filter, outputName, additionalParameters);
			
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} catch (EntityValidationException e) {
			throw new IOException("Invalid query file: " + queryFile.getAbsolutePath());
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public NucleotideExport blastn(
		NucleotideDatabase database, 
		NucleotideQuery query, 
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.blastBinariesExecutor.executeBlastN(database, query, export, expectedValue, filter, outputName, additionalParameters);
			
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}

	@Override
	public ProteinExport blastp(
		ProteinDatabase database,
		File queryFile, 
		BigDecimal expectedValue, 
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final ProteinExport export = exportManager.getProtein(
			database.getName()
		);
		
		try {
			this.repositoryManager.fasta().validateEntityPath(SequenceType.PROTEIN, queryFile);
			this.blastBinariesExecutor.executeBlastP(
				database, queryFile, export, expectedValue, filter, outputName, additionalParameters
			);

			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} catch (EntityValidationException e) {
			throw new IOException("Invalid query file: " + queryFile.getAbsolutePath());
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}

	@Override
	public ProteinExport blastp(
		ProteinDatabase database,
		ProteinQuery query, 
		BigDecimal expectedValue, 
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final ProteinExport export = exportManager.getProtein(
			database.getName()
		);
		
		try {
			this.blastBinariesExecutor.executeBlastP(
				database, query, export, expectedValue, filter, outputName, additionalParameters
			);

			this.generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public NucleotideExport tblastx(
		NucleotideDatabase database, 
		File queryFile, 
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.repositoryManager.fasta().validateEntityPath(SequenceType.NUCLEOTIDE, queryFile);
			
			this.blastBinariesExecutor.executeTBlastX(database, queryFile, export, expectedValue, filter, outputName, additionalParameters);
				
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
				
			return export;
		} catch (EntityValidationException e) {
			throw new IOException("Invalid query file: " + queryFile.getAbsolutePath());
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public NucleotideExport tblastx(
		NucleotideDatabase database, 
		NucleotideQuery query, 
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.blastBinariesExecutor.executeTBlastX(database, query, export, expectedValue, filter, outputName, additionalParameters);
			
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public NucleotideExport tblastn(
		NucleotideDatabase database, 
		File queryFile, 
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.repositoryManager.fasta().validateEntityPath(SequenceType.PROTEIN, queryFile);
			
			this.blastBinariesExecutor.executeTBlastN(database, queryFile, export, expectedValue, filter, outputName, additionalParameters);
			
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} catch (EntityValidationException e) {
			throw new IOException("Invalid query file: " + queryFile.getAbsolutePath());
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public NucleotideExport tblastn(
		NucleotideDatabase database, 
		ProteinQuery query, 
		BigDecimal expectedValue,
		boolean filter,
		boolean keepSingleSequenceFiles,
		String outputName,
		Map<String, String> additionalParameters
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException {
		final ExportRepositoryManager exportManager = this.repositoryManager.export();
		final NucleotideExport export = exportManager.getNucleotide(
			database.getName()
		);
		
		try {
			this.blastBinariesExecutor.executeTBlastN(database, query, export, expectedValue, filter, outputName, additionalParameters);
			
			generateExportEntry(database, outputName, export, keepSingleSequenceFiles);
			
			return export;
		} finally {
			if (!exportManager.exists(export)) {
				exportManager.delete(export);
			}
		}
	}
	
	@Override
	public Map<String, String> getBlastAdditionalParameters(BLASTType blastType) {
		return this.blastBinariesExecutor.getBlastAdditionalParameters(blastType);
	}

	private void generateExportEntry(
		final Database database,
		final String outputName, 
		final Export export,
		final boolean keepSingleSequenceFiles
	) throws InterruptedException, ExecutionException, IOException {
		final ExportEntry exportEntry = export.getExportEntry(outputName);
		
		if (exportEntry == null) {
			throw new IllegalStateException("Missing output file");
		} else {
			final List<String> alignments = 
				this.blastBinariesExecutor.extractSignificantSequences(exportEntry);
			
			for (String alignment : alignments) {
				this.blastBinariesExecutor.executeBlastDBCMD(
					database, exportEntry, alignment
				);
			}
			
			for (File fastas : exportEntry.getSequenceFiles()) {
				FileUtils.write(
					exportEntry.getSummaryFastaFile(), 
					FileUtils.readFileToString(fastas), 
					true
				);
			}
			
			if (!keepSingleSequenceFiles)
				exportEntry.deleteSequenceFiles();
		}
	}
	
	@Override
	public NucleotideFasta getORF(
		NucleotideFasta fasta,
		int minSize,
		int maxSize,
		boolean noNewLines,
		String outputName
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException, FastaParseException {
		final FastaRepositoryManager fastaManager = this.repositoryManager.fasta();
		final NucleotideFasta orf = fastaManager.getNucleotide(outputName);
		
		if (fastaManager.exists(orf)) {
			throw new IllegalArgumentException("ORF already exists: " + outputName);
		} else {
			try {
				this.embossBinariesExecutor.executeGetORF(fasta, orf, minSize, maxSize);
				if (noNewLines) {
					this.reformatFasta(RenameMode.NONE, orf, 0, null);
				}
				
				return orf;
			} finally {
				if (!fastaManager.exists(orf))
					fastaManager.delete(orf);
			}
		}
	}

	@Override
	public NucleotideFasta splignCompart(
		NucleotideFasta genomeFasta,
		NucleotideFasta cdsFasta,
		boolean concatenateExons,
		String outputName
	) throws IOException, InterruptedException, ExecutionException, IllegalStateException, FastaParseException {
		final FastaRepositoryManager fastaManager = this.repositoryManager.fasta();
		final NucleotideFasta fasta = fastaManager.getNucleotide(outputName);
		
		if (fastaManager.exists(fasta)) {
			throw new IllegalArgumentException("Fasta already exists: " + outputName);
		} else {
			final SplignCompartPipeline pipeline = new SplignCompartPipeline(
				this.bedToolsBinariesExecutor,
				this.splignBinariesExecutor,
				this.compartBinariesExecutor,
				this.blastBinariesExecutor,
				this.embossBinariesExecutor
			);
			
			final ExecutionResult result = pipeline.splignCompart(
				genomeFasta, cdsFasta, fasta, concatenateExons
			);
			
			if (result != null && result.getExitStatus() != 0) {
				if (fastaManager.exists(fasta))
					fastaManager.delete(fasta);
				
				throw new ExecutionException(result.getExitStatus(), "Error executing splignCompart", "splignCompart");
			} else {
				return fasta;
			}
		}
	}
	
	public void reformatFasta(
		RenameMode mode,
		Fasta fasta,
		int fragmentLength,
		Map<ReformatFastaParameters, Object> additionalParameters
	) throws FastaParseException, IOException {
		final Path tmpPath = Files.createTempFile("bdbm", "rename.fasta");
		
		try (final PrintWriter writer = new PrintWriter(tmpPath.toFile())) {
			FastaUtils.fastaSequenceRenaming(
				mode, fasta.getFile(), fragmentLength,
				writer, additionalParameters
			);
		}
		
		Files.move(tmpPath, fasta.getFile().toPath(), REPLACE_EXISTING);
	};

	@Override
	public void mergeFastas(Fasta[] fastas, String outputFastaName) throws FastaParseException, IOException {
		final FastaRepositoryManager fastaManager = this.repositoryManager.fasta();
		final Fasta outputFasta = fastas[0] instanceof NucleotideFasta ?
			fastaManager.createNucleotide(outputFastaName):
			fastaManager.createProtein(outputFastaName);
			
		if (fastaManager.exists(outputFasta)) {
			throw new IllegalArgumentException("Fasta file already exists: " + outputFastaName);
		} else {
			try {
				final File[] fastaFiles = new File[fastas.length];
				for (int i = 0; i < fastas.length; i++) {
					fastaFiles[i] = fastas[i].getFile();
				}
				
				FastaUtils.mergeFastas(fastaFiles, outputFasta.getFile());
			} finally {
				if (!fastaManager.exists(outputFasta))
					fastaManager.delete(outputFasta);
			}
		}
	}
}
