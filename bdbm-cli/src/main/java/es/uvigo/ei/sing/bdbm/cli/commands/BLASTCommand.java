/*-
 * #%L
 * BDBM CLI
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

package es.uvigo.ei.sing.bdbm.cli.commands;

import java.util.HashMap;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.yaacli.command.option.BigDecimalOption;
import es.uvigo.ei.sing.yaacli.command.option.BooleanOption;
import es.uvigo.ei.sing.yaacli.command.option.DefaultValueBooleanOption;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public abstract class BLASTCommand extends BDBMCommand {
	public static final String OPTION_DATABASE_SHORT_NAME = "db";
	public static final String OPTION_QUERY_SHORT_NAME = "query";
	public static final String OPTION_EXPECTED_SHORT_NAME = "evalue";
	public static final String OPTION_FILTER_SHORT_NAME = "filter";
	public static final String OPTION_OUTPUT_NAME_SHORT_NAME = "output";
	public static final String OPTION_KEEP_SINGLE_SEQUENCE_SHORT_NAME = "keep_seqs";
	
	public static final FileOption OPTION_DATABASE = 
		new FileOption(
			"Database", OPTION_DATABASE_SHORT_NAME, "Database from which the search entry will be retrieved",
			false, true
		);
	public static final FileOption OPTION_QUERY =
		new FileOption(
			"Query", OPTION_QUERY_SHORT_NAME, "FASTA file to be used as query", 
			false, true
		);
	public static final BigDecimalOption OPTION_EXPECTED_VALUE =
		new BigDecimalOption(
			"Expected value", OPTION_EXPECTED_SHORT_NAME, "Expected value blastn parameter", "0.05"
		);
	public static final BooleanOption OPTION_FILTER = 
		new BooleanOption(
			"Filter", OPTION_FILTER_SHORT_NAME, "Filter results", 
			false, true 
		);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", OPTION_OUTPUT_NAME_SHORT_NAME, "Output name", 
			false, true
		);
	public static final DefaultValueBooleanOption OPTION_KEEP_SINGLE_SEQUENCE_FILES = 
		new DefaultValueBooleanOption(
			"Keep single sequences", OPTION_KEEP_SINGLE_SEQUENCE_SHORT_NAME, "Keep single sequence file", 
			false
		);
		
	public BLASTCommand() {
		super();
	}

	public BLASTCommand(BDBMController controller) {
		super(controller);
	}
	
	protected Map<String, String> getAdditionalParameters(Parameters parameters) {
		final Map<String, String> additionalParameters = new HashMap<>();
		
		for (Option<?> option : parameters.listOptions()) {
			if (!isBlastOption(option) && !option.isMultiple()) {
				additionalParameters.put(
					option.getShortName(),
					option.requiresValue() ? parameters.getSingleValueString(option) : null
				);
			}
		}
		
		return additionalParameters;
	}
	
	protected boolean isBlastOption(Option<?> option) {
		return option.equals(OPTION_DATABASE)
			|| option.equals(OPTION_QUERY)
			|| option.equals(OPTION_EXPECTED_VALUE)
			|| option.equals(OPTION_OUTPUT_NAME)
			|| option.equals(OPTION_QUERY)
			|| option.equals(OPTION_KEEP_SINGLE_SEQUENCE_FILES);
	}
}
