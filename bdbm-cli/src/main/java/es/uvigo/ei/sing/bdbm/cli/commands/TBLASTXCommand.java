/*
 * #%L
 * BDBM CLI
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
package es.uvigo.ei.sing.bdbm.cli.commands;

import java.io.File;
import java.math.BigDecimal;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.BigDecimalOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.BooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideSearchEntry;
import es.uvigo.ei.sing.yaacli.Parameters;
import es.uvigo.ei.sing.yaacli.StringOption;

public class TBLASTXCommand extends BDBMCommand {
	public static final FileOption OPTION_DATABASE = 
		new FileOption(
			"Database", "db", "Database from which the search entry will be retrieved",
			false, true
		);
	public static final FileOption OPTION_QUERY =
		new FileOption(
			"Query", "query", "Fasta file to be used as query", 
			false, true
		);
	public static final BigDecimalOption OPTION_EXPECTED_VALUE =
		new BigDecimalOption(
			"Expected value", "evalue", "Expected value blastn parameter", "0.05"
		);
	public static final BooleanOption OPTION_FILTER = 
		new BooleanOption(
			"Filter", "filter", "Filter results", 
			false, true 
		);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", "output", "Output name", 
			false, true
		);
	public static final DefaultValueBooleanOption OPTION_KEEP_SINGLE_SEQUENCE_FILES = 
		new DefaultValueBooleanOption(
			"Keep single sequences", "keep_seqs", "Keep single sequence file", 
			false
		);
	
	public TBLASTXCommand() {
		super();
	}
	
	public TBLASTXCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "tblastx";
	}
	
	@Override
	public String getDescriptiveName() {
		return "TBLASTX";
	}

	@Override
	public String getDescription() {
		return "Performs a 'tblastx' search";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final File database = parameters.getSingleValue(OPTION_DATABASE);
		final File query = parameters.getSingleValue(OPTION_QUERY);
		final BigDecimal expectedValue = parameters.getSingleValue(OPTION_EXPECTED_VALUE);
		final Boolean filter = parameters.getSingleValue(OPTION_FILTER);
		final String outputName = parameters.getSingleValue(OPTION_OUTPUT_NAME);
		final boolean keepSingleSequences = parameters.getSingleValue(OPTION_KEEP_SINGLE_SEQUENCE_FILES);
		
		this.controller.tblastx(
			new DefaultNucleotideDatabase(database), 
			new DefaultNucleotideSearchEntry(query.getParentFile(), false).getQuery(query.getName()), 
			expectedValue, 
			filter, 
			keepSingleSequences,
			outputName
		);
	}
}