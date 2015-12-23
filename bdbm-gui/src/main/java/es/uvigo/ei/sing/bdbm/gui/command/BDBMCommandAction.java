/*
 * #%L
 * BDBM GUI
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
package es.uvigo.ei.sing.bdbm.gui.command;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.actions.BDBMAction;
import es.uvigo.ei.sing.yaacli.command.Command;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class BDBMCommandAction extends BDBMAction {
	private static final long serialVersionUID = 1L;
	
	protected final Command command;
	protected final Class<? extends CommandDialog> commandDialog;
	protected final Parameters defaultParameters;
	
	protected final List<Class<?>> paramClasses;
	protected final List<Object> paramValues;

	public BDBMCommandAction(BDBMController controller, Command command) {
		this(controller, command, CommandDialog.class);
	}

	public BDBMCommandAction(String name, BDBMController controller, Command command) {
		this(name, controller, command, CommandDialog.class);
	}

	public BDBMCommandAction(String name, Icon icon, BDBMController controller, Command command) {
		this(name, icon, controller, command, CommandDialog.class);
	}

	public BDBMCommandAction(BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass) {
		this(command.getDescriptiveName(), controller, command, commandDialogClass, null);
	}

	public BDBMCommandAction(String name, BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass) {
		this(name, controller, command, commandDialogClass, null);
	}

	public BDBMCommandAction(String name, Icon icon, BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass) {
		this(name, icon, controller, command, commandDialogClass, null);
	}

	public BDBMCommandAction(BDBMController controller, Command command, Parameters defaultParameters) {
		this(controller, command, CommandDialog.class, defaultParameters);
	}

	public BDBMCommandAction(String name, BDBMController controller, Command command, Parameters defaultParameters) {
		this(name, controller, command, CommandDialog.class, defaultParameters);
	}

	public BDBMCommandAction(String name, Icon icon, BDBMController controller, Command command, Parameters defaultParameters) {
		this(name, icon, controller, command, CommandDialog.class, defaultParameters);
	}

	public BDBMCommandAction(BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass, Parameters defaultParameters) {
		super(command.getDescriptiveName(), controller);
		
		this.command = command;
		this.commandDialog = commandDialogClass;
		this.defaultParameters = defaultParameters;
		
		this.paramClasses = new ArrayList<Class<?>>(10);
		this.paramValues = new ArrayList<Object>(10);
	}

	public BDBMCommandAction(String name, BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass, Parameters defaultParameters) {
		super(name, controller);
		
		this.command = command;
		this.commandDialog = commandDialogClass;
		this.defaultParameters = defaultParameters;
		
		this.paramClasses = new ArrayList<Class<?>>(10);
		this.paramValues = new ArrayList<Object>(10);
	}

	public BDBMCommandAction(String name, Icon icon, BDBMController controller, Command command, Class<? extends CommandDialog> commandDialogClass, Parameters defaultParameters) {
		super(name, icon, controller);
		
		this.command = command;
		this.commandDialog = commandDialogClass;
		this.defaultParameters = defaultParameters;
		
		this.paramClasses = new ArrayList<Class<?>>(10);
		this.paramValues = new ArrayList<Object>(10);
	}
	
	public <T> void addParamValue(Class<? super T> paramClass, T paramValue) {
		this.paramClasses.add(paramClass);
		this.paramValues.add(paramValue);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final List<Class<?>> paramClasses = new ArrayList<Class<?>>(12);
		final List<Object> paramValues = new ArrayList<Object>(12);
		
		paramClasses.add(BDBMController.class);
		paramValues.add(this.getController());
		
		if (this.commandDialog.equals(CommandDialog.class)) {
			paramClasses.add(Command.class);
			paramValues.add(this.command);
		} else {
			paramClasses.add(this.command.getClass());
			paramValues.add(this.command);
		}
		
		if (this.defaultParameters != null) {
			paramClasses.add(Parameters.class);
			paramValues.add(this.defaultParameters);
		}
		
		if (!this.paramClasses.isEmpty()) {
			for (int i = 0; i < this.paramClasses.size(); i++) {
				paramClasses.add(this.paramClasses.get(i));
				paramValues.add(this.paramValues.get(i));
			}
		}
		
		try {
			final CommandDialog dialog = this.commandDialog.getConstructor(
				paramClasses.toArray(new Class<?>[paramClasses.size()])
			).newInstance(paramValues.toArray(new Object[paramValues.size()]));
			
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setVisible(true);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}
}
