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
package es.uvigo.ei.sing.bdbm.gui.command.dialogs;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.BLASTNCommand;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.binaries.BLASTType;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.ValueCallback;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.yaacli.command.option.FileOption;
import es.uvigo.ei.sing.yaacli.command.option.StringOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public abstract class BLASTCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;
	
	private JTextArea taAdditionalParameters;
	
	public BLASTCommandDialog(
		BDBMController controller, 
		BLASTCommand command
	) {
		this(controller, command, null);
	}
	
	public BLASTCommandDialog(
		BDBMController controller, 
		BLASTCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);
		
		this.setMinimumSize(new Dimension(500, 200));
		this.pack();
	}
	
	protected abstract Database[] listDatabases();
	protected abstract SearchEntry[] listSearchEntries();
	protected abstract BLASTType getBlastType();
	
	@ComponentForOption(BLASTCommand.OPTION_DATABASE_SHORT_NAME)
	protected Component createComponentForDatabaseOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, new JComboBox<Database>(this.listDatabases()),
			this.getDefaultOptionString(option),
			new ValueCallback<Database>() {
				@Override
				public void callback(Database value) {
					if (value == null) {
						receiver.setValue(option, (String) null);
					} else {
						receiver.setValue(option, new File(value.getBaseFile(), value.getName()).getAbsolutePath());
					}
				}
			}
		);
	}

	@ComponentForOption(BLASTCommand.OPTION_QUERY_SHORT_NAME)
	protected Component createComponentForQueryOption(
		final FileOption option,
		final ParameterValuesReceiver receiver
	) {
		final JPanel panel = new JPanel(new GridLayout(2, 1));

		final JComboBox<Query> cmbQueries = new JComboBox<>();
		
		cmbQueries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Query query = cmbQueries.getItemAt(cmbQueries.getSelectedIndex());
				
				BLASTCommandDialog.this.updateQuery(query, receiver);
			}
		});
		
		final JComboBox<SearchEntry> cmbSearchEntries =
			ComponentFactory.createComponentForSequenceEntityValues(
				option, receiver, new JComboBox<SearchEntry>(this.listSearchEntries()),
				this.getDefaultOptionString(option),
				new ValueCallback<SearchEntry>() {
					public void callback(SearchEntry searchEntry) {
						if (searchEntry != null && !searchEntry.listQueries().isEmpty()) {
							cmbQueries.setModel(new DefaultComboBoxModel<>(
								new Vector<>(searchEntry.listQueries())
							));
							cmbQueries.setSelectedIndex(0);
						} else {
							cmbQueries.setModel(new DefaultComboBoxModel<Query>());
							cmbQueries.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Model changed"));
						}
					}
				}
			);
		
		panel.add(cmbSearchEntries);
		panel.add(cmbQueries);
		
		return panel;
	}
	
	protected void updateQuery(Query query, ParameterValuesReceiver receiver) {
		if (query == null) {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, (String) null);
		} else {
			receiver.setValue(BLASTNCommand.OPTION_QUERY, query.getBaseFile().getAbsolutePath());
		}
		
		this.updateButtonOk();
	}
	
	
	@Override
	protected void postComponentsCreation(PanelOptionsBuilder panelOptionsBuilder) {
		final JButton btnShow = new JButton("Show additional parameters");
		
		taAdditionalParameters = new JTextArea(getAdditionalParametersString());
		taAdditionalParameters.setLineWrap(true);
		taAdditionalParameters.setWrapStyleWord(true);
		taAdditionalParameters.setRows(1);
		
		
		final JPanel panel = new JPanel();
		final CardLayout layout = new CardLayout();
		panel.setLayout(layout);
		
		panel.add(btnShow);
		panel.add(new JScrollPane(taAdditionalParameters));
		panelOptionsBuilder.addOptionRow(
			"Additional parameters",
			"This option allows you to introduce additional parameters to the blast operation.",
			panel
		);
		
		btnShow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(
					BLASTCommandDialog.this,
					"<html>"
					+ "Using custom additional parameters may cause execution<br/>"
					+ "fails or output files incompatible with other BDBM options.<br/>"
					+ "Therefore, it is recommended to use this option carefully <br/>"
					+ "and only by advanced BLAST users.<br/><br/>"
					+ "Do you still want to continue?"
					+ "</html>",
					"BLAST Advanced Options",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
				) == JOptionPane.YES_OPTION) {
					layout.next(panel);
					taAdditionalParameters.setRows(4);
					BLASTCommandDialog.this.pack();
				}
			}
		});

		taAdditionalParameters.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				parameterValues.removeNonDefaultOptions();
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				if (areAdditionalParametersValid()) {
					final Map<String, String> parameters = getAdditionalParameters();
					
					for (Map.Entry<String, String> parameter : parameters.entrySet()) {
						final boolean requiresValue = parameter.getValue() != null;
						final String paramName = parameter.getKey();
						
						parameterValues.setValue(
							new StringOption(paramName, paramName, paramName, false, requiresValue),
							requiresValue ? parameter.getValue() : ""
						);
					}
				} else {
					JOptionPane.showMessageDialog(
						BLASTCommandDialog.this,
						"The format of the additional parameters is invalid. Please, fix them before continue.",
						"Invalid Format",
						JOptionPane.ERROR_MESSAGE
					);
				}
				
				updateButtonOk();
			}
		});
	}

	@Override
	protected void updateButtonOk() {
		this.btnOk.setEnabled(this.parameterValues.isComplete() && areAdditionalParametersValid());
	}

	private String getAdditionalParametersString() {
		final StringBuilder sb = new StringBuilder();
		
		final Map<String, String> additionalParameters =
			this.controller.getBlastAdditionalParameters(getBlastType());
		for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
			sb.append(" -").append(entry.getKey().trim());
			
			final String value = entry.getValue();
			if (value != null) {
				sb.append(' ').append(value);
			}
		}
		
		return sb.toString().trim();
	}
	
	private Map<String, String> getAdditionalParameters() {
		final String[] params = this.taAdditionalParameters.getText().trim().split("\\s+");
		
		final Map<String, String> parameters = new LinkedHashMap<>();
		for (int i = 0; i < params.length; i++) {
			if (!params[i].startsWith("-")) {
				throw new IllegalArgumentException("Invalid parameters format");
			}
			
			final String paramName = params[i].substring(1);
			if (i < params.length - 1 && !params[i + 1].startsWith("-")) {
				parameters.put(paramName, params[i + 1]);
				i++;
			} else {
				parameters.put(paramName, null);
			}
		}
		
		return parameters;
	}
	
	private boolean areAdditionalParametersValid() {
		if (taAdditionalParameters.getText().trim().isEmpty()) {
			return true;
		} else {
			final String[] params = this.taAdditionalParameters.getText().trim().split("\\s+");
			
			for (int i = 0; i < params.length; i++) {
				if (!params[i].startsWith("-")) {
					return false;
				} else if (i < params.length - 1 && !params[i + 1].startsWith("-")) {
					i++;
				}
			}
			
			return true;
		}
	}
}
