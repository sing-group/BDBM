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

import java.io.File;
import java.util.List;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class MergeFastasCommand extends BDBMCommand {
	public static final String OPTION_FASTA_TYPE_SHORT_NAME = "fastatype";
	public static final String OPTION_FASTAS_SHORT_NAME = "fastas";
	public static final String OPTION_OUTPUT_SHORT_NAME = "out";
	
	public static final Option<SequenceType> OPTION_FASTA_TYPE = 
		new Option<SequenceType>(
			"Fasta Type", OPTION_FASTA_TYPE_SHORT_NAME, "Fasta type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_FASTAS = 
		new FileOption(
			"Fastas", OPTION_FASTAS_SHORT_NAME, "Fasta files to be merged",
			false, true, true
		);
	public static final StringOption OPTION_OUTPUT = 
		new StringOption(
			"Output Fasta", OPTION_OUTPUT_SHORT_NAME, "Resulting fasta file",
			false, true
		);
	
	public MergeFastasCommand() {
		super();
	}
	
	public MergeFastasCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "merge_fastas";
	}

	@Override
	public String getDescriptiveName() {
		return "Merge Fastas";
	}

	@Override
	public String getDescription() {
		return "Merge two or more fasta files";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType fastaType = parameters.getSingleValue(OPTION_FASTA_TYPE);
		final List<File> fastaFiles = parameters.getAllValues(OPTION_FASTAS);
		final String outputFasta = parameters.getSingleValue(OPTION_OUTPUT);
		
		final Fasta[] fastas = new Fasta[fastaFiles.size()];
		int i = 0;
		for (File fastaFile : fastaFiles) {
			fastas[i++] = AbstractFasta.newFasta(fastaType, fastaFile);
		}
		
		this.controller.mergeFastas(fastas, outputFasta);
	}
}
