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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.Option;
import es.uvigo.ei.sing.yaacli.OptionConverter;
import es.uvigo.ei.sing.yaacli.SingleParameterValue;

public class EnumInputComponentBuilder implements InputComponentBuilder {
	@Override
	public boolean canHandle(Option<?> option) {
		return !option.isMultiple() && option.getConverter().getTargetClass().isEnum();
	}

	@Override
	public <T> JComboBox<T> createFor(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		if (!this.canHandle(option))
			throw new IllegalArgumentException("Unsupported option type");
		
		final OptionConverter<T> converter = option.getConverter();
		final T[] enumConstants = converter.getTargetClass().getEnumConstants();
		
		final JComboBox<T> cmbEnum;
		if (option.isOptional()) {
			@SuppressWarnings("unchecked")
			final T[] enumConstantsWithEmpty = (T[]) new Object[enumConstants.length+1];
			System.arraycopy(enumConstants, 0, enumConstantsWithEmpty, 1, enumConstants.length);
			enumConstantsWithEmpty[0] = null;
			
			cmbEnum = new JComboBox<>(enumConstantsWithEmpty);
		} else {
			cmbEnum = new JComboBox<>(enumConstants);
		}
		
		
		if (receiver.hasValue(option)) {
			final SingleParameterValue value = new SingleParameterValue(receiver.getValue(option));
			
			cmbEnum.setSelectedItem(converter.convert(value));
		} else if (!option.isOptional()) {
			cmbEnum.setSelectedIndex(0);
			receiver.setValue(option, ((Enum<?>) enumConstants[0]).name());
		}
		
		cmbEnum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Object selectedItem = cmbEnum.getSelectedItem();
				
				if (selectedItem instanceof Enum) {
					receiver.setValue(option, ((Enum<?>) selectedItem).name());
				} else {
					receiver.setValue(option, (String) null);
				}
			}
		});
		
		return cmbEnum;
	}

}
