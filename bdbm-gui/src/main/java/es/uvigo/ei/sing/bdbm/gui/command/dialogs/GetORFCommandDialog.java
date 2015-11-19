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

import javax.swing.JComboBox;

import es.uvigo.ei.sing.bdbm.cli.commands.GetORFCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class GetORFCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	public GetORFCommandDialog(
		BDBMController controller, 
		GetORFCommand command
	) {
		this(controller, command, null);
	}
	
	public GetORFCommandDialog(
		BDBMController controller, 
		GetORFCommand command,
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
		if (option.equals(GetORFCommand.OPTION_FASTA)) {
			final NucleotideFasta[] nucleotideFasta = 
				this.controller.listNucleotideFastas();
			final JComboBox<NucleotideFasta> cmbFastas =
				new JComboBox<>(nucleotideFasta);
			
			if (this.hasDefaultOption(option)) {
				final String defaultFasta = this.getDefaultOptionString(option);
				for (NucleotideFasta fasta : nucleotideFasta) {
					if (fasta.getFile().getAbsolutePath().equals(defaultFasta)) {
						cmbFastas.setSelectedItem(fasta);
					}
				}
			}/* else {
				final Object value = cmbFastas.getSelectedItem();

				if (value != null) {
					final NucleotideFasta fasta = (NucleotideFasta) value;
					receiver.setValue(
						option, 
						fasta.getFile().getAbsolutePath()
					); 
				}
			}*/
			
			final ActionListener alFastas = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final Object item = cmbFastas.getSelectedItem();
					
					if (item == null) {
						receiver.setValue(option, (String) null);
					} else if (item instanceof NucleotideFasta) {
						final NucleotideFasta fasta = (NucleotideFasta) item;
						receiver.setValue(
							option, fasta.getFile().getAbsolutePath()
						);
					}
				}
			};
			alFastas.actionPerformed(null);
			
			cmbFastas.addActionListener(alFastas);
			
			return cmbFastas;
		} else {
			return super.createComponentForOption(option, receiver);
		}
	}
}
