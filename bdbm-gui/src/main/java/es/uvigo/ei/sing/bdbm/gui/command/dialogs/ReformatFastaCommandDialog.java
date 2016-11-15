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

import static es.uvigo.ei.sing.bdbm.fasta.FastaUtils.prefixFastaSequenceNameRename;
import static es.uvigo.ei.sing.bdbm.fasta.naming.SequenceNameSummarizerFactory.createStandardNameSummarizer;
import static org.apache.commons.lang3.StringUtils.abbreviate;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import es.uvigo.ei.sing.bdbm.cli.commands.ReformatFastaCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.EnumOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.IntegerOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.fasta.FastaUtils;
import es.uvigo.ei.sing.bdbm.fasta.naming.FastaSequenceRenameMode;
import es.uvigo.ei.sing.bdbm.fasta.naming.GenericComposedSequenceNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.ComposedSequenceRenameConfiguration;
import es.uvigo.ei.sing.bdbm.fasta.naming.configuration.PrefixSequenceRenameConfiguration;
import es.uvigo.ei.sing.bdbm.fasta.naming.standard.StandardSequenceNameSummarizer;
import es.uvigo.ei.sing.bdbm.fasta.naming.standard.StandardSequenceNameSummarizer.NameField;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.ComponentForOption;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.FastaValuesProvider;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ComponentFactory.ValueCallback;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringConstructedOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ReformatFastaCommandDialog extends CommandDialog {
	private static final int MAXIMUM_SEQUENCE_LENGTH = 80;

	private static final long serialVersionUID = 1L;

	private JComboBox<Fasta> cmbFastas;
	
	private JCheckBox cmpKeepNames;
	private JCheckBox cmpAddIndex;
	private JTextField cmpPrefix;
	private JTextField cmpDelimiterString;
	private JTextField cmpJoinerString;
	private JCheckBox cmpKeepDescription;
	
	private JTextField cmpIndexes;
	
	private JLabel lblFormatDetected;
	private JLabel lblFirstSequence;
	private JLabel lblReformatedSequence;
	private JPanel panelParams;
	private GridLayout panelParamsLayout;

	private JComboBox<FastaSequenceRenameMode> cmbMode;
	
	private SortedSet<NameField> selectedFields;



	public ReformatFastaCommandDialog(
		BDBMController controller, 
		ReformatFastaCommand command
	) {
		this(controller, command, null);
	}
	
	public ReformatFastaCommandDialog(
		BDBMController controller, 
		ReformatFastaCommand command,
		Parameters defaultParameters
	) {
		super(controller, command, defaultParameters);

		this.selectedFields = new TreeSet<>(new Comparator<NameField>() {
			@Override
			public int compare(NameField o1, NameField o2) {
				return Integer.compare(o1.getIndex(), o2.getIndex());
			}
		});
		
		this.asynchronousPack();
	}
	
	@Override
	protected void preComponentsCreation(PanelOptionsBuilder panelOptionBuilder) {
		this.cmbFastas = new JComboBox<>();
		this.lblFormatDetected = new JLabel();
		this.lblFirstSequence = new JLabel();
		this.lblReformatedSequence = new JLabel();
		this.panelParamsLayout = new GridLayout(1, 1);
		this.panelParams = new JPanel(panelParamsLayout);
		
		this.lblFormatDetected.setVisible(false);
		this.lblFirstSequence.setVisible(false);
		this.lblReformatedSequence.setVisible(false);
		this.panelParams.setVisible(false);
	}
	
	@Override
	protected void postComponentsCreation(PanelOptionsBuilder panelOptionsBuilder) {
		panelOptionsBuilder.addOptionRow(
			"Name Parts",
			"Recognized parts in the FASTA sequence name. You can select which parts you want to include in the reformatted name, "
			+ "however, at least one part must be selected.",
			this.panelParams
		);
		panelOptionsBuilder.addOptionRow(
			"Sequence Name Format",
			"Parses the first sequence name and tries to identify its format.",
			this.lblFormatDetected
		);
		panelOptionsBuilder.addOptionRow(
			"First Sequence Name",
			"First sequence of the selected FASTA file.",
			this.lblFirstSequence
		);
		panelOptionsBuilder.addOptionRow(
			"Name Reformat Preview",
			"Preview of the result of reformatting the first sequence name.",
			this.lblReformatedSequence
		);
	}
	
	@Override
	protected <T> void postComponentCreation(
		final Component component,
		final Option<T> option,
		final ParameterValuesReceiver receiver,
		final PanelOptionsBuilder panelOptionsBuilder
	) {
		if (option.equals(ReformatFastaCommand.OPTION_DELIMITER_STRING)) {
			this.cmpDelimiterString = (JTextField) component;
			component.setVisible(false);
			
			this.cmpDelimiterString.getDocument().addDocumentListener(newUpdateSequenceNameInformationDocumentListener());
		} else if (option.equals(ReformatFastaCommand.OPTION_JOINER_STRING)) {
			this.cmpJoinerString = (JTextField) component;
			component.setVisible(false);
			
			this.cmpJoinerString.getDocument().addDocumentListener(newUpdateSequenceNameInformationDocumentListener());
		} else if (option.equals(ReformatFastaCommand.OPTION_PREFIX)) {
			this.cmpPrefix = (JTextField) component;
			component.setVisible(false);
			
			this.cmpPrefix.getDocument().addDocumentListener(newUpdateSequenceNameInformationDocumentListener());
		} else if (option.equals(ReformatFastaCommand.OPTION_KEEP_NAMES_WHEN_PREFIX)) {
			this.cmpKeepNames = (JCheckBox) component;
			component.setVisible(false);
			
			this.cmpKeepNames.addChangeListener(newUpdateSequenceNameInformationChangeListener());
		} else if (option.equals(ReformatFastaCommand.OPTION_ADD_INDEX_WHEN_PREFIX)) {
			this.cmpAddIndex = (JCheckBox) component;
			component.setVisible(false);
			
			this.cmpAddIndex.addChangeListener(newUpdateSequenceNameInformationChangeListener());
		} else if (option.equals(ReformatFastaCommand.OPTION_INDEXES)) {
			component.setVisible(false);
		} else if (option.equals(ReformatFastaCommand.OPTION_KEEP_DESCRIPTION)) {
			this.cmpKeepDescription = (JCheckBox) component;
			this.cmpKeepDescription.setText("");
			component.setVisible(false);
			
			this.cmpKeepDescription.addChangeListener(newUpdateSequenceNameInformationChangeListener());
		}
	}
	
	@Override
	protected String createOptionDescription(Option<?> option) {
		if (option == ReformatFastaCommand.OPTION_INDEXES) {
			return "Indexes of the parts of the name that you want to keep. You can use an expression following this rules:"
				+ "\n\t· The index of the first part is 1."
				+ "\n\t· The indexes of the selected parts must be separated by a comma. For example: \"1,2,5\"."
				+ "\n\t· You can use ranges, separating the first and last numbers (both inclusive) with a hyphen (-). For example: \"1-3\", \"4-6\", etc."
				+ "\n\t· Leave this field blank if you want to select all the parts of the name.";
		} else {
			return super.createOptionDescription(option);
		}
	}

	@ComponentForOption(
		value = ReformatFastaCommand.OPTION_INDEXES_SHORT_NAME,
		allowsMultiple = true
	)
	protected Component createComponentForIndexes(
		final StringConstructedOption<Integer> option,
		final ParameterValuesReceiver receiver
	) {
		this.cmpIndexes = new JTextField();
		final Color background = cmpIndexes.getBackground();
		
		this.cmpIndexes.getDocument().addDocumentListener(new DocumentListener() {
			private void update() {
				try {
					cmpIndexes.setBackground(background);
					
					
					final List<String> indexes = indexesList();
					
					if (indexes == null)
						receiver.removeValue(option);
					else
						receiver.setValue(option, indexes);
				} catch(IllegalStateException iae) {
					cmpIndexes.setBackground(Color.RED);
					receiver.removeValue(option);
				} finally {
					updateSequenceNameInformation();
				}
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				this.update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				this.update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				this.update();
			}
		});
		
		return cmpIndexes;
	}
	
	@ComponentForOption(ReformatFastaCommand.OPTION_FASTA_TYPE_SHORT_NAME)
	protected Component createComponentForFastaTypeOption(
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceType(
			this, option, receiver, this.cmbFastas,
			new FastaValuesProvider(this.controller),
			this.getDefaultOptionString(option),
			this.<Fasta[]>newUpdateSequenceNameInformationValueCallback()
		);
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_FASTA_SHORT_NAME)
	protected JComboBox<Fasta> createComponentForFastaOption(
		final FileOption option, 
		final ParameterValuesReceiver receiver
	) {
		return ComponentFactory.createComponentForSequenceEntityValues(
			option, receiver, this.cmbFastas,
			this.getDefaultOptionString(option),
			this.<Fasta>newUpdateSequenceNameInformationValueCallback()
		);
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_FRAGMENT_LENGTH_SHORT_NAME)
	protected Component createComponentForFragmentLengthOption(
		final IntegerOption option, 
		final ParameterValuesReceiver receiver
	) {
		final JCheckBox chkChangeLength = new JCheckBox("Change sequence length?", false);
		final JTextField component = (JTextField) BuildComponent.forOption(this, option, receiver);
		component.setEnabled(false);
		component.setText("");
		
		chkChangeLength.addActionListener(new ActionListener() {
			private String lastValue = "0";
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkChangeLength.isSelected()) {
					component.setText(this.lastValue);
					component.setEnabled(true);
				} else {
					component.setEnabled(false);
					lastValue = receiver.getValue(option);
					component.setText("");
				}
			}
		});
		
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(chkChangeLength);
		panel.add(component);
		
		return panel;
	}

	@ComponentForOption(ReformatFastaCommand.OPTION_RENAMING_MODE_SHORT_NAME)
	protected Component createComponentForFragmentLengthOption(
		final EnumOption<FastaSequenceRenameMode> option, 
		final ParameterValuesReceiver receiver
	) {
		this.cmbMode = BuildComponent.forEnum(this, option, receiver);
		
		this.cmbMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableComponents();
				
				clearConfiguration();
				
				updateSequenceNameInformation();
				
				asynchronousPack();
			}
		});
		
		return this.cmbMode;
	}
	
	private int[] indexes() {
		switch (this.getSelectedRenameMode()) {
		case MULTIPART_NAME: {
			try {
				final List<String> indexesList = indexesList();
				
				if (indexesList == null) {
					return null;
				} else {
					final int[] indexes = new int[indexesList.size()];
					
					int i = 0;
					for (String index : indexesList) {
						indexes[i++] = Integer.parseInt(index);
					}
					
					return indexes;
				}
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				return null;
			}
		}
		case KNOWN_SEQUENCE_NAMES: {
			final int[] indexes = new int[this.selectedFields.size()];
			
			int i = 0;
			for (NameField field : this.selectedFields) {
				indexes[i++] = field.getIndex() + 1;
			}
			
			return indexes;
		}
		default:
			throw new IllegalStateException("Invalid rename mode selected: " + this.getSelectedRenameMode());
		}
	}

	private List<String> indexesList() throws IllegalStateException {
		final String text = cmpIndexes.getText().trim();
		
		if (text.isEmpty())
			return null;
		
		final String regex = "[1-9][0-9]*(-[1-9][0-9]*)?(,([1-9][0-9]*(-[1-9][0-9]*)?))*";

		if (text.matches(regex)) {
			final String[] parts = text.split(",");
			
			final SortedSet<String> indexes = new TreeSet<>();
			for (String part : parts) {
				if (part.contains("-")) {
					final String[] minMax = part.split("-");
					final int min = Integer.parseInt(minMax[0]);
					final int max = Integer.parseInt(minMax[1]);
					
					if (min >= max) {
						throw new IllegalArgumentException("Invalid range: " + part);
					} else {
						for (int i = min; i <= max; i++) {
							indexes.add(Integer.toString(i - 1));
						}
					}
				} else {
					final int index = Integer.parseInt(part) - 1;
					indexes.add(Integer.toString(index));
				}
			}
			
			return indexes.isEmpty() ? null : new ArrayList<>(indexes);
		} else {
			throw new IllegalStateException("Invalid format: " + text);
		}
	}
	
	private FastaSequenceRenameMode getSelectedRenameMode() {
		if (this.cmbMode != null)
			return (FastaSequenceRenameMode) this.cmbMode.getSelectedItem();
		else
			return null;
	}
	
	private void enableComponents() {
		final FastaSequenceRenameMode selectedMode = getSelectedRenameMode();
		
		if (cmpKeepDescription != null)
			cmpKeepDescription.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		if (lblFirstSequence != null)
			lblFirstSequence.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		if (lblReformatedSequence != null)
			lblReformatedSequence.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		
		if (cmpPrefix != null)
			cmpPrefix.setVisible(selectedMode == FastaSequenceRenameMode.PREFIX);
		if (cmpKeepNames != null)
			cmpKeepNames.setVisible(selectedMode == FastaSequenceRenameMode.PREFIX);
		if (cmpAddIndex != null)
			cmpAddIndex.setVisible(selectedMode == FastaSequenceRenameMode.PREFIX);
		
		if (cmpIndexes != null)
			cmpIndexes.setVisible(selectedMode == FastaSequenceRenameMode.MULTIPART_NAME);
		if (cmpDelimiterString != null)
			cmpDelimiterString.setVisible(selectedMode == FastaSequenceRenameMode.MULTIPART_NAME);
		
		if (lblFormatDetected != null)
			lblFormatDetected.setVisible(selectedMode == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES);
		if (panelParams != null)
			panelParams.setVisible(selectedMode == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES);
		
		if (cmpJoinerString != null)
			cmpJoinerString.setVisible(selectedMode == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES || selectedMode == FastaSequenceRenameMode.MULTIPART_NAME);
	}
	
	@Override
	protected void updateButtonOk() {
		final FastaSequenceRenameMode renameMode = getSelectedRenameMode();
		
		if (renameMode != null) {
			switch (renameMode) {
			case KNOWN_SEQUENCE_NAMES: {
				final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
				
				this.btnOk.setEnabled(configuration.isValid() && getSummarizers() != null);
				break;
			}
			case PREFIX: {
				final PrefixSequenceRenameConfiguration configuration = this.createPrefixRenamingConfiguration();
				
				this.btnOk.setEnabled(configuration.isValid());
				break;
			}
			case MULTIPART_NAME: {
				final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
				
				this.btnOk.setEnabled(configuration.isValid());
				break;
			}
			default:
			}
		} else {
			super.updateButtonOk();
		}
	}
	
	protected StandardSequenceNameSummarizer getSummarizers() {
		try {
			final String firstSequence = getFirstSequence();
			
			if (firstSequence == null)
				return null;
			else
				return createStandardNameSummarizer(firstSequence, createNameSummaryConfiguration());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getFirstSequence() {
		try {
			final Fasta fasta = (Fasta) this.cmbFastas.getSelectedItem();
			
			if (fasta == null)
				return null;
			else
				return FastaUtils.getFirstSequence(fasta.getFile());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void clearConfiguration() {
		if (this.cmpJoinerString != null)
			this.cmpJoinerString.setText(ReformatFastaCommand.OPTION_JOINER_STRING.getDefaultValue());
		if (this.cmpDelimiterString != null)
			this.cmpDelimiterString.setText(ReformatFastaCommand.OPTION_DELIMITER_STRING.getDefaultValue());
		if (this.cmpKeepDescription != null)
			this.cmpKeepDescription.setSelected(Boolean.parseBoolean(ReformatFastaCommand.OPTION_KEEP_DESCRIPTION.getDefaultValue()));
		if (this.cmpIndexes != null)
			this.cmpIndexes.setText("");
		
		if (this.cmpAddIndex != null)
			this.cmpAddIndex.setSelected(Boolean.parseBoolean(ReformatFastaCommand.OPTION_ADD_INDEX_WHEN_PREFIX.getDefaultValue()));
		if (this.cmpKeepNames != null)
			this.cmpKeepNames.setSelected(Boolean.parseBoolean(ReformatFastaCommand.OPTION_KEEP_NAMES_WHEN_PREFIX.getDefaultValue()));
		if (this.cmpPrefix != null)
			this.cmpPrefix.setText("seq");
		
		this.clearPanelParams();
	}
	
	private ComposedSequenceRenameConfiguration createNameSummaryConfiguration() {
		final ComposedSequenceRenameConfiguration configuration = new ComposedSequenceRenameConfiguration();

		if (this.cmpIndexes != null)
			configuration.setSelectedIndexes(this.indexes());
		if (this.cmpJoinerString != null)
			configuration.setJoinerString(this.cmpJoinerString.getText());
		if (this.cmpKeepDescription != null)
			configuration.setKeepDescription(this.cmpKeepDescription.isSelected());
		if (this.cmpDelimiterString != null && this.getSelectedRenameMode() == FastaSequenceRenameMode.MULTIPART_NAME)
			configuration.setDelimiterString(this.cmpDelimiterString.getText());
		
		return configuration;
	}
	
	private PrefixSequenceRenameConfiguration createPrefixRenamingConfiguration() {
		final PrefixSequenceRenameConfiguration configuration = new PrefixSequenceRenameConfiguration();
		
		if (this.cmpJoinerString != null)
			configuration.setJoinerString(this.cmpJoinerString.getText());
		if (this.cmpPrefix != null)
			configuration.setPrefix(this.cmpPrefix.getText());
		if (this.cmpAddIndex != null)
			configuration.setAddIndex(this.cmpAddIndex.isSelected());
		if (this.cmpKeepDescription != null)
			configuration.setKeepDescription(this.cmpKeepDescription.isSelected());
		if (this.cmpKeepNames != null)
			configuration.setKeepNames(this.cmpKeepNames.isSelected());
		
		return configuration;
	}
	
	protected <T> ValueCallback<T> newUpdateSequenceNameInformationValueCallback() {
		return new ValueCallback<T>() {
			@Override
			public void callback(T value) {
				clearConfiguration();
				updateSequenceNameInformation();
			}
		};
	}
	
	protected DocumentListener newUpdateSequenceNameInformationDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSequenceNameInformation();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSequenceNameInformation();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateSequenceNameInformation();
			}
		};
	}

	private ChangeListener newUpdateSequenceNameInformationChangeListener() {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateSequenceNameInformation();
			}
		};
	}
	
	private void clearPanelParams() {
		if (this.panelParams != null && this.selectedFields != null) {
			this.panelParams.removeAll();
			this.selectedFields.clear();
	
			if (this.getSelectedRenameMode() == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES) {
				final StandardSequenceNameSummarizer summarizer = getSummarizers();
				if (summarizer != null) {
					final String firstSequence = this.getFirstSequence();
					final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
					
					final List<? extends NameField> fields = summarizer.identifyNameFields(firstSequence, configuration);
					
					this.panelParamsLayout.setRows(fields.size() + 1);
					
					addParamPanelField(new NameField() {
						@Override
						public int getIndex() {
							return -1;
						}
						
						@Override
						public String getName() {
							return "Prefix (" + summarizer.getPrefix() + ")";
						}
						
						public boolean isOptional() {
							return false;
						};
					});
					
					for (final NameField field : fields) {
						addParamPanelField(field);
					}
				}
			}
		}
	}

	private void addParamPanelField(final NameField field) {
		final JCheckBox chkField = new JCheckBox(field.getName(), true);
		
		this.panelParams.add(chkField);
		this.selectedFields.add(field);
		
		chkField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (chkField.isSelected()) {
					selectedFields.add(field);
				} else {
					selectedFields.remove(field);
				}
				
				updateSequenceNameInformation();
			}
		});
	}
	
	protected void updateSequenceNameInformation() {
		final String firstSequence = this.getFirstSequence();
		final String abbreviatedFirstSequence = abbreviate(firstSequence, MAXIMUM_SEQUENCE_LENGTH);
		
		this.lblFormatDetected.setForeground(Color.BLACK);
		if (firstSequence == null) {
			this.lblFirstSequence.setText("");
			this.lblFirstSequence.setToolTipText(null);
			
			this.setFormatDetectedError("No FASTA selected");
			this.clearReformatedSequence();
		} else {
			final FastaSequenceRenameMode renameMode = getSelectedRenameMode();
			
			if (renameMode == null) {
				this.setFormatDetectedError("Unrecognized sequence name format");
				this.clearReformatedSequence();
			} else {					
				this.lblFirstSequence.setText(abbreviatedFirstSequence);
				this.lblFirstSequence.setToolTipText(firstSequence);
				
				switch (renameMode) {
				case KNOWN_SEQUENCE_NAMES: {
					final StandardSequenceNameSummarizer summarizer = getSummarizers();
					
					if (summarizer == null) {
						this.setFormatDetectedError("Unrecognized sequence name format");
						this.clearReformatedSequence();
					} else {
						final String summary = summarizer.summarize(firstSequence, this.createNameSummaryConfiguration());
						
						this.setFormatDetectedText(summarizer.getDescription());
						this.setReformatedSequenceText(summary);
					}
					break;
				}
				case MULTIPART_NAME: {
					try {
						final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
						
						if (configuration.isValid()) {
							final GenericComposedSequenceNameSummarizer summarizer = new GenericComposedSequenceNameSummarizer();
							final String summary = summarizer.summarize(firstSequence, configuration);
							
							this.setReformatedSequenceText(summary);
						} else {
							this.setReformatedSequenceError("Invalid configuration");
						}
					} catch (IllegalArgumentException iae) {
						this.setReformatedSequenceError(iae.getMessage());
					}
					
					break;
				}
				case PREFIX: {
					final PrefixSequenceRenameConfiguration configuration = this.createPrefixRenamingConfiguration();
					
					if (configuration.isValid()) {
						final String summary = prefixFastaSequenceNameRename(firstSequence, configuration);
						
						this.setReformatedSequenceText(summary);
					} else {
						this.setReformatedSequenceError("Invalid configuration. You must add an index or keep the sequence name.");
					}
					
					break;
				}
				default:
				}
			}
		}
		
		this.asynchronousPack();
	}
	
	private void clearReformatedSequence() {
		this.setReformatedSequenceText(null);
	}
	
	private void setReformatedSequenceText(String text) {
		this.lblReformatedSequence.setText(text == null ? "" : abbreviate(text, MAXIMUM_SEQUENCE_LENGTH));
		this.lblReformatedSequence.setToolTipText(text);
		
		configureNormalLabel(this.lblReformatedSequence);
	}
	
	private void setReformatedSequenceError(String error) {
		this.lblReformatedSequence.setText(error);
		this.lblReformatedSequence.setToolTipText(null);
		
		configureErrorLabel(this.lblReformatedSequence);
	}
	
	private void setFormatDetectedText(String text) {
		this.lblFormatDetected.setText(text);
		
		configureNormalLabel(this.lblFormatDetected);
	}
	
	private void setFormatDetectedError(String error) {
		this.lblFormatDetected.setText(error);
		
		configureErrorLabel(this.lblFormatDetected);
	}
	
	private static void configureNormalLabel(JLabel label) {
		label.setForeground(Color.BLACK);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
	}
	
	private static void configureErrorLabel(JLabel label) {
		label.setForeground(Color.RED);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
	}
}
