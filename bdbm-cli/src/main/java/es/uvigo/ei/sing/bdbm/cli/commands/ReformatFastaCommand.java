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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.EnumOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.IntegerOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.fasta.ReformatFastaParameters;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractFasta;
import es.uvigo.ei.sing.yaacli.command.option.DefaultValuedStringOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringConstructedOption;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ReformatFastaCommand extends BDBMCommand {
	public static final Option<SequenceType> OPTION_FASTA_TYPE = 
		new Option<SequenceType>(
			"Fasta Type", "fastatype", "Fasta type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_FASTA = 
		new FileOption(
			"Fasta", "fasta", "Source fasta file",
			false, true
		);
	
	public static final IntegerOption OPTION_FRAGMENT_LENGTH = 
		new IntegerOption(
			"Sequence Fragment Length", "length", 
			"Length of the sequence fragments (Negative values mean no changes, and zero value means no line break)", 
			-1
		);
	
	public static final EnumOption<FastaUtils.RenameMode> OPTION_RENAMING_MODE =
		new EnumOption<>(
			"Renaming Mode", 
			"mode", 
			"Renaming mode:\n"
			+ "\tNONE: No renaming\n" 
			+ "\tSMART: Recognices and summarizes the most common sequence name formats\n"
			+ "\tPREFIX: Replaces sequences names with a prefix followed by a sequence id\n"
			+ "\tGENERIC: If sequence name is in format <src>|<val0>|<val1>|<val2>..., then replaces name with the <valX> selected",
			FastaUtils.RenameMode.NONE,
			false, true, false
		);
	
	public static final StringConstructedOption<Integer> OPTION_INDEXES =
		new StringConstructedOption<Integer>(
			"Indexes",
			"indexes",
			"Indexes selected for the \"Generic\" renaming method",
			true, true, true
		) {};
	
	public static final StringOption OPTION_PREFIX =
		new StringOption(
			"Prefix", 
			"prefix", 
			"Prefix for the \"Prefix\" renaming method", 
			true, true
		);
	
	public static final DefaultValueBooleanOption OPTION_KEEP_NAMES_WHEN_PREFIX =
		new DefaultValueBooleanOption(
			"Keep Names", 
			"keep_names", 
			"Keep names when using the \"Prefix\" renaming method", 
			true
		);
	
	public static final DefaultValueBooleanOption OPTION_ADD_INDEX_WHEN_PREFIX =
		new DefaultValueBooleanOption(
			"Add Index", 
			"add_index", 
			"Add index after the prefix when using the \"Prefix\" renaming method", 
			true
		);
	
	public static final DefaultValuedStringOption OPTION_SEPARATOR =
		new DefaultValuedStringOption(
			"Separator", 
			"separator", 
			"Separator of the parts of new names", 
			"_"
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
		return "Reformat Fasta";
	}

	@Override
	public String getDescription() {
		return "Reformats a Fasta file to change the sequence fragments length. "
			+ "Zero or negative length means no fragmented sequences";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType fastaType = parameters.getSingleValue(OPTION_FASTA_TYPE);
		final File fastaFile = parameters.getSingleValue(OPTION_FASTA);
		final FastaUtils.RenameMode renameMode = parameters.getSingleValue(OPTION_RENAMING_MODE);
		final Integer fragmentLength = parameters.getSingleValue(OPTION_FRAGMENT_LENGTH);

		final Map<ReformatFastaParameters, Object> additionalParameters = new HashMap<>();
		switch (renameMode) {
			case SMART: {
				final String separator = parameters.getSingleValue(OPTION_SEPARATOR);
				additionalParameters.put(ReformatFastaParameters.SEPARATOR, separator == null? "" : separator);
				break;
			}
			case PREFIX: {
				final String prefix = parameters.getSingleValue(OPTION_PREFIX);
				final Boolean keepNameAfterPrefix = parameters.getSingleValue(OPTION_KEEP_NAMES_WHEN_PREFIX);
				final Boolean addIndexAfterPrefix = parameters.getSingleValue(OPTION_ADD_INDEX_WHEN_PREFIX);
				final String separator = parameters.getSingleValue(OPTION_SEPARATOR);
				
				additionalParameters.put(ReformatFastaParameters.PREFIX, prefix);
				additionalParameters.put(ReformatFastaParameters.KEEP_NAMES_WHEN_PREFIX, keepNameAfterPrefix);
				additionalParameters.put(ReformatFastaParameters.ADD_INDEX_WHEN_PREFIX, addIndexAfterPrefix);
				additionalParameters.put(ReformatFastaParameters.SEPARATOR, separator == null? "" : separator);
				break;
			}
			case GENERIC: {
				final List<Integer> indexes = parameters.getAllValues(OPTION_INDEXES);
				if (indexes == null || indexes.isEmpty())
					throw new IllegalArgumentException("At least one index must be selected");
				
				final int[] indexesArray = new int[indexes.size()];
				int i = 0;
				for (Integer index : indexes) {
					indexesArray[i++] = index;
				}

				additionalParameters.put(ReformatFastaParameters.INDEXES, indexesArray);
				
				final String separator = parameters.getSingleValue(OPTION_SEPARATOR);
				additionalParameters.put(ReformatFastaParameters.SEPARATOR, separator == null? "" : separator);
				break;
			}
			default:
		}
		
		this.controller.reformatFasta(
			renameMode, 
			AbstractFasta.newFasta(fastaType, fastaFile), 
			fragmentLength == null ? -1 : fragmentLength, 
			additionalParameters
		);
	}
}
