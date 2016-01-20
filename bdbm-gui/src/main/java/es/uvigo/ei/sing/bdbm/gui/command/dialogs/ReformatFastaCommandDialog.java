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
package es.uvigo.ei.sing.bdbm.gui.command.dialogs;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bdbm.cli.commands.ReformatFastaCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.EnumOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.IntegerOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils.RenameMode;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.FastaValuesProvider;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ReformatFastaCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<Fasta> cmbFastas;
	
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
	protected void preComponentsCreation() {
		this.cmbFastas = new JComboBox<Fasta>();
	}
	
	@Override
	protected <T> void postComponentCreation(
		Component component,
		Option<T> option,
		ParameterValuesReceiver receiver
	) {
		if (option.equals(ReformatFastaCommand.OPTION_SEPARATOR)) {
			this.cmpSeparator = component;
			component.setVisible(false);
		} else if (option.equals(ReformatFastaCommand.OPTION_PREFIX)) {
			this.cmpPrefix = component;
			component.setVisible(false);
		} else if (option.equals(ReformatFastaCommand.OPTION_KEEP_NAMES_WHEN_PREFIX)) {
			this.cmpKeepNames = component;
			component.setVisible(false);
		} else if (option.equals(ReformatFastaCommand.OPTION_ADD_INDEX_WHEN_PREFIX)) {
			this.cmpAddIndex = component;
			component.setVisible(false);
		} else if (option.equals(ReformatFastaCommand.OPTION_INDEXES)) {
			this.cmpIndexes = component;
			component.setVisible(false);
		}
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_FASTA_TYPE_SHORT_NAME)
	protected Component createComponentForFastaTypeOption(
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceType(
			this, option, receiver, this.cmbFastas,
			new FastaValuesProvider(this.controller),
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_FASTA_SHORT_NAME)
	protected Component createComponentForFastaOption(
		final FileOption option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, this.cmbFastas,
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_FRAGMENT_LENGTH_SHORT_NAME)
	protected Component createComponentForFragmentLengthOption(
		final IntegerOption option, 
		final ParameterValuesReceiver receiver
	) {
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
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_RENAMING_MODE_SHORT_NAME)
	protected Component createComponentForFragmentLengthOption(
		final EnumOption<RenameMode> option, 
		final ParameterValuesReceiver receiver
	) {
		final JComboBox<RenameMode> cmbMode = BuildComponent.forEnum(this, option, receiver);
		
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
	}
}
