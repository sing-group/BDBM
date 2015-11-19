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

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.command.option.Option;

public class DefaultInputComponentBuilder implements InputComponentBuilder {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultInputComponentBuilder.class);
	
	@Override
	public boolean canHandle(Option<?> option) {
		return !option.isMultiple();
	}
	
	@Override
	public <T> Component createFor(
		final Component parent, 
		final Option<T> option,
		final ParameterValuesReceiver receiver
	) {
		if (!this.canHandle(option))
			throw new IllegalArgumentException("Unsupported option type");
		
		if (option.requiresValue()) {
			final JTextField txt = new JTextField();
			
			if (receiver.hasOption(option)) {
				txt.setText(receiver.getValue(option));
			}
//			
//			final KeyAdapter txtListener = new KeyAdapter() {
//				@Override
//				public void keyReleased(KeyEvent e) {
//					try {
//						final JTextField txt = (JTextField) e.getComponent();
//						
//						if (txt.getText().isEmpty()) {
//							receiver.removeValue(option);
//						} else {
//							receiver.setValue(option, txt.getText());
//						}
//					} catch (Exception ex) {
//						LOG.error("Error setting option value", ex);
//					}
//				}
//			};
			
			final DocumentListener docListener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					updateReceiver();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					updateReceiver();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					updateReceiver();
				}

				public void updateReceiver() {
					try {
						if (txt.getText().isEmpty()) {
							receiver.removeValue(option);
						} else {
							receiver.setValue(option, txt.getText());
						}
					} catch (Exception ex) {
						LOG.error("Error setting option value", ex);
					}
				}
			};
			
			docListener.changedUpdate(null);
			txt.getDocument().addDocumentListener(docListener);
			
//			txtListener.keyReleased(null);
//			txt.addKeyListener(txtListener);
			
			return txt;
		} else {
			final JCheckBox chk = new JCheckBox();

			final ActionListener chkListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (chk.isSelected()) {
						receiver.setValue(option, "true");
					} else {
						receiver.removeValue(option);
					}
				}
			};
			
			chkListener.actionPerformed(null);
			chk.addActionListener(chkListener);
			
			return chk;
		}
	}
}
