package es.uvigo.ei.sing.bdbm.cli.commands;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.BigDecimalOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.BooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;

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
			"Query", OPTION_QUERY_SHORT_NAME, "Fasta file to be used as query", 
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
}
