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
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class RetrieveSearchEntryCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private final boolean accessionInfer;
	
	private JComboBox<Database> cmbDatabases;
	private ActionListener alDatabases;

	private JComboBox<String> cmbAccessions;
	private ActionListener alAccessions;
	
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
		super(controller, command, defaultParameters);
		
		this.accessionInfer = accessionInfer;
		
		this.pack();
	}

	private Database getSelectedDatabase() {
		return (Database) this.cmbDatabases.getSelectedItem();
	}
	
	@Override
	protected <T> Component createComponentForOption(
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		if (this.cmbDatabases == null) {
			 this.cmbDatabases = new JComboBox<>();
		}
		
		if (option.equals(RetrieveSearchEntryCommand.OPTION_DB_TYPE)) {
			final ParameterValuesReceiver pvr = new ParameterValuesReceiverWrapper(receiver) {
				@Override
				public void setValue(Option<?> option, String value) {
					super.setValue(option, value);
					
					if (value == null) {
						cmbDatabases.setModel(new DefaultComboBoxModel<Database>());
					} else {
						final Object convertedValue = 
							option.getConverter().convert(new SingleParameterValue(value));
						
						final Database[] databases;
						if (convertedValue == SequenceType.NUCLEOTIDE) {
							databases = controller.listNucleotideDatabases();
						} else if (convertedValue == SequenceType.PROTEIN) {
							databases = controller.listProteinDatabases();
						} else {
							throw new IllegalArgumentException("Unknown option: " + convertedValue);
						}
						
						cmbDatabases.setModel(new DefaultComboBoxModel<>(databases));
						
						if (alDatabases != null)
							alDatabases.actionPerformed(null);
					}
				}
			};
			
			pvr.setValue(option, this.getDefaultOptionString(option));
			
			return BuildComponent.forEnum(this, option, pvr);
		} else if (option.equals(RetrieveSearchEntryCommand.OPTION_DATABASE)) {
			this.alDatabases = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Database database = (Database) cmbDatabases.getSelectedItem();
					
					if (database == null) {
						receiver.setValue(option, (String) null);
					} else {
						receiver.setValue(option, database.getDirectory().getAbsolutePath());
						
						if (cmbAccessions != null && alAccessions != null) {
							cmbAccessions.setModel(new DefaultComboBoxModel<String>(new Vector<String>(
								database.listAccessions()
							)));
							
							alAccessions.actionPerformed(null);
						}
					}
				}
			};
			
			if (this.hasDefaultOption(option)) {
				final String dbPath = this.getDefaultOptionString(option);
				final int size = this.cmbDatabases.getItemCount();
				
				for (int i = 0; i < size; i++) {
					final Database database = (Database) this.cmbDatabases.getItemAt(i);
					
					if (database.getDirectory().getAbsolutePath().equals(dbPath)) {
						this.cmbDatabases.setSelectedIndex(i);
						break;
					}
				}
			}
			
			this.alDatabases.actionPerformed(null);
			this.cmbDatabases.addActionListener(alDatabases);
			
			return cmbDatabases;
		} else if (option.equals(RetrieveSearchEntryCommand.OPTION_ACCESSION)) {
			if (this.accessionInfer) {
				final Database database = this.getSelectedDatabase();
				if (database == null) {
					this.cmbAccessions = new JComboBox<>();
				} else {
					this.cmbAccessions = new JComboBox<>(new Vector<String>(
						database.listAccessions()
					));
				}
				
				this.alAccessions = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (cmbAccessions.getSelectedItem() != null)
							receiver.setValue(option, (String) cmbAccessions.getSelectedItem());
					}
				};
				
				this.alAccessions.actionPerformed(null);
				this.cmbAccessions.addActionListener(this.alAccessions);
				
				return cmbAccessions;
			} else {
				return super.createComponentForOption(option, receiver);
			}
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}
}
