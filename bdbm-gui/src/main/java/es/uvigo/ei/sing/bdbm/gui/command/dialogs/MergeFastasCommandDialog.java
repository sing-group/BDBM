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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.uvigo.ei.sing.bdbm.cli.commands.MergeFastasCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.FastaValuesProvider;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class MergeFastasCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private JList<Fasta> listFastas;
	private ListSelectionListener lslFastas;
	
	public MergeFastasCommandDialog(
		BDBMController controller, 
		MergeFastasCommand command
	) {
		this(controller, command, null);
	}
	
	public MergeFastasCommandDialog(
		BDBMController controller, 
		MergeFastasCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}
	
	@Override
	protected void preComponentsCreation() {
		this.listFastas = new JList<Fasta>();
		this.listFastas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	@ComponentForOption(MergeFastasCommand.OPTION_FASTA_TYPE_SHORT_NAME)
	protected Component createComponentForFastaTypeOption(
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceType(
			this, option, receiver,
			this.listFastas,
			new FastaValuesProvider(this.controller),
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption(value = MergeFastasCommand.OPTION_FASTAS_SHORT_NAME, allowsMultiple = true)
	protected Component createComponentForFastasOption(
		final FileOption option, 
		final ParameterValuesReceiver receiver
	) {
		this.lslFastas = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final List<Fasta> selectedFastas = listFastas.getSelectedValuesList();
				
				if (selectedFastas.isEmpty()) {
					receiver.setValue(option, (List<String>) null);
					receiver.removeValue(option);
				} else {
					final List<String> fastaPaths = new ArrayList<>(selectedFastas.size());
					for (Fasta fasta : selectedFastas) {
						fastaPaths.add(fasta.getFile().getAbsolutePath());
					}
					
					receiver.setValue(option, fastaPaths);
				}
			}
		};
		
		this.lslFastas.valueChanged(null);
		this.listFastas.addListSelectionListener(this.lslFastas);
		
		return new JScrollPane(this.listFastas);
	}
}
