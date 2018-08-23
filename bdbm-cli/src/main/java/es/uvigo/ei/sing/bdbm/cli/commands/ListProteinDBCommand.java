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

import java.util.Collections;
import java.util.List;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinDatabase;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ListProteinDBCommand extends BDBMCommand {
	public ListProteinDBCommand() {
		super();
	}
	
	public ListProteinDBCommand(BDBMController controller) {
		super(controller);
	}

	@Override
	public String getName() {
		return "list_prot_db";
	}
	
	@Override
	public String getDescriptiveName() {
		return "List Protein Databases";
	}

	@Override
	public String getDescription() {
		return "Lists the BLAST protein databases";
	}

	@Override
	public void execute(Parameters parameters) throws Exception {
		for (ProteinDatabase database : this.controller.listProteinDatabases()) {
			System.out.println(database.getName());
		}
	}

	@Override
	protected List<Option<?>> createOptions() {
		return Collections.emptyList();
	}
}
