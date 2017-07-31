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

import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractFasta;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class MakeBLASTDBCommand extends BDBMCommand {
	public static final String OPTION_DB_TYPE_SHORT_NAME = "dbtype";
	public static final String OPTION_INPUT_SHORT_NAME = "in";
	public static final String OPTION_OUTPUT_SHORT_NAME = "out";
	
	public static final Option<SequenceType> OPTION_DB_TYPE = 
		new Option<SequenceType>(
			"DB Type", OPTION_DB_TYPE_SHORT_NAME, "Database type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_INPUT = 
		new FileOption(
			"Input FASTA", OPTION_INPUT_SHORT_NAME, "Input FASTA file", 
			false, true
		);
	public static final StringOption OPTION_OUTPUT = 
		new StringOption(
			"Output name", OPTION_OUTPUT_SHORT_NAME, "Output database name", 
			false, true
		);
	
	public MakeBLASTDBCommand() {
		super();
	}
	
	public MakeBLASTDBCommand(BDBMController controller) {
		super(controller);
	}
	
	@Override
	public String getName() {
		return "make_blast_db";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Make BLAST Database";
	}

	@Override
	public String getDescription() {
		return "Creates a BLAST database from a FASTA file";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType dbType = parameters.getSingleValue(OPTION_DB_TYPE);
		final File input = parameters.getSingleValue(OPTION_INPUT);
		final String outputDBName = parameters.getSingleValue(OPTION_OUTPUT);
		
		this.controller.makeBlastDB(AbstractFasta.newFasta(dbType, input), outputDBName);
	}
}
