/*
 * #%L
 * BDBM CLI
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
package es.uvigo.ei.sing.bdbm.cli.commands;

import static es.uvigo.ei.sing.bdbm.persistence.entities.AbstractFasta.newFasta;
import static java.lang.String.format;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.BooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.EnumOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.IntegerOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.fasta.ReformatFastaParameters;
import es.uvigo.ei.sing.bdbm.fasta.naming.FastaSequenceRenameMode;
import es.uvigo.ei.sing.yaacli.command.option.DefaultValuedStringOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringConstructedOption;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ReformatFastaCommand extends BDBMCommand {
	public static final String OPTION_FASTA_TYPE_SHORT_NAME = "fastatype";
	public static final String OPTION_FASTA_SHORT_NAME = "fastatype";
	public static final String OPTION_FRAGMENT_LENGTH_SHORT_NAME = "fragment_length";
	public static final String OPTION_REMOVE_LINE_BREAKS_SHORT_NAME = "remove_line_breaks";
	public static final String OPTION_RENAMING_MODE_SHORT_NAME = "mode";
	public static final String OPTION_INDEXES_SHORT_NAME = "indexes";
	public static final String OPTION_PREFIX_SHORT_NAME = "prefix";
	public static final String OPTION_KEEP_NAMES_WHEN_PREFIX_SHORT_NAME = "keep_names";
	public static final String OPTION_ADD_INDEX_WHEN_PREFIX_SHORT_NAME = "add_index";
	public static final String OPTION_JOINER_STRING_SHORT_NAME = "joiner_string";
	public static final String OPTION_DELIMITER_STRING_NAME = "separator_string";
	public static final String OPTION_KEEP_DESCRIPTION_NAME = "keep_description";
	
	public static final Option<SequenceType> OPTION_FASTA_TYPE = 
		new Option<SequenceType>(
			"FASTA Type", OPTION_FASTA_TYPE_SHORT_NAME,
			"FASTA type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	
	public static final FileOption OPTION_FASTA = 
		new FileOption(
			"FASTA", OPTION_FASTA_SHORT_NAME, "Source FASTA file",
			false, true
		);
	
	public static final IntegerOption OPTION_FRAGMENT_LENGTH = 
		new IntegerOption(
			"Sequence Fragment Length", OPTION_FRAGMENT_LENGTH_SHORT_NAME, 
			"Length of the sequence fragments. Requires a positive value. This option can't be used together with 'Remove Line Breaks'.", 
			80
		);
	
	public static final BooleanOption OPTION_REMOVE_LINE_BREAKS = 
		new BooleanOption(
			"Remove Line Breaks", OPTION_REMOVE_LINE_BREAKS_SHORT_NAME, 
			"Remove line breaks in sequences. This option can't be used together with 'Remove Line Breaks'.", 
			true, false
		);
	
	public static final EnumOption<FastaSequenceRenameMode> OPTION_RENAMING_MODE =
		new EnumOption<>(
			"Renaming Mode", 
			OPTION_RENAMING_MODE_SHORT_NAME, 
			"Renaming mode:\n"
			+ "\tNONE: No renaming\n" 
			+ "\tSMART: Recognices and summarizes the most common sequence name formats\n"
			+ "\tPREFIX: Replaces sequences names with a prefix followed by a sequence id\n"
			+ "\tGENERIC: If sequence name is in format <src>|<val0>|<val1>|<val2>..., then replaces name with the <valX> selected",
			FastaSequenceRenameMode.NONE,
			false, true, false
		);
	
	public static final StringOption OPTION_PREFIX =
		new StringOption(
			"Prefix",
			OPTION_PREFIX_SHORT_NAME,
			"Prefix for the \"Prefix\" renaming method", 
			true, true
		);
	
	public static final DefaultValueBooleanOption OPTION_ADD_INDEX_WHEN_PREFIX =
		new DefaultValueBooleanOption(
			"Add Index", 
			OPTION_ADD_INDEX_WHEN_PREFIX_SHORT_NAME, 
			"Add index after the prefix when using the \"Prefix\" renaming method", 
			true
		);
	
	public static final DefaultValueBooleanOption OPTION_KEEP_NAMES_WHEN_PREFIX =
		new DefaultValueBooleanOption(
			"Keep Names", 
			OPTION_KEEP_NAMES_WHEN_PREFIX_SHORT_NAME,
			"Keep names when using the \"Prefix\" renaming method", 
			true
		);
	
	public static final DefaultValuedStringOption OPTION_DELIMITER_STRING =
		new DefaultValuedStringOption(
			"Delimiter String", 
			OPTION_DELIMITER_STRING_NAME, 
			"Text string that delimits the parts of the original sequence name", 
			"|"
		);
	
	public static final DefaultValuedStringOption OPTION_JOINER_STRING =
		new DefaultValuedStringOption(
			"Joiner String", 
			OPTION_JOINER_STRING_SHORT_NAME, 
			"Text string to join the parts of the new sequence name", 
			"_"
		);
	
	public static final StringConstructedOption<Integer> OPTION_INDEXES =
		new StringConstructedOption<Integer>(
			"Selected Indexes",
			OPTION_INDEXES_SHORT_NAME,
			"Indexes of the name parts selected for the \"Generic\" or \"Smart\" renaming methods",
			true, true, true
		) {};
	
	public static final DefaultValueBooleanOption OPTION_KEEP_DESCRIPTION =
		new DefaultValueBooleanOption(
			"Keep Description", 
			OPTION_KEEP_DESCRIPTION_NAME,
			"Keep description (only available for if reformat mode is different from NONE)", 
			true
		);
	
	public ReformatFastaCommand() {
		super();
	}
	
	public ReformatFastaCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "reformat_fasta";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Reformat FASTA";
	}

	@Override
	public String getDescription() {
		return "Reformats a FASTA file to change the sequence names, the "
			+ "sequence fragments length and remove empty lines.";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType fastaType = parameters.getSingleValue(OPTION_FASTA_TYPE);
		final File fastaFile = parameters.getSingleValue(OPTION_FASTA);
		final FastaSequenceRenameMode renameMode = parameters.getSingleValue(OPTION_RENAMING_MODE);
		final Boolean keepDescription = parameters.getSingleValue(OPTION_KEEP_DESCRIPTION);
		final String delimiter = parameters.getSingleValue(OPTION_DELIMITER_STRING);
		final String joiner = parameters.getSingleValue(OPTION_JOINER_STRING);
		
		final Integer fragmentLength = parameters.getSingleValue(OPTION_FRAGMENT_LENGTH);
		final Boolean removeLineBreaks = parameters.getSingleValue(OPTION_REMOVE_LINE_BREAKS);
		
		if (fragmentLength != null && removeLineBreaks)
			throw new IllegalArgumentException(format("%s and %s can't be used at the same time", OPTION_REMOVE_LINE_BREAKS_SHORT_NAME, OPTION_FRAGMENT_LENGTH_SHORT_NAME));
		
		if (fragmentLength != null && fragmentLength <= 0)
			throw new IllegalArgumentException(format("%s must be a positive value (actual value %d)", OPTION_FRAGMENT_LENGTH_SHORT_NAME, fragmentLength));
		
		final Map<ReformatFastaParameters, Object> additionalParameters = new HashMap<>();
		switch (renameMode) {
			case KNOWN_SEQUENCE_NAMES: {
				final int[] selectedIndexes = extractSelectedIndexes(parameters);

				if (selectedIndexes != null)
					additionalParameters.put(ReformatFastaParameters.INDEXES, selectedIndexes);
				additionalParameters.put(ReformatFastaParameters.JOINER_STRING, joiner == null ? "" : joiner);
				additionalParameters.put(ReformatFastaParameters.KEEP_DESCRIPTION, keepDescription);
				break;
			}
			case PREFIX: {
				final String prefix = parameters.getSingleValue(OPTION_PREFIX);
				final Boolean keepNameAfterPrefix = parameters.getSingleValue(OPTION_KEEP_NAMES_WHEN_PREFIX);
				final Boolean addIndexAfterPrefix = parameters.getSingleValue(OPTION_ADD_INDEX_WHEN_PREFIX);
				
				additionalParameters.put(ReformatFastaParameters.PREFIX, prefix);
				additionalParameters.put(ReformatFastaParameters.KEEP_NAMES_WHEN_PREFIX, keepNameAfterPrefix);
				additionalParameters.put(ReformatFastaParameters.ADD_INDEX_WHEN_PREFIX, addIndexAfterPrefix);
				additionalParameters.put(ReformatFastaParameters.JOINER_STRING, joiner == null ? "" : joiner);
				additionalParameters.put(ReformatFastaParameters.KEEP_DESCRIPTION, keepDescription);
				break;
			}
			case MULTIPART_NAME: {
				final int[] selectedIndexes = extractSelectedIndexes(parameters);

				if (selectedIndexes != null)
					additionalParameters.put(ReformatFastaParameters.INDEXES, selectedIndexes);
				additionalParameters.put(ReformatFastaParameters.DELIMITER_STRING, delimiter == null ? "" : delimiter);
				additionalParameters.put(ReformatFastaParameters.JOINER_STRING, joiner == null ? "" : joiner);
				additionalParameters.put(ReformatFastaParameters.KEEP_DESCRIPTION, keepDescription);
				break;
			}
			default:
		}
		
		if (removeLineBreaks) {
			this.controller.renameSequencesAndRemoveLineBreaks(
				newFasta(fastaType, fastaFile), 
				renameMode,
				additionalParameters
			);
		} else if (fragmentLength != null) {
			this.controller.renameSequencesAndChangeLength(
				newFasta(fastaType, fastaFile), 
				fragmentLength, 
				renameMode,
				additionalParameters
			);
		} else {
			this.controller.renameSequences(
				newFasta(fastaType, fastaFile), 
				renameMode,
				additionalParameters
			);
		}
	}

	private static int[] extractSelectedIndexes(Parameters parameters) {
		final List<Integer> indexes = parameters.getAllValues(OPTION_INDEXES);
		
		if (indexes != null && !indexes.isEmpty()) {
			final int[] indexesArray = new int[indexes.size()];
			int i = 0;
			for (Integer index : indexes) {
				indexesArray[i++] = index;
			}
			
			return indexesArray;
		} else {
			return null;
		}
	}
}
