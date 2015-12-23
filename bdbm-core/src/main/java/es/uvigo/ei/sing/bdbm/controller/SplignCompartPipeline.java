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

import static es.uvigo.ei.sing.bdbm.fasta.FastaUtils.changeSequenceLength;
import static es.uvigo.ei.sing.bdbm.fasta.FastaUtils.prefixFastaSequenceRenaming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BedToolsBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.CompartBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.EMBOSSBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.environment.execution.SplignBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.BinariesExecutor.InputLineCallback;
import es.uvigo.ei.sing.bdbm.fasta.FastaParseException;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.util.DirectoryUtils;

public class SplignCompartPipeline {
	private static final String TO_BE_ERASED_SEQUENCE_NAME = "To_be_erased";

	private final static Logger LOG = LoggerFactory.getLogger(SplignCompartPipeline.class);
	
	private BedToolsBinariesExecutor btBinaries;
	private SplignBinariesExecutor sBinaries;
	private CompartBinariesExecutor cBinaries;
	private BLASTBinariesExecutor bBinaries;
	private EMBOSSBinariesExecutor eBinaries;
	
	public SplignCompartPipeline() {}

	public SplignCompartPipeline(
		BedToolsBinariesExecutor btBinaries,
		SplignBinariesExecutor sBinaries,
		CompartBinariesExecutor cBinaries,
		BLASTBinariesExecutor bBinaries,
		EMBOSSBinariesExecutor eBinaries
	) {
		this.btBinaries = btBinaries;
		this.sBinaries = sBinaries;
		this.cBinaries = cBinaries;
		this.bBinaries = bBinaries;
		this.eBinaries = eBinaries;
	}
	
	public void setBedToolsBinaries(BedToolsBinariesExecutor btBinaries) {
		this.btBinaries = btBinaries;
	}
	
	public void setSplignBinaries(SplignBinariesExecutor sBinaries) {
		this.sBinaries = sBinaries;
	}
	
	public void setCompartBinaries(CompartBinariesExecutor cBinaries) {
		this.cBinaries = cBinaries;
	}
	
	public void setBLASTBinariesExecutor(BLASTBinariesExecutor bBinaries) {
		this.bBinaries = bBinaries;
	}
	
	public void setEMBOSSBinaries(EMBOSSBinariesExecutor eBinaries) {
		this.eBinaries = eBinaries;
	}
	
	public ExecutionResult splignCompart(
		NucleotideFasta genomeFasta, 
		NucleotideFasta genesFasta,
		NucleotideFasta outputFasta,
		boolean concatenateExons
	) throws InterruptedException, ExecutionException, IOException, FastaParseException {
		try (final DirectoryManager dirManager = new DirectoryManager(
			genomeFasta, genesFasta, outputFasta
		)) {
			// Reversing and appending genome
			final ExecutionResult reverseAndMergeResult = reverseAndMerge(
				genomeFasta,
				dirManager.getReversedGenomeFile(),
				dirManager.getRenamedReversedGenomeFile(), dirManager.getBidirectionalGenomeFile());
			if (reverseAndMergeResult != null) {
				return reverseAndMergeResult;
			}
			
			this.checkReverseAndMergeOutput(dirManager.getBidirectionalGenomeFile());
			
			// Genome and genes database creation
			final ExecutionResult makeDBGenome = makeBlastDB(
				dirManager.getBidirectionalGenomeFile(), dirManager.getWorkingDirectory());
			if (makeDBGenome != null)
				return makeDBGenome;
			
			final ExecutionResult makeDBGenes = makeBlastDB(
				dirManager.getGenesFastaFile(), dirManager.getWorkingDirectory());
			if (makeDBGenes != null)
				return makeDBGenes;
			
			// SPLIGN-COMPART
			final ExecutionResult mklds = this.mklds(dirManager.getWorkingDirectoryPath());
			if (mklds != null)
				return mklds;
			
			final ExecutionResult compart = this.compart(
				dirManager.getGenesFastaPath(),
				dirManager.getBidirectionalGenomePath(),
				dirManager.getCompartmentsFile()
			);
			if (compart != null)
				return compart;
			
			final ExecutionResult ldsdir = this.ldsdir(
				dirManager.getWorkingDirectoryPath(),
				dirManager.getCompartmentsPath(),
				dirManager.getLdsdirFile()
			);
			if (ldsdir != null)
				return ldsdir;
			
			ldsdirToBed(dirManager.getLdsdirFile(), dirManager.getBedtoolsFile());

			final ExecutionResult bedtoolsResult = this.bedtools(
				dirManager.getBidirectionalGenomePath(),
				dirManager.getBedtoolsPath(),
				dirManager.getBedtoolsOutputPath()
			);
			if (bedtoolsResult != null)
				return bedtoolsResult;
			
			this.mergeSequences(
				dirManager.getBedtoolsOutputFile(),
				dirManager.getBedtoolsMergedOutputFile(),
				concatenateExons
			);
			
			FileUtils.moveFile(dirManager.getBedtoolsMergedOutputFile(), outputFasta.getFile());
			
			return null;
		}
	}
	
	protected ExecutionResult reverseAndMerge(
		NucleotideFasta fasta,
		File reversedFastaFile,
		File renamedReversedFastaFile,
		File bidirectionalFastaFile
	) throws InterruptedException, ExecutionException, FastaParseException, IOException {
		final ExecutionResult revseqResult = this.eBinaries.executeRevseq(
			fasta, new DefaultNucleotideFasta(reversedFastaFile)
		);
		if (revseqResult.getExitStatus() != 0) {
			return revseqResult;
		}
		
		try (PrintWriter fw = new PrintWriter(renamedReversedFastaFile)) {
			prefixFastaSequenceRenaming(
				reversedFastaFile, "Reversed", true, false, "_", fw);
		}
		
		try (PrintWriter pw = new PrintWriter(bidirectionalFastaFile)) {
			changeSequenceLength(fasta.getFile(), -1, pw);
			changeSequenceLength(renamedReversedFastaFile, -1, pw);
		}
		
		return null;
	}
	
	protected void checkReverseAndMergeOutput(File fastaFile)
	throws IOException {
		if (!fastaFile.exists()) {
			throw new IOException("Bidirectional genome file does not exists");
		} else if (fastaFile.length() == 0) {
			throw new IOException("Bidirectional genome file is empty");
		}
	}

	protected ExecutionResult makeBlastDB(
		final File fastaFile,
		final File databaseDirectory
	) throws InterruptedException, ExecutionException, IOException {
		
		final ExecutionResult result = this.bBinaries.executeMakeBlastDB(
			fastaFile,
			new DefaultNucleotideDatabase(databaseDirectory) {
				public String getName() {
					return fastaFile.getName();
				}
			}
		);
		
		return result.getExitStatus() != 0 ? result : null;
	}
	
	protected ExecutionResult mklds(
		String directoryPath
	) throws ExecutionException, InterruptedException {
		final ExecutionResult result = this.sBinaries.mklds(directoryPath);
		
		return result.getExitStatus() != 0 ? result : null;
	}

	protected ExecutionResult compart(
		String genesFastaPath,
		String genomeFastaPath,
		File compartmentsFile
	) throws ExecutionException, InterruptedException, IOException {
		final ExecutionResult result = this.cBinaries.compart(
			genesFastaPath,
			genomeFastaPath,
			new InputLineToFileCallback(compartmentsFile)
		);
		
		return result.getExitStatus() != 0 ? result : null;
	}

	protected ExecutionResult ldsdir(
		String workingDirectoryPath,
		String compartmentsPath,
		File ldsdirFile
	) throws ExecutionException, InterruptedException {
		final ExecutionResult result = this.sBinaries.ldsdir(
			workingDirectoryPath,
			compartmentsPath,
			new InputLineToFileCallback(ldsdirFile)
		);
		
		return result.getExitStatus() != 0 ? result : null;
	}

	protected void ldsdirToBed(File input, File output) throws IOException {
		try (final BufferedReader reader = new BufferedReader(new FileReader(input));
			final PrintWriter pw = new PrintWriter(output)
		) {
			String line;
			
			while ((line = reader.readLine()) != null && !line.equals("# END")) {
				final String[] split = line.split("\t");
				
				if (split.length == 11) {
					if (split[7].equals("-")) {
						final String info = split[2];
						pw.append(info).append("\t2\t3\t")
							.println(TO_BE_ERASED_SEQUENCE_NAME);
					} else {
						final Integer start = safeParseInt(split[7]) - 1;
						final Integer end = safeParseInt(split[8]);
						
						if (start < end) {
							final String name = split[1];
							final String info = split[2];
							pw.append(info).append('\t')
								.append(Integer.toString(start)).append('\t')
								.append(Integer.toString(end)).append('\t')
								.append(name).append('\n');
						}
					}
				}
			}
		}
	}

	protected ExecutionResult bedtools(
		String genomeFasta,
		String bedtoolsFasta,
		String bedtoolsOutputFile
	) throws ExecutionException, InterruptedException {
		final ExecutionResult result = this.btBinaries.getfasta(
			genomeFasta,
			bedtoolsFasta,
			bedtoolsOutputFile
		);
		
		return result.getExitStatus() != 0 ? result : null;
	}

	protected void mergeSequences(
		File inputFasta,
		File outputFasta,
		boolean concatenateExons
	) throws ExecutionException {
		try {
			final File tmpFile = File.createTempFile("bdbm", "tmp");
			
			try (final PrintWriter pw = new PrintWriter(tmpFile)) {
				if (concatenateExons) {
					FastaUtils.mergeConsecutiveSequences(inputFasta, pw, TO_BE_ERASED_SEQUENCE_NAME);
				} else {
					FastaUtils.removeSequences(inputFasta, pw, TO_BE_ERASED_SEQUENCE_NAME);
				}
			}
			
			try (PrintWriter pw = new PrintWriter(outputFasta)) {
				FastaUtils.prefixFastaSequenceRenaming(tmpFile, null, true, true, "-", pw);
			}
		} catch (Exception e) {
			throw new ExecutionException(-1, "Error cleaning final fasta file", e, null);
		}
	}
	
	private static Integer safeParseInt(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	protected static class DirectoryManager implements AutoCloseable {
		private final Path workingDirectory;
		private final Path genomeFastaFile;
		private final Path genesFastaFile;
		
		private final Path bidirectionalGenomeFile;
		private final Path reversedGenomeFile;
		private final Path renamedReversedGenomeFile;
		private final Path compartmentsFile;
		private final Path ldsdirFile;
		private final Path bedtoolsFile;
		private final Path bedtoolsOutputFile;
		private final Path bedtoolsMergedOutputFile;
		
		public DirectoryManager(
			NucleotideFasta genomeFasta,
			NucleotideFasta genesFasta,
			NucleotideFasta outputFasta
		) throws IOException {
			this.workingDirectory = Files.createTempDirectory("bdbm_spligncompart");
			final Path genomeFastaPath = genomeFasta.getFile().toPath();
			final Path genesFastaPath = genesFasta.getFile().toPath();
			
			this.genomeFastaFile = this.workingDirectory.resolve("genome");
			this.genesFastaFile = this.workingDirectory.resolve("genes");
			
			Files.createSymbolicLink(this.genomeFastaFile, genomeFastaPath);
			Files.createSymbolicLink(this.genesFastaFile, genesFastaPath);
			
			this.bidirectionalGenomeFile = this.workingDirectory.resolve("genome_bidirectional");
			this.reversedGenomeFile = this.workingDirectory.resolve("genome_reversed");
			this.renamedReversedGenomeFile = this.workingDirectory.resolve("genome_reversed_renamed");
			this.compartmentsFile = this.workingDirectory.resolve("compartments");
			this.ldsdirFile = this.workingDirectory.resolve("ldsdir");
			this.bedtoolsFile = this.workingDirectory.resolve("bedtools");
			this.bedtoolsOutputFile = this.workingDirectory.resolve("bedtools_output");
			this.bedtoolsMergedOutputFile = this.workingDirectory.resolve("bedtools_merged_output");
		}
		
		public String getBidirectionalGenomePath() {
			return this.bidirectionalGenomeFile.toAbsolutePath().toString();
		}

		public File getWorkingDirectory() {
			return this.workingDirectory.toFile();
		}

		public File getBedtoolsMergedOutputFile() {
			return this.bedtoolsMergedOutputFile.toFile();
		}

		public File getBedtoolsOutputFile() {
			return this.bedtoolsOutputFile.toFile();
		}

		public File getBedtoolsFile() {
			return this.bedtoolsFile.toFile();
		}

		public String getBedtoolsOutputPath() {
			return this.bedtoolsOutputFile.toAbsolutePath().toString();
		}
		
		public String getBedtoolsPath() {
			return this.bedtoolsFile.toAbsolutePath().toString();
		}
		
		public File getLdsdirFile() {
			return this.ldsdirFile.toFile();
		}
		
		public File getCompartmentsFile() {
			return this.compartmentsFile.toFile();
		}
		
		public String getCompartmentsPath() {
			return this.compartmentsFile.toAbsolutePath().toString();
		}

		public String getGenomeFastaPath() {
			return this.genomeFastaFile.toAbsolutePath().toString();
		}

		public String getGenesFastaPath() {
			return this.genesFastaFile.toAbsolutePath().toString();
		}

		public File getGenesFastaFile() {
			return this.genesFastaFile.toFile();
		}

		public String getWorkingDirectoryPath() {
			return this.workingDirectory.toAbsolutePath().toString();
		}

		public File getGenomeDatabaseDirectory() {
			return this.getBidirectionalGenomeFile();
		}
		
		public File getGenesDatabaseDirectory() {
			return this.getGenesFastaFile();
		}

		public File getBidirectionalGenomeFile() {
			return this.bidirectionalGenomeFile.toFile();
		}

		public File getReversedGenomeFile() {
			return this.reversedGenomeFile.toFile();
		}
		
		public File getRenamedReversedGenomeFile() {
			return this.renamedReversedGenomeFile.toFile();
		}

		@Override
		public void close() throws IOException {
			if (Boolean.valueOf(System.getProperty("spligncompart.deletetmpfiles", "true"))) {
				Files.deleteIfExists(this.bidirectionalGenomeFile);
				Files.deleteIfExists(this.reversedGenomeFile);
				Files.deleteIfExists(this.renamedReversedGenomeFile);
				Files.deleteIfExists(this.compartmentsFile);
				Files.deleteIfExists(this.ldsdirFile);
				Files.deleteIfExists(this.bedtoolsFile);
				Files.deleteIfExists(this.bedtoolsOutputFile);
				Files.deleteIfExists(this.bedtoolsMergedOutputFile);
				
				DirectoryUtils.deleteIfExists(this.workingDirectory);
			}
		}
	}

	private static final class InputLineToFileCallback implements InputLineCallback {
		private final File file;
		private PrintWriter pw;
		
		public InputLineToFileCallback(File file) {
			this.file = file;
		}

		@Override
		public void inputFinished() {
			if (this.pw != null)
				this.pw.close();
		}

		@Override
		public void inputStarted() {
			try {
				this.pw = new PrintWriter(this.file);
			} catch (FileNotFoundException e) {
				LOG.error("Error creating writer for file: " + this.file.getAbsolutePath(), e);
			}
		}

		@Override
		public void line(String line) {
			if (this.pw != null)
				this.pw.println(line);
		}

		@Override
		public void error(String message, Exception e) {
		}

		@Override
		public void info(String message) {
		}
	}
}
