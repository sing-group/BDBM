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
package es.uvigo.ei.sing.bdbm.gui.command;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ServiceLoader;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.gui.command.input.DefaultInputComponentBuilder;
import es.uvigo.ei.sing.bdbm.gui.command.input.InputComponentBuilder;
import es.uvigo.ei.sing.yaacli.command.Command;
import es.uvigo.ei.sing.yaacli.command.option.DefaultValuedOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class CommandDialog extends JDialog {
	private final static long serialVersionUID = 1L;
	private final static Logger LOG = LoggerFactory.getLogger(CommandDialog.class);
	
	private final static ImageIcon ICON_HELP = 
		new ImageIcon(CommandDialog.class.getResource("images/help-about.png"));
	
	private final static ServiceLoader<InputComponentBuilder> SERVICE_LOADER = 
		ServiceLoader.load(InputComponentBuilder.class);

	private static final InputComponentBuilder DEFAULT_INPUT_COMPONENT_BUILDER = 
		new DefaultInputComponentBuilder();

	protected final BDBMController controller;
	protected final Command command;
	private final Parameters defaultParameters;

	protected JButton btnOk;

	protected ParameterValues parameterValues;

	public CommandDialog(BDBMController controller, Command command) {
		this(controller, command, null);
	}
	
	public CommandDialog(BDBMController controller, Command command, Parameters defaultParameters) {
		this(controller, command, defaultParameters, true);
	}
	
	public CommandDialog(BDBMController controller, Command command, Parameters defaultParameters, boolean init) {
		this.controller = controller;
		this.command = command;
		this.defaultParameters = defaultParameters;
		
		if (init)
			this.init();
	}
	
	protected void asynchronousPack() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CommandDialog.this.revalidate();
				CommandDialog.this.pack();
			}
		});
	}
	
	protected boolean hasDefaultValue(Option<?> option) {
		return this.defaultParameters != null && this.defaultParameters.hasOption(option);
	}
	
	protected String getDefaultOptionString(Option<?> option) {
		if (this.hasDefaultValue(option)) {
			return this.defaultParameters.getSingleValueString(option);
		} else {
			return null;
		}
	}
	
	protected List<String> getDefaultOptionStringList(Option<?> option) {
		if (this.hasDefaultValue(option)) {
			return this.defaultParameters.getAllValuesString(option);
		} else {
			return null;
		}
	}

	protected final static class PanelOptionsBuilder {
		private final JPanel panelOptions;
		
		private final GroupLayout groupLayout;
		private final SequentialGroup verticalGroup;
		private final ParallelGroup pgLblName;
		private final ParallelGroup pgText;
		private final ParallelGroup pgLblDescription;
		
		public PanelOptionsBuilder() {
			this.panelOptions = new JPanel();
			this.panelOptions.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			
			this.groupLayout = new GroupLayout(this.panelOptions);
			this.groupLayout.setAutoCreateContainerGaps(true);
			this.groupLayout.setAutoCreateGaps(true);
			
			this.groupLayout.setVerticalGroup(this.verticalGroup = this.groupLayout.createSequentialGroup());
			this.groupLayout.setHorizontalGroup(
				this.groupLayout.createSequentialGroup()
					.addGroup(this.pgLblName = this.groupLayout.createParallelGroup(Alignment.LEADING, false))
					.addGroup(this.pgText = this.groupLayout.createParallelGroup())
					.addGroup(this.pgLblDescription = this.groupLayout.createParallelGroup(Alignment.CENTER, false))
			);
			this.panelOptions.setLayout(this.groupLayout);
		}
		
		public JPanel getPanelOptions() {
			return this.panelOptions;
		}

		public void addOptionRow(String name, String txtDescription, Component inputComponent) {
			final JLabel lblName = new JLabel(name);
			lblName.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
			final JLabel lblDescription = new JLabel(CommandDialog.ICON_HELP);
			
			final String description = StringEscapeUtils.escapeHtml4(txtDescription)
				.replaceAll("\n", "<br/>")
				.replaceAll("\t", "&nbsp;&nbsp;&nbsp;");
			
			lblDescription.setToolTipText("<html>" + description + "</html>");
			
			this.verticalGroup.addGroup(
				this.groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
					.addComponent(lblName, Alignment.LEADING)
					.addComponent(inputComponent)
					.addComponent(lblDescription)
			);
			
			this.pgLblName.addComponent(lblName);
			this.pgText.addComponent(inputComponent);
			this.pgLblDescription.addComponent(lblDescription);
			
			inputComponent.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					lblName.setVisible(true);
					lblDescription.setVisible(true);
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					lblName.setVisible(false);
					lblDescription.setVisible(false);
				}
			});
			lblName.setVisible(inputComponent.isVisible());
			lblDescription.setVisible(inputComponent.isVisible());
		}
	}
	
	protected void init() {
		this.setTitle(this.command.getDescriptiveName());
		this.setMinimumSize(new Dimension(400, 200));
		
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(new BorderLayout());
	
		final JTextArea taDescription = new JTextArea(this.command.getDescription());
		taDescription.setEditable(false);
		taDescription.setWrapStyleWord(true);
		taDescription.setLineWrap(true);
		taDescription.setMargin(new Insets(10, 8, 10, 8));
		
		final JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		
		btnOk = new JButton("Ok");
		final JButton btnCancel = new JButton("Cancel");
		panelButtons.add(btnOk);
		panelButtons.add(btnCancel);
		
		final PanelOptionsBuilder panelOptionsBuilder = new PanelOptionsBuilder();
		
		this.parameterValues = new ParameterValues(this.command.getOptions());
		this.updateButtonOk();
		this.parameterValues.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				CommandDialog.this.updateButtonOk();
			}
		});
		
		this.preComponentsCreation(panelOptionsBuilder);
		for (Option<?> option : this.command.getOptions()) {
			this.preComponentCreation(option, parameterValues, panelOptionsBuilder);
			final Component inputComponent = this.createComponentForOption(option, parameterValues);
			final String optionName = this.createOptionName(option);
			final String optionDescription = this.createOptionDescription(option);
			
			panelOptionsBuilder.addOptionRow(optionName, optionDescription, inputComponent);
			this.postComponentCreation(inputComponent, option, parameterValues, panelOptionsBuilder);
		}
		this.postComponentsCreation(panelOptionsBuilder);
		
		panel.add(taDescription, BorderLayout.NORTH);
		panel.add(panelOptionsBuilder.getPanelOptions(), BorderLayout.CENTER);
		panel.add(panelButtons, BorderLayout.SOUTH);
		
		this.setContentPane(panel);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					CommandDialog.this.setVisible(false);
				}
			}
		});
		
		this.btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommandDialog.this.setVisible(false);
				CommandDialog.this.dispose();
				
				final CommandExecutionDialog executionDialog = 
					new CommandExecutionDialog(getOwner(), command, parameterValues);
				executionDialog.pack();
				executionDialog.setLocationRelativeTo(getOwner());
				executionDialog.startExecution();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CommandDialog.this.setVisible(false);
			}
		});
	}

	private static class MICBParameterValuesReceiver 
	extends Observable
	implements ParameterValuesReceiver {
		private Option<?> option = null;
		private String lastValue = null;
		
		@Override
		public boolean hasOption(Option<?> option) {
			return this.option != null && this.option.equals(option);
		}
		
		@Override
		public String getValue(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean hasValue(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public List<String> getValues(Option<?> option) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeValue(Option<?> option) {
			if (this.option != null && this.option.equals(option)) {
				this.option = null;
				this.lastValue = null;
				return true;
			} else {
				return false;
			}
		}
		
		public boolean hasValue() {
			return this.lastValue != null;
		}
		
		public String getValue() {
			return this.lastValue;
		}
		
		@Override
		public void setValue(Option<?> option, String value) {
			this.lastValue = value;
			this.option = option;
			this.setChanged();
			this.notifyObservers();
		}
	
		@Override
		public void setValue(Option<?> option, List<String> value) {
			throw new IllegalStateException("Multiple values not supported");
		}
	}
	
	protected static List<String> listModelToList(ListModel<Object> listModel) {
		final List<String> list = new ArrayList<String>(listModel.getSize());
		
		for (int i = 0; i < listModel.getSize(); i++) {
			list.add(listModel.getElementAt(i).toString());
		}
		
		return list;
	}
	
	protected <T> List<Method> getComponentForOptionMethods(
		final Option<T> option, final ParameterValuesReceiver receiver
	) {
		final List<Method> methods = new LinkedList<>();
		
		for (Class<?> clazz = this.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(ComponentForOption.class)) {
					final Class<?>[] paramTypes = method.getParameterTypes();
					
					if (Component.class.isAssignableFrom(method.getReturnType())
						&& paramTypes.length == 2
						&& paramTypes[0].isAssignableFrom(option.getClass())
						&& paramTypes[1].isAssignableFrom(receiver.getClass())
					) {
						methods.add(method);
					}
				}
			}
		}
		
		return methods;
	}
	
	private static <T> boolean creatorIsValidForOption(Method method, Option<T> option) {
		final ComponentForOption cfoAnnotation =
			method.getAnnotation(ComponentForOption.class);
			
		for (String value : cfoAnnotation.value()) {
			if (value.equals(option.getShortName())
				&& (!option.isMultiple() || cfoAnnotation.allowsMultiple())) {
				return true;
			}
		}
		
		return false;
	}
	
	protected <T> Component customComponentCreation(
		final Option<T> option, final ParameterValuesReceiver receiver
	) {
		for (Method method : getComponentForOptionMethods(option, receiver)) {
			if (creatorIsValidForOption(method, option)) {
				final boolean accesible = method.isAccessible();
				try {
					method.setAccessible(true);
					return (Component) method.invoke(this, option, receiver);
				} catch (Exception e) {
					LOG.error("Error invoking method: " + method, e);
					return null;
				} finally {
					method.setAccessible(accesible);
				}
			}
		}
		
		return null;
	}
	
	protected void preComponentsCreation(PanelOptionsBuilder panelOptionsBuilder) {}
	
	protected void postComponentsCreation(PanelOptionsBuilder panelOptionsBuilder) {}
	
	protected <T> void preComponentCreation(
		final Option<T> option,
		final ParameterValuesReceiver receiver,
		final PanelOptionsBuilder panelOptionsBuilder
	) {}

	protected <T> void postComponentCreation(
		final Component component,
		final Option<T> option,
		final ParameterValuesReceiver receiver,
		final PanelOptionsBuilder panelOptionsBuilder
	) {}
	
	protected <T> Component createComponentForOptionNoReflection(
		final Option<T> option, final ParameterValuesReceiver receiver
	) {
		if (option.isMultiple()) {
			final JPanel panelInput = new JPanel(new BorderLayout());
			final JPanel panelInner = new JPanel(new BorderLayout());
			
			final MICBParameterValuesReceiver micbReceiver = 
				new MICBParameterValuesReceiver();
			
			if (receiver.hasOption(option)) {
				micbReceiver.setValue(option, receiver.getValue(option));
				receiver.removeValue(option);
			}
			
			final Component singleInputComponent = 
				this.createComponentForOption(
					new Option<T>(
						option.getParamName(),
						option.getShortName(),
						option.getDescription(),
						option.isOptional(),
						option.requiresValue(),
						false,
						option.getConverter()
					),
					micbReceiver
				);
			
			final JPanel panelButtons = new JPanel(new GridLayout(1, 3));
			final JButton btnAdd = new JButton("Add");
			final JButton btnRemove = new JButton("Remove");
			final JButton btnClear = new JButton("Clear");
			btnAdd.setEnabled(micbReceiver.hasValue());
			btnRemove.setEnabled(false);
			btnClear.setEnabled(false);
			
			panelButtons.add(btnAdd);
			panelButtons.add(btnRemove);
			panelButtons.add(btnClear);
			
			final DefaultListModel<Object> listModel = new DefaultListModel<>();
			this.setMultipleDefaultParameters(option, receiver, listModel);
			final JList<Object> listValues = new JList<>(listModel);
			listValues.setVisibleRowCount(6);
			
			panelInner.add(panelButtons, BorderLayout.NORTH);
			panelInner.add(new JScrollPane(listValues), BorderLayout.CENTER);
			
			panelInput.add(singleInputComponent, BorderLayout.NORTH);
			panelInput.add(panelInner, BorderLayout.CENTER);
			
			micbReceiver.addObserver(new Observer() {
				@Override
				public void update(Observable o, Object arg) {
					btnAdd.setEnabled(micbReceiver.hasValue());
				}
			});
			listValues.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					btnRemove.setEnabled(!listValues.isSelectionEmpty());
				}
			});
			listModel.addListDataListener(new ListDataListener() {
				protected void updateOption() {
					CommandDialog.this.updateMultipleValues(option, listModel, receiver);
					
					btnClear.setEnabled(!listModel.isEmpty());
					CommandDialog.this.updateButtonOk();
				}
				
				@Override
				public void intervalRemoved(ListDataEvent e) {
					this.updateOption();
				}
				
				@Override
				public void intervalAdded(ListDataEvent e) {
					this.updateOption();
				}
				
				@Override
				public void contentsChanged(ListDataEvent e) {
					this.updateOption();
				}
			});
			btnAdd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.addMultipleValue(
						option, listModel, micbReceiver.getValue()
					);
				}
			});
			btnRemove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.removeMultipleValue(
						option, listModel, listValues.getSelectedValuesList().toArray()
					);
				}
			});
			btnClear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandDialog.this.clearMultipleValue(option, listModel, receiver);
				}
			});
			
			return panelInput;
		} else {
			InputComponentBuilder builder = CommandDialog.DEFAULT_INPUT_COMPONENT_BUILDER;
			for (InputComponentBuilder icb : CommandDialog.SERVICE_LOADER) {
				if (icb.canHandle(option)) {
					builder = icb;
					break;
				}
			}
			
			this.setSingleDefaultValueOption(option, receiver);
			
			return builder.createFor(this, option, receiver);
		}
	}
	
	protected <T> Component createComponentForOption(
		final Option<T> option, final ParameterValuesReceiver receiver
	) {
		final Component component = customComponentCreation(option, receiver);
		
		if (component != null) {
			return component;
		} else {
			return createComponentForOptionNoReflection(option, receiver);
		}
	}

	protected String createOptionName(Option<?> option) {
		return option.getParamName();
	}
	
	protected String createOptionDescription(Option<?> option) {
		return option.getDescription();
	}
	
	protected void addMultipleValue(Option<?> option, DefaultListModel<Object> listModel, String value) {
		listModel.addElement(value);
	}

	protected void removeMultipleValue(Option<?> option, DefaultListModel<Object> listModel, Object[] values) {
		for (Object value : values) {
			listModel.removeElement(value);
		}
	}
	
	protected void clearMultipleValue(Option<?> option, DefaultListModel<Object> listModel, ParameterValuesReceiver receiver) {
		listModel.clear();

		receiver.setValue(option, (List<String>) null);
	}
	
	protected void updateMultipleValues(Option<?> option, DefaultListModel<Object> listModel, ParameterValuesReceiver receiver) {
		receiver.setValue(option, CommandDialog.listModelToList(listModel));
	}
	
	protected <T> void setMultipleDefaultParameters(
		final Option<T> option,
		final ParameterValuesReceiver receiver,
		final DefaultListModel<Object> listModel
	) {
		if (this.defaultParameters != null && this.defaultParameters.hasOption(option)) {
			final List<String> values = this.defaultParameters.getAllValuesString(option);
			
			receiver.setValue(option, values);
			for (String value : values) {
				listModel.addElement(value);
			}
		}
	}

	protected <T> void setSingleDefaultValueOption(
		final Option<T> option,
		final ParameterValuesReceiver receiver
	) {
		if (this.defaultParameters != null && this.defaultParameters.hasOption(option)) {
			receiver.setValue(option, this.defaultParameters.getSingleValueString(option));
		} else if (option instanceof DefaultValuedOption<?>) {
			receiver.setValue(option, ((DefaultValuedOption<?>) option).getDefaultValue());
		}
	}

	protected void updateButtonOk() {
		this.btnOk.setEnabled(this.parameterValues.isComplete());
	}
}
