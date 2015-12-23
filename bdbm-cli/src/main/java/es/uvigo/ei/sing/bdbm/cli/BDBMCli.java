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
package es.uvigo.ei.sing.bdbm.cli;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import es.uvigo.ei.sing.bdbm.BDBMManager;
import es.uvigo.ei.sing.bdbm.cli.commands.BDBMCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.controller.DefaultBDBMController;
import es.uvigo.ei.sing.bdbm.environment.DefaultBDBMEnvironment;
import es.uvigo.ei.sing.bdbm.persistence.DefaultBDBMRepositoryManager;
import es.uvigo.ei.sing.yaacli.CLIApplication;
import es.uvigo.ei.sing.yaacli.command.Command;

public class BDBMCli extends CLIApplication {
	public BDBMCli() {
		super(true, false);
		
		this.loadCommands();
	}
	
	@Override
	protected List<Command> buildCommands() {
		try {
			final BDBMManager manager = new BDBMManager(
				new DefaultBDBMEnvironment(new File("bdbm.conf")),
				new DefaultBDBMRepositoryManager(),
				new DefaultBDBMController()
			);
			
			return getCommands(manager.getController());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getApplicationName() {
		return "BLAST DB Manager";
	}

	@Override
	protected String getApplicationCommand() {
		return "bdbm";
	}
	
	private static List<Command> getCommands(BDBMController controller) {
		final List<Command> commands = getCommands();
		
		for (Command command : commands) {
			if (command instanceof BDBMCommand) {
				((BDBMCommand) command).setController(controller);
			}
		}

		Collections.sort(commands, new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return commands;
	}
	
	private static List<Command> getCommands() {
		final ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
		
		final Iterator<Command> itCommand = loader.iterator();
		
		final List<Command> commands = new LinkedList<>();
		while (itCommand.hasNext()) {
			commands.add(itCommand.next());
		}
		
		return commands;
	}
}
