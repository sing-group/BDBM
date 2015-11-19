/*
 * #%L
 * BDBM GUI
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
package es.uvigo.ei.sing.bdbm.gui.command.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTNCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ExternalBLASTNCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	public ExternalBLASTNCommandDialog(
		BDBMController controller, 
		BLASTNCommand command
	) {
		this(controller, command, null);
	}
	
	public ExternalBLASTNCommandDialog(
		BDBMController controller, 
		BLASTNCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}

	@Override
	protected <T> Component createComponentForOption(
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		if (option.equals(BLASTNCommand.OPTION_DATABASE)) {
			final NucleotideDatabase[] nucleotideDatabases = 
				this.controller.listNucleotideDatabases();
			final JComboBox<NucleotideDatabase> cmbDatabases =
				new JComboBox<>(nucleotideDatabases);
			
			if (receiver.hasOption(option)) {
				for (NucleotideDatabase database : nucleotideDatabases) {
					if (database.getName().equals(receiver.getValue(option))) {
						cmbDatabases.setSelectedItem(database);
					}
				}
			} else {
				final Object value = cmbDatabases.getSelectedItem();

				if (value != null) {
					final NucleotideDatabase database = (NucleotideDatabase) value;
					receiver.setValue(
						option, 
						new File(database.getDirectory(), database.getName()).getAbsolutePath()
					); 
				}
			}
			
			final ActionListener alDatabases = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Object item = cmbDatabases.getSelectedItem();
					
					if (item == null) {
						receiver.setValue(option, (String) null);
					} else if (item instanceof NucleotideDatabase) {
						final NucleotideDatabase database = (NucleotideDatabase) item;
						final File dbDirectory = database.getDirectory();
						receiver.setValue(
							option, new File(dbDirectory, database.getName()).getAbsolutePath()
						);
					}
				}
			};
			alDatabases.actionPerformed(null);
			
			cmbDatabases.addActionListener(alDatabases);
			
			return cmbDatabases;
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}
}
