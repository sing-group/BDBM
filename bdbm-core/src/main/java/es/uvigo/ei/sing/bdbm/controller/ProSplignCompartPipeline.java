/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import static java.nio.file.Files.createTempDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import es.uvigo.ei.sing.bdbm.environment.execution.BLASTBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionException;
import es.uvigo.ei.sing.bdbm.environment.execution.ExecutionResult;
import es.uvigo.ei.sing.bdbm.environment.execution.ProCompartBinariesExecutor;
import es.uvigo.ei.sing.bdbm.environment.execution.ProSplignBinariesExecutor;
import es.uvigo.ei.sing.bdbm.fasta.FastaParseException;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideBlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideBlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.bdbm.util.DirectoryUtils;
import es.uvigo.ei.sing.bdbm.util.ProSplignTxtParser;

public class ProSplignCompartPipeline {
	private ProSplignBinariesExecutor proSplignBinaries;
	private ProCompartBinariesExecutor proCompartBinaries;
	private BLASTBinariesExecutor bBinaries;
	
	public ProSplignCompartPipeline() {}

	public ProSplignCompartPipeline(
		ProSplignBinariesExecutor sBinaries,
		ProCompartBinariesExecutor cBinaries,
		BLASTBinariesExecutor bBinaries
	) {
		this.proSplignBinaries = sBinaries;
		this.proCompartBinaries = cBinaries;
		this.bBinaries = bBinaries;
	}
	public void setSplignBinaries(ProSplignBinariesExecutor sBinaries) {
		this.proSplignBinaries = sBinaries;
	}
	
	public void setCompartBinaries(ProCompartBinariesExecutor cBinaries) {
		this.proCompartBinaries = cBinaries;
	}
	
	public void setBLASTBinariesExecutor(BLASTBinariesExecutor bBinaries) {
		this.bBinaries = bBinaries;
	}

	public ExecutionResult proSplignCompart(
		NucleotideFasta nucleotideFasta, 
		ProteinFasta queryFasta,
		NucleotideFasta outputFasta,
		NucleotideExport outputExportDirectory,
		int maxTargetSeqs
	) throws InterruptedException, ExecutionException, IOException, FastaParseException {
		try (final DirectoryManager dirManager = new DirectoryManager(
			nucleotideFasta, queryFasta
		)) {
			prepareNucleotides(nucleotideFasta.getFile(),
				dirManager.getPreparedNucleotidesFile(),
				dirManager.getSubjectMapFile()
			);

			prepareQueryFile(queryFasta.getFile(),
				dirManager.getPreparedQueryFile(),
				dirManager.getQueryMapFile()
			);

			File workingNucleotidesFasta = dirManager.getPreparedNucleotidesFile();
			File workingQueryFasta = dirManager.getPreparedQueryFile();

			NucleotideDatabase database = new DefaultNucleotideDatabase(
				dirManager.getWorkingDirectory()) {
				@Override
				public String getName() {
					return workingNucleotidesFasta.getName();
				}
			};

			final ExecutionResult makeDBGenome = makeBlastDB(
				workingNucleotidesFasta, database);
			if (makeDBGenome != null)
				return makeDBGenome;

			NucleotideBlastResults nucleotideExport = new DefaultNucleotideBlastResults(
				dirManager.getTblastNExportFile());

			NucleotideDatabase database2 = new DefaultNucleotideDatabase(
				new File(dirManager.getWorkingDirectory() + "/"
					+ workingNucleotidesFasta.getName()));

			tblastn(workingQueryFasta, database2, nucleotideExport, maxTargetSeqs);

			sortTblastNOutputFile(dirManager.getTblastNOutputFile());
			
			proCompart(dirManager.getTblastNOutputFile(),
				dirManager.getProCompartOutputFile());

			proSplign(dirManager.getProCompartOutputFile(),
				workingNucleotidesFasta,
				workingQueryFasta,
				dirManager.getWorkingDirectory(), "pro");
			
			extractSequences(dirManager.getProSplignOutputTxtFile(),
				dirManager.getProSplignOutputSequences(),
				dirManager.getProSplignOutputCompleteSequences(),
				dirManager.getQueryMapFile(),
				dirManager.getSubjectMapFile()
			);
			
			FileUtils.moveFile(dirManager.getProSplignOutputSequences(), outputFasta.getFile());
			FileUtils.moveFile(dirManager.getProSplignOutputCompleteSequences(), new File(outputExportDirectory.getFile(), dirManager.getProSplignOutputCompleteSequences().getName()));
			FileUtils.moveFile(dirManager.getProSplignOutputTxtFile(), new File(outputExportDirectory.getFile(), dirManager.getProSplignOutputTxtFile().getName()));

			return null;
		}
	}
	
	private static void extractSequences(File proSplignOutputTxtFile,
		File proSplignOutputSequences, File proSplignOutputCompleteSequences,
		File queryMappingFile, File subjectMappingFile
	) {
		try {
			Map<String, String> queryMapping = loadQueryMappingFile(queryMappingFile);
			Map<String, String> subjectMapping = loadSubjectMappingFile(subjectMappingFile);
			
			ProSplignTxtParser parser = new ProSplignTxtParser();
			parser.parse(proSplignOutputTxtFile.toPath(), queryMapping, subjectMapping);
			
			List<String> sequences = parser.getSequences();
			FileUtils.writeLines(proSplignOutputSequences, sequences, false);
			
			
			List<String> completeSequences = parser.getFullSequences();
			FileUtils.writeLines(proSplignOutputCompleteSequences, completeSequences, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, String> loadSubjectMappingFile(File subjectMappingFile) {
		return loadQueryMappingFile(subjectMappingFile);
	}

	private static Map<String, String> loadQueryMappingFile(File queryMappingFile) {
		try {
			return Files.readAllLines(queryMappingFile.toPath()).stream()
				.map(line -> line.split("\t"))
				.collect(Collectors.toMap(s -> s[1], s -> s[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	private void prepareQueryFile(File queryFile,
		File preparedQueryFile, File queryMappingFile) {
		try (
			BufferedReader inNucleotidesFile = new BufferedReader(
				new FileReader(queryFile));
			FileWriter outNucleotidesFile = new FileWriter(
				preparedQueryFile);
			FileWriter outMappingFile = new FileWriter(
				queryMappingFile);
			) {

			Map<String, String> queryMapping = new HashMap<>();
			String line;
			int count = 1;

			while((line = inNucleotidesFile.readLine()) != null) {
				if(line.startsWith(">")) {
					queryMapping.put(line.replace(">", ""), Integer.toString(count));
					outNucleotidesFile.write(">" + (count++));
				} else {
					outNucleotidesFile.write(line);
				}
				outNucleotidesFile.write("\n");
				if(count % 100 == 0) {
					outNucleotidesFile.flush();
				}
			}
			
			for(Entry<String, String> e : queryMapping.entrySet()) {
				outMappingFile.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void prepareNucleotides(File nucleotidesFile,
		File preparedNucleotidesFile, File subjectMapFile) {
		try (
			BufferedReader inNucleotidesFile = new BufferedReader(
				new FileReader(nucleotidesFile));
			FileWriter outNucleotidesFile = new FileWriter(
				preparedNucleotidesFile);
			FileWriter outSubjectMapFile = new FileWriter(
				subjectMapFile);
		) {
			Map<String, String> subjectMapping = new HashMap<>();
			String line;
			int count = 1;

			while((line = inNucleotidesFile.readLine()) != null) {
				if(line.startsWith(">")) {
					subjectMapping.put(line.replace(">", ""), Integer.toString(count));
					outNucleotidesFile.write(">gi|" + (count++) + "|");
				} else {
					outNucleotidesFile.write(line);
				}
				outNucleotidesFile.write("\n");
				if(count % 100 == 0) {
					outNucleotidesFile.flush();
				}
			}

			for(Entry<String, String> e : subjectMapping.entrySet()) {
				outSubjectMapFile.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
			inNucleotidesFile.close();
			outNucleotidesFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void sortTblastNOutputFile(File tblastNOutputFile) {
		try (Stream<String> fileStream = Files.lines(tblastNOutputFile.toPath())) {
			final String[] sortedLines = fileStream
				.map(line -> line.split("\t"))
				.sorted((line1, line2) -> compareLines(line1, line2))
				.map(line -> String.join("\t", line))
				.toArray(String[]::new);
			
			FileUtils.writeLines(tblastNOutputFile, Arrays.asList(sortedLines), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int compareLines(String[] line1, String[] line2) {
		if(line1[2].equals(line2[2])) {
			return line1[1].compareTo(line2[1]);
		} else {
			return line1[2].compareTo(line2[2]);
		}
	}

	private void proCompart(File tblastNDatabaseFile,
		File proCompartOutputfile) throws ExecutionException, InterruptedException {
		this.proCompartBinaries.procompart(tblastNDatabaseFile, proCompartOutputfile);
	}

	private void proSplign(File compart, File nucleotidesFasta,
		File proteinQueryFasta, File outputDir, String outputFileName)
		throws ExecutionException, InterruptedException {
		this.proSplignBinaries.proSplign(compart, nucleotidesFasta,
			proteinQueryFasta, outputDir, outputFileName);
	}

	protected ExecutionResult makeBlastDB(
		final File fastaFile,
		final NucleotideDatabase database
	) throws InterruptedException, ExecutionException, IOException {
		
		final ExecutionResult result = this.bBinaries.executeMakeBlastDB(
			fastaFile,
			database,
			false
		);
		
		return result.getExitStatus() != 0 ? result : null;
	}
	
  protected ExecutionResult tblastn(
	  final File queryFile,
	  final NucleotideDatabase database, 
	  NucleotideBlastResults nucleotideExport,
	  int maxTargetSeqs
  ) throws IllegalStateException,
    IOException, InterruptedException, ExecutionException {
	  ProteinQuery query = new DefaultProteinSearchEntry(queryFile.getParentFile(), false).getQuery(queryFile.getName());

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("outfmt", "6");
		parameters.put("max_target_seqs", String.valueOf(maxTargetSeqs));

		return this.bBinaries.executeTBlastN(database, query, nucleotideExport,
			new BigDecimal("0.05"), true, "tblastn", parameters, false);
	}
	
	protected static class DirectoryManager implements AutoCloseable {
		private final Path workingDirectory;
		private NucleotideFasta nucleotideFasta;
		private ProteinFasta queryProteinFasta;

		public DirectoryManager(NucleotideFasta nucleotideFasta,
			ProteinFasta queryProteinFasta) throws IOException {
			this.workingDirectory = createTempDirectory("bdbm_prospligncompart");
			this.nucleotideFasta = nucleotideFasta;
			this.queryProteinFasta = queryProteinFasta;
		}

		public File getPreparedNucleotidesFile() {
			return new File(this.workingDirectory.toFile(), "subject.fa");
		}

		public File getPreparedQueryFile() {
			return new File(this.workingDirectory.toFile(), "query.fa");
		}
		
		public File getQueryMapFile() {
			return new File(this.workingDirectory.toFile(), "query-mapping");
		}

		public File getSubjectMapFile() {
			return new File(this.workingDirectory.toFile(), "subject-mapping");
		}

		public File getProSplignOutputSequences() {
			return new File(this.workingDirectory.toFile(), "pro.fasta");
		}

		public File getProSplignOutputCompleteSequences() {
			return new File(this.workingDirectory.toFile(), "pro-complete-sequences.fasta");
		}

		public File getProCompartOutputFile() {
			return new File(this.workingDirectory.toFile(), "comp");
		}

		public File getProSplignOutputTxtFile() {
			return new File(this.workingDirectory.toFile(), "pro.txt");
		}

		public File getProSplignOutputAsnFile() {
			return new File(this.workingDirectory.toFile(), "pro.asn");
		}

		public File getTblastNOutputFile() {
			return new File(getTblastNExportFile(), "tblastn/tblastn.out");
		}

		public File getProteinQueryFile() {
			return this.queryProteinFasta.getFile();
		}

		public File getNucleotidesDbFile() {
			return new File(this.workingDirectory.toFile(), nucleotideFasta.getName());
		}

		public File getWorkingDirectory() {
			return this.workingDirectory.toFile();
		}

		public File getTblastNExportFile() {
			return this.workingDirectory.toFile();
		}

		@Override
		public void close() throws IOException {
			if (Boolean.valueOf(System.getProperty("prospligncompart.deletetmpfiles", "true"))) {
				DirectoryUtils.deleteIfExists(this.workingDirectory);
			}
		}
	}
}
