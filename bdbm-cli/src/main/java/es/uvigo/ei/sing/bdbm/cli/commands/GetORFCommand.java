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

import es.uvigo.ei.sing.bdbm.cli.commands.converters.DefaultValueBooleanOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.IntegerOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideFasta;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class GetORFCommand extends BDBMCommand {
	public static final FileOption OPTION_FASTA = 
		new FileOption(
			"Fasta", "fasta", "Input fasta (nucleotide) file",
			false, true
		);
	public static final IntegerOption OPTION_MIN_SIZE =
		new IntegerOption(
			"Min. Size", "min", "Minimum ORF size", "300"
		);
	public static final IntegerOption OPTION_MAX_SIZE =
		new IntegerOption(
			"Max. Size", "max", "Maximum ORF size", "10000"
		);
	public static final DefaultValueBooleanOption OPTION_REMOVE_NEWLINES =
		new DefaultValueBooleanOption(
			"Remove new lines", "nonl", "Delete the new line characters from the sequences", 
			false
		);
	public static final StringOption OPTION_OUTPUT_NAME = 
		new StringOption(
			"Output name", "output", "Output name", 
			false, true
		);
	
	public GetORFCommand() {
		super();
	}
	
	public GetORFCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "getorf";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Get ORF (EMBOSS)";
	}

	@Override
	public String getDescription() {
		return "Performs a 'getorf' (EMBOSS)";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final File fastaFile = parameters.getSingleValue(OPTION_FASTA);
		final Integer minSize = parameters.getSingleValue(OPTION_MIN_SIZE);
		final Integer maxSize = parameters.getSingleValue(OPTION_MAX_SIZE);
		final Boolean noNewLine = parameters.getSingleValue(OPTION_REMOVE_NEWLINES);
		final String outputName = parameters.getSingleValue(OPTION_OUTPUT_NAME);
		
		this.controller.getORF(
			new DefaultNucleotideFasta(fastaFile),
			minSize,
			maxSize,
			noNewLine,
			outputName
		);
	}
}
