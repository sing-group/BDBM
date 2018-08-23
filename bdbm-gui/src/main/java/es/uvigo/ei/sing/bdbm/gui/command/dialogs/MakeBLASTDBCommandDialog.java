/*-
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import es.uvigo.ei.sing.bdbm.cli.commands.MakeBLASTDBCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.FastaValuesProvider;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class MakeBLASTDBCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<Fasta> cmbFastas;
	
	public MakeBLASTDBCommandDialog(
		BDBMController controller, 
		MakeBLASTDBCommand command
	) {
		this(controller, command, null);
	}
	
	public MakeBLASTDBCommandDialog(
		BDBMController controller, 
		MakeBLASTDBCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.pack();
	}
	
	@Override
	protected void preComponentsCreation(PanelOptionsBuilder panelOptionBuilder) {
		this.cmbFastas = new JComboBox<Fasta>();
	}
	
	@ComponentForOption(MakeBLASTDBCommand.OPTION_DB_TYPE_SHORT_NAME)
	private Component createComponentForDBTypeOption(
		final Option<SequenceType> option,
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceType(
			this, option, receiver, this.cmbFastas,
			new FastaValuesProvider(this.controller),
			this.getDefaultOptionString(option)
		);
	}
	
	@ComponentForOption(MakeBLASTDBCommand.OPTION_INPUT_SHORT_NAME)
	private Component createComponentForInputOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, this.cmbFastas,
			this.getDefaultOptionString(option)
		);
	}
}
