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
package es.uvigo.ei.sing.bdbm.gui.command.input;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.command.option.Option;

public class BooleanInputComponentBuilder implements InputComponentBuilder {
	@Override
	public boolean canHandle(Option<?> option) {
		return Boolean.class.equals(option.getConverter().getTargetClass());
	}
	
	@Override
	public <T> JCheckBox createFor(
		final Component parent, 
		final Option<T> option,
		final ParameterValuesReceiver receiver
	) {
		if (!this.canHandle(option))
			throw new IllegalArgumentException("Unsupported option type");
		
		final JCheckBox chk = new JCheckBox();
		
		if (receiver.hasValue(option)) {	
			chk.setSelected(Boolean.valueOf(receiver.getValue(option)));
		} else {
			receiver.setValue(option, Boolean.toString(chk.isSelected()));
		}

		chk.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				receiver.setValue(option, Boolean.toString(chk.isSelected()));
			}
		});
		
		return chk;
	}
}
