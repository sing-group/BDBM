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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.Option;

public class FileInputComponentBuilder implements InputComponentBuilder {
	private final static JFileChooser FILE_CHOOSER = new JFileChooser(new File("."));
	
	@Override
	public boolean canHandle(Option<?> option) {
		return !option.isMultiple() && File.class.equals(option.getConverter().getTargetClass());
	}

	@Override
	public <T> Component createFor(
		final Component parent, 
		final Option<T> option, 
		final ParameterValuesReceiver receiver
	) {
		if (!this.canHandle(option))
			throw new IllegalArgumentException("Unsupported option type");
		
		final JPanel panel = new JPanel(new BorderLayout());
		final JTextField txtLocation = new JTextField(20);
//		lblLocation.setHorizontalAlignment(SwingConstants.TRAILING);
		txtLocation.setEditable(false);
		
//		if (option instanceof DefaultValuedOption) {
//			final DefaultValuedOption<T> dvOption = (DefaultValuedOption<T>) option;
//			final String defaultValue = dvOption.getDefaultValue();
//			
//			txtLocation.setText(defaultValue);
//			txtLocation.setToolTipText(defaultValue);
//		}
		if (receiver.hasOption(option)) {
			txtLocation.setText(receiver.getValue(option));
			txtLocation.setToolTipText(receiver.getValue(option));
		}
		
		final AbstractAction chooseAction = new AbstractAction("Choose...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (FILE_CHOOSER.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					final String path = FILE_CHOOSER.getSelectedFile().getAbsolutePath();
					
					txtLocation.setText(path);
					txtLocation.setToolTipText(path);
					receiver.setValue(option, path);
				}
			}
		};
		
		final JButton btnChooseFile = new JButton(chooseAction);
		txtLocation.addActionListener(chooseAction);
		
		panel.add(txtLocation, BorderLayout.CENTER);
		panel.add(btnChooseFile, BorderLayout.EAST);
		
		return panel;
	}
}