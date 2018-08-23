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

import java.io.File;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractDatabase;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class RetrieveSearchEntryCommand extends BDBMCommand {
	public static final String OPTION_DB_TYPE_SHORT_NAME = "dbtype";
	public static final String OPTION_DATABASE_SHORT_NAME = "db";
	public static final String OPTION_ACCESSION_SHORT_NAME = "accession";
	
	public static final Option<SequenceType> OPTION_DB_TYPE = 
		new Option<SequenceType>(
			"DB Type", OPTION_DB_TYPE_SHORT_NAME, "Database type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_DATABASE = 
		new FileOption(
			"Database", OPTION_DATABASE_SHORT_NAME, "Database from which the search entry will be retrieved",
			false, true
		);
	public static final StringOption OPTION_ACCESSION = 
		new StringOption(
			"Accession", OPTION_ACCESSION_SHORT_NAME, "Accession name", 
			false, true
		);
	
	public RetrieveSearchEntryCommand() {
		super();
	}
	
	public RetrieveSearchEntryCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "retrieve_search_entry";
	}
	
	@Override
	public String getDescriptiveName() {
		return "Retrieve Search Entry";
	}

	@Override
	public String getDescription() {
		return "Retrieves a search entry from a database";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType sequenceType = parameters.getSingleValue(OPTION_DB_TYPE);
		final File databaseFile = parameters.getSingleValue(OPTION_DATABASE);
		final String accesionName = parameters.getSingleValue(OPTION_ACCESSION);
		
		this.controller.retrieveSearchEntry(
			AbstractDatabase.newDatabase(sequenceType, databaseFile),
			accesionName
		);
	}
}
