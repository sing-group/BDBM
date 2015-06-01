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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTDBAliasToolCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.Parameters;
import es.uvigo.ei.sing.yaacli.SingleParameterValue;

public class BLASTDBAliasToolCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<Database> cmbDatabases;
	private JComboBox<SequenceType> inputDBType;

	private DefaultListModel<Database> lmDatabases;
	private ActionListener alDatabases;
	
	public BLASTDBAliasToolCommandDialog(
		BDBMController controller, 
		BLASTDBAliasToolCommand command
	) {
		this(controller, command, null);
	}
	
	public BLASTDBAliasToolCommandDialog(
		BDBMController controller, 
		BLASTDBAliasToolCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}
	
	@Override
	protected void addMultipleValue(
		Option<?> option, DefaultListModel<Object> listModel, String value
	) {
		if (option.equals(BLASTDBAliasToolCommand.OPTION_DATABASES)) {
			listModel.addElement(this.getDatabase(value));
		} else {
			super.addMultipleValue(option, listModel, value);
		}
	}
	
	@Override
	protected void updateMultipleValues(
		Option<?> option, DefaultListModel<Object> listModel, ParameterValuesReceiver receiver
	) {
		if (option.equals(BLASTDBAliasToolCommand.OPTION_DATABASES)) {
			final List<String> list = new ArrayList<String>(listModel.size());
			
			for (int i = 0; i < listModel.size(); i++) {
				list.add(((Database) listModel.getElementAt(i))
					.getDirectory().getAbsolutePath());
			}
			
			receiver.setValue(option, list);
			
			this.updateButtonOk();
		} else {
			super.updateMultipleValues(option, listModel, receiver);
		}
	}
	
	@Override
	protected <T> void setMultipleDefaultParameters(
		Option<T> option,
		ParameterValuesReceiver receiver, 
		DefaultListModel<Object> listModel
	) {
		if (option.equals(BLASTDBAliasToolCommand.OPTION_DATABASES)) {
			this.lmDatabases = new DefaultListModel<>();
			
			for (Object value : listModel.toArray()) {
				if (value instanceof Database) {
					this.lmDatabases.addElement((Database) value);
				} else {
					throw new IllegalArgumentException("listModel must contain Database objects");
				}
			}
			
			final SequenceType defaultST = this.getDefaultSequenceType();
			
			if (defaultST != null) {
				final List<String> values = 
					this.getDefaulOptionStringList(option);
				
				for (Database db : this.getRegularDatabases(defaultST)) {
					if (values.contains(db.getDirectory().getAbsolutePath())) {
						listModel.addElement(db);
					}
				}
			}
		} else {
			super.setMultipleDefaultParameters(option, receiver, listModel);
		}
	}
	
	protected SequenceType getDefaultSequenceType() {
		final FileOption option = BLASTDBAliasToolCommand.OPTION_DATABASES;
		
		if (this.hasDefaultOption(option)) {
			final List<String> values = this.getDefaulOptionStringList(option);
			
			for (SequenceType type : SequenceType.values()) {
				for (Database db : this.getRegularDatabases(type)) {
					if (values.contains(db.getDirectory().getAbsolutePath())) {
						return db.getType();
					}
				}
			}
		}
		
		return null;
	}
	
	protected SequenceType getCurrentSequenceType() {
		if (this.lmDatabases == null || this.lmDatabases.isEmpty()) {
			return null;
		} else {
			final Database db = this.lmDatabases.getElementAt(0);
			
			return db.getType();
		}
	}
	
	protected Database getDatabase(String databaseDirectory) {
		for (SequenceType type : SequenceType.values()) {
			for (Database db : this.getRegularDatabases(type)) {
				if (databaseDirectory.equals(db.getDirectory().getAbsolutePath())) {
					return db;
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected <T> Component createComponentForOption(
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		if (this.cmbDatabases == null)
			 this.cmbDatabases = new JComboBox<>();
		
		if (option.isMultiple()) {
			return super.createComponentForOption(option, receiver);
		} else if (option.equals(BLASTDBAliasToolCommand.OPTION_DB_TYPE)) {
			final ParameterValuesReceiver pvr = new ParameterValuesReceiverWrapper(receiver) {
				@Override
				public void setValue(Option<?> option, String value) {
					if (value == null) {
						throw new IllegalStateException("Illegal sequence type value");
					} else {
						final SequenceType currentSequenceType = getCurrentSequenceType();
						final SequenceType newSequenceType = BLASTDBAliasToolCommand.OPTION_DB_TYPE
							.getConverter().convert(new SingleParameterValue(value));
						
						if (lmDatabases != null && !lmDatabases.isEmpty() && 
							currentSequenceType != newSequenceType
						) {
							final int answer = confirmDatabaseTypeChange();
							
							if (answer == JOptionPane.NO_OPTION) {
								inputDBType.setSelectedItem(currentSequenceType);
								return;
							} else {
								lmDatabases.clear();
								receiver.setValue(BLASTDBAliasToolCommand.OPTION_DATABASES, (List<String>) null);
							}
						}
						
						super.setValue(option, value);
						
						final Vector<Database> databases = getRegularDatabases(newSequenceType);
						cmbDatabases.setModel(new DefaultComboBoxModel<>(databases));
						
						if (alDatabases != null)
							alDatabases.actionPerformed(null);
					}
				}

				private int confirmDatabaseTypeChange() {
					final int answer = JOptionPane.showConfirmDialog(
						BLASTDBAliasToolCommandDialog.this, 
						"Changing database type will clear the database selection list. Do you wish to continue?",
						"Database Type Change",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE
					);
					return answer;
				}
			};
			
			if (this.hasDefaultOption(option)) {
				pvr.setValue(option, this.getDefaultOptionString(option));
			} else {
				final SequenceType defaultST = this.getDefaultSequenceType();
				if (defaultST != null) 
					pvr.setValue(option, defaultST.name());
			}
			
			return this.inputDBType = BuildComponent.forEnum(
				this, BLASTDBAliasToolCommand.OPTION_DB_TYPE, pvr
			);
		} else if (option.getParamName().equals(BLASTDBAliasToolCommand.OPTION_DATABASES.getParamName())) {
			this.alDatabases = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Object item = cmbDatabases.getSelectedItem();
					
					if (item == null) {
						receiver.setValue(option, (String) null);
					} else if (item instanceof Database) {
						receiver.setValue(option, ((Database) item).getDirectory().getAbsolutePath());
					}
				}
			};
			this.cmbDatabases.addActionListener(alDatabases);
			
			if (this.hasDefaultOption(option)) {
				final String dbPath = this.getDefaultOptionString(option);
				final int size = this.cmbDatabases.getItemCount();
				
				for (int i = 0; i < size; i++) {
					final Database db = (Database) this.cmbDatabases.getItemAt(i);
					if (db.getDirectory().getAbsoluteFile().equals(dbPath)) {
						this.cmbDatabases.setSelectedIndex(i);
						break;
					}
				}
			}
			
			this.alDatabases.actionPerformed(null);
			this.cmbDatabases.addActionListener(alDatabases);
			
			return cmbDatabases;
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}

	protected Vector<Database> getRegularDatabases(final SequenceType sequenceType) {
		final Vector<Database> databases;
		if (sequenceType == SequenceType.NUCLEOTIDE) {
			databases = new Vector<Database>(Arrays.asList(controller.listNucleotideDatabases()));
		} else if (sequenceType == SequenceType.PROTEIN) {
			databases = new Vector<Database>(Arrays.asList(controller.listProteinDatabases()));
		} else {
			throw new IllegalArgumentException("Unknown option: " + sequenceType);
		}
		
		final Iterator<Database> itDatabases = databases.iterator();
		while (itDatabases.hasNext()) {
			if (!itDatabases.next().isRegular())
				itDatabases.remove();
		}
		
		return databases;
	}
}
