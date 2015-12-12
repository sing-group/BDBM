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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.BLASTNCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.ValueCallback;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public abstract class BLASTCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	public BLASTCommandDialog(
		BDBMController controller, 
		BLASTCommand command
	) {
		this(controller, command, null);
	}
	
	public BLASTCommandDialog(
		BDBMController controller, 
		BLASTCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}
	
	protected abstract Database[] listDatabases();
	protected abstract SearchEntry[] listSearchEntries();
	
	@ComponentForOption(BLASTCommand.OPTION_DATABASE_SHORT_NAME)
	protected Component createComponentForDatabaseOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, new JComboBox<Database>(this.listDatabases()),
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption(BLASTCommand.OPTION_QUERY_SHORT_NAME)
	protected Component createComponentForQueryOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		final JPanel panel = new JPanel(new GridLayout(2, 1));

		final JComboBox<Query> cmbQueries = new JComboBox<>();
		
		final JComboBox<SearchEntry> cmbSearchEntries =
			ComponentFactory.createComponentForSequenceEntityValues(
				option, receiver, new JComboBox<SearchEntry>(this.listSearchEntries()),
				this.getDefaultOptionString(option),
				new ValueCallback<SearchEntry>() {
					public void callback(SearchEntry searchEntry) {
						if (searchEntry != null && !searchEntry.listQueries().isEmpty()) {
							cmbQueries.setModel(new DefaultComboBoxModel<>(
								new Vector<>(searchEntry.listQueries())
							));
							cmbQueries.setSelectedIndex(0);
						} else {
							cmbQueries.setModel(new DefaultComboBoxModel<Query>());
							cmbQueries.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Model changed"));
						}
					}
				}
			);
		
		cmbQueries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Query query = cmbQueries.getItemAt(cmbQueries.getSelectedIndex());
				
				BLASTCommandDialog.this.updateQuery(query, receiver);
			}
		});
		
		panel.add(cmbSearchEntries);
		panel.add(cmbQueries);
		
		return panel;
	}
	
	protected void updateQuery(Query query, ParameterValuesReceiver receiver) {
		if (query == null) {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, (String) null);
		} else {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, query.getBaseFile().getAbsolutePath());
		}
		
		this.updateButtonOk();
	}
}
