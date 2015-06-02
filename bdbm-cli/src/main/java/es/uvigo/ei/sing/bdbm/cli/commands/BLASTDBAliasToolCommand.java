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
import java.util.ArrayList;
import java.util.List;

import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.SequenceTypeOptionConverter;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.AbstractDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.Parameters;
import es.uvigo.ei.sing.yaacli.StringOption;

public class BLASTDBAliasToolCommand extends BDBMCommand {
	public static final Option<SequenceType> OPTION_DB_TYPE = 
		new Option<SequenceType>(
			"DB Type", "dbtype", "Database type: prot (proteins) or nucl (nucleotides)", 
			false, true, 
			new SequenceTypeOptionConverter()
		);
	public static final FileOption OPTION_DATABASES = 
		new FileOption(
			"Databases", "db", "Databases to be aggregated",
			false, true, true
		);
	public static final StringOption OPTION_OUTPUT = 
		new StringOption(
			"Output name", "out", "Output database name", 
			false, true
		);
	
	public BLASTDBAliasToolCommand() {
		super();
	}
	
	public BLASTDBAliasToolCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "blastdbalias";
	}
	
	@Override
	public String getDescriptiveName() {
		return "BLAST DB Alias";
	}

	@Override
	public String getDescription() {
		return "Aggegates two or more databases";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		final SequenceType sequenceType = parameters.getSingleValue(OPTION_DB_TYPE);
		final List<File> databaseFiles = parameters.getAllValues(OPTION_DATABASES);
		final String outputDBName = parameters.getSingleValue(OPTION_OUTPUT);
		
		final List<Database> databases = new ArrayList<Database>(databaseFiles.size());
		
		for (File dbFile : databaseFiles) {
			databases.add(AbstractDatabase.newDatabase(sequenceType, dbFile));
		}
		
		this.controller.blastdbAliasTool(
			databases.toArray(new Database[databases.size()]),
			outputDBName
		);
	}
}