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
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import es.uvigo.ei.sing.bdbm.cli.commands.RetrieveSearchEntryCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.DatabaseValuesProvider;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.ValueCallback;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class RetrieveSearchEntryCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private final boolean accessionInfer;
	
	private JComboBox<Database> cmbDatabases;
	private JComboBox<String> cmbAccessions;
	
	public RetrieveSearchEntryCommandDialog(
		BDBMController controller, 
		RetrieveSearchEntryCommand command,
		boolean accessionInfer
	) {
		this(controller, command, null, accessionInfer);
	}
	
	public RetrieveSearchEntryCommandDialog(
		BDBMController controller, 
		RetrieveSearchEntryCommand command,
		Parameters defaultParameters,
		boolean accessionInfer
	) {
		super(controller, command, defaultParameters, false);
		
		this.accessionInfer = accessionInfer;
		
		this.init();
		this.pack();
	}

	private Database getSelectedDatabase() {
		return this.cmbDatabases.getItemAt(this.cmbDatabases.getSelectedIndex());
	}
	
	@Override
	protected void preComponentsCreation() {
		this.cmbDatabases = new JComboBox<>();
	}

	@ComponentForOption(RetrieveSearchEntryCommand.OPTION_DB_TYPE_SHORT_NAME)
	protected Component createComponentForDBTypeOption(
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceType(
			this, option, receiver, cmbDatabases,
			new DatabaseValuesProvider(this.controller),
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption(RetrieveSearchEntryCommand.OPTION_DATABASE_SHORT_NAME)
	protected Component createComponentForDatabaseOption(
		final FileOption option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, this.cmbDatabases,
			this.getDefaultOptionString(option),
			new ValueCallback<Database>() {
				@Override
				public void callback(Database database) {
					if (cmbAccessions != null) {
						cmbAccessions.setModel(new DefaultComboBoxModel<String>(
							new Vector<String>(database.listAccessions())
						));

						cmbAccessions.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Model Changed"));
					}
				}
			}
		);
	}

	@ComponentForOption(RetrieveSearchEntryCommand.OPTION_ACCESSION_SHORT_NAME)
	protected Component createComponentForAccessionOption(
		final StringOption option, 
		final ParameterValuesReceiver receiver
	) {
		if (this.accessionInfer) {
			final Database database = this.getSelectedDatabase();
			
			if (database == null) {
				this.cmbAccessions = new JComboBox<>();
			} else {
				this.cmbAccessions = new JComboBox<>(new Vector<String>(
					database.listAccessions()
				));
			}
			
			final ActionListener alAccessions = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbAccessions.getSelectedItem() != null)
						receiver.setValue(option, (String) cmbAccessions.getSelectedItem());
				}
			};
			
			alAccessions.actionPerformed(null);
			this.cmbAccessions.addActionListener(alAccessions);
			
			return cmbAccessions;
		} else {
			return null;
		}
	}
}
