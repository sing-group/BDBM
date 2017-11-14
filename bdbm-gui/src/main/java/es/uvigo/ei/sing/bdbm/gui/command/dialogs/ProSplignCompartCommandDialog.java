/*-
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import javax.swing.JComboBox;

import es.uvigo.ei.sing.bdbm.cli.commands.ProSplignCompartCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ProSplignCompartCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	public ProSplignCompartCommandDialog(
		BDBMController controller, 
		ProSplignCompartCommand command
	) {
		this(controller, command, null);
	}
	
	public ProSplignCompartCommandDialog(
		BDBMController controller, 
		ProSplignCompartCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}
	
	@ComponentForOption({
		ProSplignCompartCommand.OPTION_GENOME_FASTA_SHORT_NAME
	})
	private Component createComponentForCDSAndGenomeFastaOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, new JComboBox<>(this.controller.listNucleotideFastas()),
			this.getDefaultOptionString(option)
		);
	}

	@ComponentForOption({
		ProSplignCompartCommand.OPTION_QUERY_SHORT_NAME
	})
	private Component createComponentForProteinQueryOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
		) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, new JComboBox<>(this.controller.listProteinFastas()),
			this.getDefaultOptionString(option)
		);
	}
}
