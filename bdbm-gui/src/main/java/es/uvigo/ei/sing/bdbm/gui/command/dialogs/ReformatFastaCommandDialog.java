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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bdbm.cli.commands.ReformatFastaCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils.RenameMode;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class ReformatFastaCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<Fasta> cmbFastas;
	private ActionListener alFastas;
	
	private Component cmpSeparator, cmpPrefix, cmpKeepNames, cmpAddIndex, cmpIndexes;
	
	public ReformatFastaCommandDialog(
		BDBMController controller, 
		ReformatFastaCommand command
	) {
		this(controller, command, null);
	}
	
	public ReformatFastaCommandDialog(
		BDBMController controller, 
		ReformatFastaCommand command,
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
		if (this.cmbFastas == null) {
			this.cmbFastas = new JComboBox<Fasta>();
		}
		
		if (option.equals(ReformatFastaCommand.OPTION_FASTA_TYPE)) {
			final ParameterValuesReceiver pvr = new ParameterValuesReceiverWrapper(receiver) {
				@Override
				public void setValue(Option<?> option, String value) {
					super.setValue(option, value);
					
					if (value == null) {
						cmbFastas.setModel(new DefaultComboBoxModel<Fasta>());
					} else {
						final Object convertedValue = 
							option.getConverter().convert(new SingleParameterValue(value));
						
						final Fasta[] fastas;
						if (convertedValue == SequenceType.NUCLEOTIDE) {
							fastas = controller.listNucleotideFastas();
						} else if (convertedValue == SequenceType.PROTEIN) {
							fastas = controller.listProteinFastas();
						} else {
							throw new IllegalArgumentException("Unknown option: " + convertedValue);
						}
						
						cmbFastas.setModel(new DefaultComboBoxModel<>(fastas));
						
						if (alFastas != null)
							alFastas.actionPerformed(null);
					}
				}
			};
			
			pvr.setValue(option, this.getDefaultOptionString(option));
			
			return BuildComponent.forEnum(this, option, pvr);
		} else if (option.equals(ReformatFastaCommand.OPTION_FASTA)) {
			this.alFastas = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Object item = cmbFastas.getSelectedItem();
					
					if (item == null) {
						receiver.setValue(option, (String) null);
					} else if (item instanceof Fasta) {
						receiver.setValue(option, ((Fasta) item).getFile().getAbsolutePath());
					}
				}
			};
			
			if (this.hasDefaultOption(option)) {
				final String fastaPath = this.getDefaultOptionString(option);
				final int size = this.cmbFastas.getItemCount();
				
				for (int i = 0; i < size; i++) {
					final Fasta fasta = (Fasta) this.cmbFastas.getItemAt(i);
					
					if (fasta.getFile().getAbsolutePath().equals(fastaPath)) {
						this.cmbFastas.setSelectedIndex(i);
						break;
					}
				}
			}
			
			this.alFastas.actionPerformed(null);
			this.cmbFastas.addActionListener(alFastas);
			
			return cmbFastas;
		} else if (option.equals(ReformatFastaCommand.OPTION_FRAGMENT_LENGTH)) {
			final JCheckBox chkChangeLength = new JCheckBox("Change sequence length?", false);
			final JTextField component = (JTextField) BuildComponent.forOption(this, option, receiver);
			component.setEnabled(false);
			component.setText("");
			
			chkChangeLength.addActionListener(new ActionListener() {
				private String lastValue = "0";
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chkChangeLength.isSelected()) {
						component.setText(this.lastValue);
						component.setEnabled(true);
					} else {
						component.setEnabled(false);
						lastValue = receiver.getValue(option);
						component.setText("");
					}
				}
			});
			
			final JPanel panel = new JPanel(new GridLayout(2, 1));
			panel.add(chkChangeLength);
			panel.add(component);
			
			return panel;
		} else if (option.equals(ReformatFastaCommand.OPTION_RENAMING_MODE)) {
			final JComboBox<T> cmbMode = BuildComponent.forEnum(this, option, receiver);
			
			cmbMode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmpPrefix != null)
						cmpPrefix.setVisible(cmbMode.getSelectedItem() == RenameMode.PREFIX);
					if (cmpKeepNames != null)
						cmpKeepNames.setVisible(cmbMode.getSelectedItem() == RenameMode.PREFIX);
					if (cmpAddIndex != null)
						cmpAddIndex.setVisible(cmbMode.getSelectedItem() == RenameMode.PREFIX);
					if (cmpSeparator != null)
						cmpSeparator.setVisible(cmbMode.getSelectedItem() != RenameMode.NONE);
					if (cmpIndexes != null)
						cmpIndexes.setVisible(cmbMode.getSelectedItem() == RenameMode.GENERIC);
					
					ReformatFastaCommandDialog.this.pack();
				}
			});
			
			return cmbMode;
		} else if (option.equals(ReformatFastaCommand.OPTION_SEPARATOR)) {
			this.cmpSeparator = super.createComponentForOption(option, receiver);
			this.cmpSeparator.setVisible(false);
			return this.cmpSeparator;
		} else if (option.equals(ReformatFastaCommand.OPTION_PREFIX)) {
			this.cmpPrefix = super.createComponentForOption(option, receiver);
			this.cmpPrefix.setVisible(false);
			return this.cmpPrefix;
		} else if (option.equals(ReformatFastaCommand.OPTION_KEEP_NAMES_WHEN_PREFIX)) {
			this.cmpKeepNames = super.createComponentForOption(option, receiver);
			this.cmpKeepNames.setVisible(false);
			return this.cmpKeepNames;
		} else if (option.equals(ReformatFastaCommand.OPTION_ADD_INDEX_WHEN_PREFIX)) {
			this.cmpAddIndex = super.createComponentForOption(option, receiver);
			this.cmpAddIndex.setVisible(false);
			return this.cmpAddIndex;
		} else if (option.equals(ReformatFastaCommand.OPTION_INDEXES)) {
			this.cmpIndexes = super.createComponentForOption(option, receiver);
			this.cmpIndexes.setVisible(false);
			return this.cmpIndexes;
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}
}
