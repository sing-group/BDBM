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

import es.uvigo.ei.sing.bdbm.cli.commands.TBLASTNCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideDatabase;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.ProteinSearchEntry.ProteinQuery;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.Parameters;

public class TBLASTNCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;
	
	public TBLASTNCommandDialog(
		BDBMController controller, 
		TBLASTNCommand command
	) {
		this(controller, command, null);
	}

	public TBLASTNCommandDialog(
		BDBMController controller, 
		TBLASTNCommand command,
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
		if (option.equals(TBLASTNCommand.OPTION_DATABASE)) {
			final JComboBox<NucleotideDatabase> cmbDatabases = new JComboBox<>(
				this.controller.listNucleotideDatabases()
			);
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
		} else if (option.equals(TBLASTNCommand.OPTION_QUERY)) {
			final JPanel panel = new JPanel(new GridLayout(2, 1));
			
			final JComboBox<ProteinSearchEntry> cmbSearchEntries = new JComboBox<>(
					this.controller.listProteinSearchEntries()
				);
			final JComboBox<ProteinQuery> cmbQueries = new JComboBox<>();
			
			final ActionListener alQueries = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Object item = cmbQueries.getSelectedItem();
					
					if (item == null) {
						receiver.setValue(option, (String) null);
					} else if (item instanceof ProteinQuery) {
						receiver.setValue(option, ((ProteinQuery) item).getBaseFile().getAbsolutePath());
					}
				}
			};
			
			final ActionListener alSearchEntries = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final ProteinSearchEntry searchEntry = 
						(ProteinSearchEntry) cmbSearchEntries.getSelectedItem();
					
					if (searchEntry != null && !searchEntry.listQueries().isEmpty()) {
						cmbQueries.setModel(
							new DefaultComboBoxModel<>(
								new Vector<ProteinQuery>(searchEntry.listQueries())
							)
						);
					} else {
						cmbQueries.setModel(new DefaultComboBoxModel<ProteinQuery>());
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
}