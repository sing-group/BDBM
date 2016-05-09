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

import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ImportFastaCommand extends BDBMCommand {
	public static final String OPTION_IMPORT_TYPE_SHORT_NAME = "type";
	public static final String OPTION_INPUT_FILE_SHORT_NAME = "file";
	
	public static final Option<SequenceType> OPTION_IMPORT_TYPE = 
		new Option<SequenceType>(
			"Type", OPTION_IMPORT_TYPE_SHORT_NAME, "FASTA type: auto, nucl (nucleotides) or prot (proteins)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_INPUT_FILE = 
		new FileOption(
			"File", OPTION_INPUT_FILE_SHORT_NAME, "FASTA file", 
			false, true
		);
	
	public ImportFastaCommand() {
		super();
	}
	
	public ImportFastaCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "import_fasta";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Import FASTA";
	}

	@Override
	public String getDescription() {
		return "Imports a nucleotide or FASTA file into the repository";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		this.controller.importFasta(
			parameters.getSingleValue(OPTION_IMPORT_TYPE),
			parameters.getSingleValue(OPTION_INPUT_FILE)
		);
	}
}
