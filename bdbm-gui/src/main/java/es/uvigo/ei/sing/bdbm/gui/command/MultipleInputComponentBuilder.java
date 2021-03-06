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

package es.uvigo.ei.sing.bdbm.gui.command;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import es.uvigo.ei.sing.bdbm.gui.command.input.InputComponentBuilder;
import es.uvigo.ei.sing.yaacli.command.option.Option;

public class MultipleInputComponentBuilder implements InputComponentBuilder {
	@Override
	public boolean canHandle(Option<?> option) {
		return option.isMultiple();
	}

	@Override
	public <T> Component createFor(
		Component parent, 
		Option<T> option,
		ParameterValuesReceiver receiver
	) {
		final JPanel inputComponent = new JPanel(new BorderLayout());
		
		
		
		return inputComponent;
	}
}
