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
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTNCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideSearchEntry.NucleotideQuery;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.Parameters;

public class BLASTNCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	public BLASTNCommandDialog(
		BDBMController controller, 
		BLASTNCommand command
	) {
		this(controller, command, null);
	}
	
	public BLASTNCommandDialog(
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
						break;
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
		} else if (option.equals(BLASTNCommand.OPTION_QUERY)) {
			final JPanel panel = new JPanel(new GridLayout(2, 1));
			
			final JComboBox<NucleotideSearchEntry> cmbSearchEntries = new JComboBox<>(
				this.controller.listNucleotideSearchEntries()
			);
			final JComboBox<NucleotideQuery> cmbQueries = new JComboBox<>();
			
			final ActionListener alQueries = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					final Object item = cmbQueries.getSelectedItem();
//					
//					if (item == null) {
//						receiver.setValue(option, (String) null);
//					} else if (item instanceof NucleotideQuery) {
//						receiver.setValue(option, ((NucleotideQuery) item).getBaseFile().getAbsolutePath());
//					}
					
					BLASTNCommandDialog.this.updateQuery((NucleotideQuery) cmbQueries.getSelectedItem(), receiver);
				}
			};
			
			final ActionListener alSearchEntries = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final NucleotideSearchEntry searchEntry = 
						(NucleotideSearchEntry) cmbSearchEntries.getSelectedItem();
					
					if (searchEntry != null && !searchEntry.listQueries().isEmpty()) {
						cmbQueries.setModel(
							new DefaultComboBoxModel<>(
								new Vector<NucleotideQuery>(searchEntry.listQueries())
							)
						);
					} else {
						cmbQueries.setModel(new DefaultComboBoxModel<NucleotideQuery>());
					}
					
					alQueries.actionPerformed(null);
				}
			};
			
			alSearchEntries.actionPerformed(null);
			
			cmbSearchEntries.addActionListener(alSearchEntries);
			cmbQueries.addActionListener(alQueries);
			
			panel.add(cmbSearchEntries);
			panel.add(cmbQueries);
			
			return panel;
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}
	
	protected void updateQuery(NucleotideQuery query, ParameterValuesReceiver receiver) {
		if (query == null) {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, (String) null);
		} else {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, query.getBaseFile().getAbsolutePath());
		}
		
		this.updateButtonOk();
	}
}