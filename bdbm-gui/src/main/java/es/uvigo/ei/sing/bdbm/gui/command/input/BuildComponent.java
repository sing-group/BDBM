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
package es.uvigo.ei.sing.bdbm.gui.command.input;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.command.option.Option;

public class BuildComponent {
	public static <T> Component forOption(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		return new DefaultInputComponentBuilder().createFor(parent, option, receiver);
	}
	
	public static <T> JComboBox<T> forEnum(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		return new EnumInputComponentBuilder().createFor(parent, option, receiver);
	}
	
	public static <T> Component forFile(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		return new FileInputComponentBuilder().createFor(parent, option, receiver);
	}
	
	public static <T> JCheckBox forBoolean(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		return new BooleanInputComponentBuilder().createFor(parent, option, receiver);
	}
	
	public static <T> JSpinner forPositiveInteger(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		return new PositiveIntegerInputComponentBuilder().createFor(parent, option, receiver);
	}
}
