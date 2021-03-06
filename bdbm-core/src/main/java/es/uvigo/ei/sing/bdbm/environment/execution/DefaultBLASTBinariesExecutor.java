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

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTBinaries;
import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTType;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults.BlastResultsEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideBlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinBlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;

public class DefaultBLASTBinariesExecutor
extends AbstractBinariesExecutor<BLASTBinaries>
implements BLASTBinariesExecutor {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultBLASTBinariesExecutor.class);

	private final static String[] BLAST_UNMODIFIABLE_PARAMS = { "query", "db", "evalue", "out", "filter" };

	public DefaultBLASTBinariesExecutor() {}

	public DefaultBLASTBinariesExecutor(BLASTBinaries bBinaries)
	throws BinaryCheckException {
		this.setBinaries(bBinaries);
	}

	@Override
	public void setBinaries(BLASTBinaries binaries)
	throws BinaryCheckException {
		DefaultBLASTBinariesChecker.checkAll(binaries);

		super.setBinaries(binaries);
	}

	@Override
	public boolean checkBLASTBinaries(BLASTBinaries bBinaries) {
		try {
			DefaultBLASTBinariesChecker.checkAll(bBinaries);

			return true;
		} catch (BinaryCheckException bce) {
			return false;
		}
	}

	@Override
	public ExecutionResult executeMakeBlastDB(
		File inputFasta, Database database
		) throws InterruptedException, ExecutionException {
		return executeMakeBlastDB(inputFasta, database, true);
	}

	@Override
	public ExecutionResult executeMakeBlastDB(
		File inputFasta, Database database, boolean parseSeqIds
	) throws InterruptedException, ExecutionException {
		if(parseSeqIds) {
		return executeCommand(
			LOG,
			this.binaries.getMakeBlastDB(),
			"-in", inputFasta.getAbsolutePath(),
			"-parse_seqids",
			"-dbtype", database.getType().getDBType(),
			"-out", new File(database.getDirectory(), database.getName()).getAbsolutePath()
		);
		}
		else {
			return executeCommand(
				LOG,
				this.binaries.getMakeBlastDB(),
				"-in", inputFasta.getAbsolutePath(),
				"-dbtype", database.getType().getDBType(),
				"-out", new File(database.getDirectory(), database.getName()).getAbsolutePath()
			);
		}
	}

	@Override
	public ExecutionResult executeBlastDBAliasTool(
		Database database, Database[] databases
	) throws InterruptedException, ExecutionException, IOException {
		final StringBuilder sbDBList = new StringBuilder();

		for (Database inputDB : databases) {
			if (sbDBList.length() > 0) sbDBList.append(' ');

			final File inputFile = new File(inputDB.getDirectory(), inputDB.getName());
			sbDBList.append(inputFile.getAbsolutePath());
		}

		final File dbFile = new File(database.getDirectory(), database.getName());
		if (!dbFile.isDirectory() && !dbFile.mkdirs())
			throw new IOException("Database directory could not be created: " + dbFile);

		return executeCommand(
			LOG,
			this.binaries.getBlastDBAliasTool(),
			"-dblist", sbDBList.toString(),
			"-dbtype", database.getType().getDBType(),
			"-out", dbFile.getAbsolutePath(),
			"-title", database.getName()
		);
	}

	@Override
	public ExecutionResult executeBlastDBCMD(
		Database database, SearchEntry searchEntry, String entry
	) throws InterruptedException, ExecutionException {
		return executeCommand(
			LOG,
			this.binaries.getBlastDBCmd(),
			"-db", new File(database.getDirectory(), database.getName()).getAbsolutePath(),
			"-entry", entry,
			"-outfmt", "%f",
			"-out",	new File(searchEntry.getDirectory(), entry + ".txt").getAbsolutePath()
		);
	}

	@Override
	public ExecutionResult executeBlastDBCMD(
		Database database, BlastResultsEntry blastResultsEntry, String entry
	) throws InterruptedException, ExecutionException {
		return executeCommand(
			LOG,
			this.binaries.getBlastDBCmd(),
			"-db", database.getDirectory().getAbsolutePath(),
			"-entry", entry,
			"-outfmt", "%f",
			"-out",	new File(blastResultsEntry.getBaseFile(), entry + ".txt").getAbsolutePath()
		);
	}

	private ExecutionResult executeBlast(
		BLASTType blastType,
		Database database,
		File queryFile,
		BlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return executeBlast(
			blastType,
			database,
			queryFile,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	private ExecutionResult executeBlast(
		BLASTType blastType,
		Database database,
		File queryFile,
		BlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters,
		boolean useConfigurationParameters
	) throws InterruptedException, ExecutionException, IOException {
		final File outDirectory = new File(blastResults.getBaseFile(), outputName);
		final File outFile = new File(outDirectory, outputName + ".out");

		if (!outDirectory.isDirectory() && !outDirectory.mkdirs()) {
			throw new IOException("Output directory could not be created: " + outDirectory);
		}
		final List<String> parameters = getBlastAdditionalParameters(blastType, additionalParameters, useConfigurationParameters);
		parameters.addAll(asList(
			"-query", queryFile.getAbsolutePath(),
			"-db", database.getDirectory().getAbsolutePath(),
			"-evalue", expectedValue.toPlainString(),
			blastType.getFilterParam(), filter ? "yes" : "no",
			"-out", outFile.getAbsolutePath()
		));

		return executeCommand(
			LOG,
			this.binaries.getBlast(blastType),
			parameters.toArray(new String[parameters.size()])
		);
	}

	@Override
	public Map<String, String> getBlastAdditionalParameters(BLASTType blastType) {
		final Map<String, String> parameters = new HashMap<>();

		final Set<String> invalidParams = new HashSet<>(asList(BLAST_UNMODIFIABLE_PARAMS));
		invalidParams.add(blastType.getFilterParam());

		final Map<String, String> configParams = this.binaries.getConfigurationParameters();
		// General params
		for (Map.Entry<String, String> entry : configParams.entrySet()) {
			final String param = entry.getKey();

			if (!invalidParams.contains(param) && !isABlastTypeAdditionalParam(param)) {
				final String value = entry.getValue().trim();
				parameters.put(param, value.isEmpty() ? null : value);
			}
		}

		// Specific params have preference over general params.
		final String blastPrefix = blastType.configName() + ".";
		for (Map.Entry<String, String> entry : configParams.entrySet()) {
			final String fullParam = entry.getKey();

			if (fullParam.startsWith(blastPrefix)) {
				final String param = fullParam.substring(blastPrefix.length());

				if (!invalidParams.contains(param)) {
					final String value = entry.getValue().trim();
					parameters.put(param, value.isEmpty() ? null : value);
				}
			}
		}

		return parameters;
	}

	private List<String> getBlastAdditionalParameters(
		BLASTType blastType,
		Map<String, String> additionalParameters,
		boolean useConfigurationParameters
	) {
		final Map<String, String> parameters = new HashMap<>();
		if (useConfigurationParameters) {
			parameters.putAll(getBlastAdditionalParameters(blastType));
		}

		final Set<String> invalidParams = new HashSet<>(asList(BLAST_UNMODIFIABLE_PARAMS));
		invalidParams.add(blastType.getFilterParam());

		// Additional params have preference over general and specific params.
		for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
			final String param = entry.getKey();

			if (!invalidParams.contains(param) && !isABlastTypeAdditionalParam(param)) {
				parameters.put(param, entry.getValue());
			}
		}

		final List<String> additionalParams = new ArrayList<>();
		for (Map.Entry<String, String> param : parameters.entrySet()) {
			additionalParams.add("-" + param.getKey());

			if (param.getValue() != null)
				additionalParams.add(param.getValue());
		}

		return additionalParams;
	}

	private static boolean isABlastTypeAdditionalParam(String param) {
		for (BLASTType type : BLASTType.values()) {
			if (param.startsWith(type.configName() + ".")) {
				return true;
			}
		}

		return false;
	}

	private ExecutionResult executeBlast(
		BLASTType blastType,
		Database database,
		SearchEntry.Query query,
		BlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return executeBlast(
			blastType,
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters,
			true
		);
	}

	private ExecutionResult executeBlast(
		BLASTType blastType,
		Database database,
		SearchEntry.Query query,
		BlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters,
		boolean useConfigurationParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			blastType,
			database,
			query.getBaseFile(),
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters,
			useConfigurationParameters
		);
	}

	@Override
	public ExecutionResult executeBlastN(
		NucleotideDatabase database,
		File queryFile,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.BLASTN,
			database,
			queryFile,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeBlastN(
		NucleotideDatabase database,
		NucleotideSearchEntry.NucleotideQuery query,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.BLASTN,
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeBlastP(
		ProteinDatabase database,
		File queryFile,
		ProteinBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.BLASTP,
			database,
			queryFile,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeBlastP(
		ProteinDatabase database,
		ProteinQuery query,
		ProteinBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.BLASTP,
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeTBlastX(
		NucleotideDatabase database,
		File queryFile,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException ,ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.TBLASTX,
			database,
			queryFile,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeTBlastX(
		NucleotideDatabase database,
		NucleotideSearchEntry.NucleotideQuery query,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException ,ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.TBLASTX,
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeTBlastN(
		NucleotideDatabase database,
		File queryFile,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.TBLASTN,
			database,
			queryFile,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters
		);
	}

	@Override
	public ExecutionResult executeTBlastN(
		NucleotideDatabase database,
		ProteinSearchEntry.ProteinQuery query,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters
	) throws InterruptedException, ExecutionException, IOException {
		return executeTBlastN(
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters,
			true
		);
	}

	@Override
	public ExecutionResult executeTBlastN(
		NucleotideDatabase database,
		ProteinSearchEntry.ProteinQuery query,
		NucleotideBlastResults blastResults,
		BigDecimal expectedValue,
		boolean filter,
		String outputName,
		Map<String, String> additionalParameters,
		boolean useConfigurationParameters
	) throws InterruptedException, ExecutionException, IOException {
		return this.executeBlast(
			BLASTType.TBLASTN,
			database,
			query,
			blastResults,
			expectedValue,
			filter,
			outputName,
			additionalParameters,
			useConfigurationParameters
		);
	}

	@Override
	public List<String> extractSignificantSequences(BlastResultsEntry entry)
	throws IOException {
		final Set<String> alignments = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(entry.getOutFile()));) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(">")) {
					alignments.add(FastaUtils.extractFastaSequenceName(line));
				}
			}
		} catch (FileNotFoundException e) {
			LOG.warn("File not found: " + entry.getOutFile(), e);
			throw e;
		} catch (IOException e) {
			LOG.warn("Error reading file: " + entry.getOutFile(), e);
			throw e;
		}

		return new ArrayList<>(alignments);
	}
}
