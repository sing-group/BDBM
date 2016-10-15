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
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSortedSet;
import static org.apache.commons.lang3.StringUtils.abbreviate;

import java.awt.Color;
import java.awt.Component;
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
import es.uvigo.ei.sing.bdbm.gui.component.IndexesJTextField;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.option.StringConstructedOption;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;

public class ReformatFastaCommandDialog extends CommandDialog {
	private static final long serialVersionUID = 1L;
	
	private static final int MAXIMUM_SEQUENCE_LENGTH = 60;

	private JComboBox<Fasta> cmbFastas;
	
	private JCheckBox cmpKeepNames;
	private JCheckBox cmpAddIndex;
	private JTextField cmpPrefix;
	private JTextField cmpDelimiterString;
	private JTextField cmpJoinerString;
	private JCheckBox cmpKeepDescription;
	
	private IndexesJTextField cmpIndexes;
	private ParameterValuesReceiver pvrIndexes;
	
	private JLabel lblFormatDetected;
	private JLabel lblFirstSequence;
	private JLabel lblReformatedSequence;
	private ParamsPanel paramsPanel;

	private JComboBox<FastaSequenceRenameMode> cmbMode;

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
		
		this.asynchronousPack();
	}
	
	@Override
	protected void preComponentsCreation(PanelOptionsBuilder panelOptionBuilder) {
		this.cmbFastas = new JComboBox<>();
		this.lblFormatDetected = new JLabel();
		this.lblFirstSequence = new JLabel();
		this.lblReformatedSequence = new JLabel();
		this.paramsPanel = new ParamsPanel();
		
		this.lblFormatDetected.setVisible(false);
		this.lblFirstSequence.setVisible(false);
		this.lblReformatedSequence.setVisible(false);
		this.paramsPanel.setVisible(false);
	}
	
	@Override
	protected void postComponentsCreation(PanelOptionsBuilder panelOptionsBuilder) {
		panelOptionsBuilder.addOptionRow(
			"Name Parts",
			"Recognized parts in the FASTA sequence name. You can select which parts you want to include in the reformatted name, "
			+ "however, at least one part must be selected.",
			this.paramsPanel
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
		
		this.paramsPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateSelectedIndexesParameterValue();
				updateSequenceNameInformation();
			}
		});
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
		this.cmpIndexes = new IndexesJTextField();
		this.pvrIndexes = receiver;
		
		this.cmpIndexes.getDocument().addDocumentListener(newUpdateSequenceNameInformationDocumentListener());
		
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
				reenableComponents();
				
				clearConfigurationFields();

				updateSelectedIndexesParameterValue();
				updateSequenceNameInformation();
				
				asynchronousPack();
			}
		});
		
		return this.cmbMode;
	}
	
	@Override
	protected void updateButtonOk() {
		System.out.println(this.parameterValues.getValues());
		if (this.cmbMode != null && this.cmbMode.getSelectedItem() == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES) {
			btnOk.setEnabled(getSummarizer() != null && this.parameterValues.isComplete());
		} else {
			super.updateButtonOk();
		}
	}
	
	private DocumentListener newUpdateSequenceNameInformationDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSelectedIndexesParameterValue();
				updateSequenceNameInformation();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSelectedIndexesParameterValue();
				updateSequenceNameInformation();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateSelectedIndexesParameterValue();
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
	
	protected <T> ValueCallback<T> newUpdateSequenceNameInformationValueCallback() {
		return new ValueCallback<T>() {
			@Override
			public void callback(T value) {
				clearConfigurationFields();
				updateSequenceNameInformation();
			}
		};
	}
	
	private void updateSelectedIndexesParameterValue() {
		switch(this.getSelectedRenameMode()) {
		case MULTIPART_NAME:
		case KNOWN_SEQUENCE_NAMES:
			final List<String> indexesList = getSelectedIndexesList();
			if (indexesList.isEmpty()) {
				this.pvrIndexes.removeValue(ReformatFastaCommand.OPTION_INDEXES);
			} else {
				this.pvrIndexes.setValue(ReformatFastaCommand.OPTION_INDEXES, indexesList);
			}
			super.updateButtonOk();
			break;
		default:
			this.pvrIndexes.removeValue(ReformatFastaCommand.OPTION_INDEXES);
		}
	}
	
	private List<String> getSelectedIndexesList() {
		try {
			switch (this.getSelectedRenameMode()) {
			case MULTIPART_NAME:
				return this.cmpIndexes.getIndexesList();
			case KNOWN_SEQUENCE_NAMES:
				return this.paramsPanel.getSelectedIndexesList(1);
			default:
				throw new IllegalStateException("Invalid rename mode selected: " + this.getSelectedRenameMode());
			}
		} catch (IllegalArgumentException iae) {
			return emptyList();
		}
	}
	
	private int[] getSelectedIndexes() {
		switch (this.getSelectedRenameMode()) {
		case MULTIPART_NAME: {
			return this.cmpIndexes.getIndexes();
		}
		case KNOWN_SEQUENCE_NAMES: {
			return this.paramsPanel.getSelectedIndexes(1);
		}
		default:
			throw new IllegalStateException("Invalid rename mode selected: " + this.getSelectedRenameMode());
		}
	}
	
	private FastaSequenceRenameMode getSelectedRenameMode() {
		if (this.cmbMode != null)
			return (FastaSequenceRenameMode) this.cmbMode.getSelectedItem();
		else
			return null;
	}
	
	private StandardSequenceNameSummarizer getSummarizer() {
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
			throw new RuntimeException(e);
		}
	}
	
	private void reenableComponents() {
		final FastaSequenceRenameMode selectedMode = getSelectedRenameMode();
		
		if (cmpKeepDescription != null)
			cmpKeepDescription.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		if (lblFirstSequence != null)
			lblFirstSequence.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		if (lblReformatedSequence != null)
			lblReformatedSequence.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		if (cmpJoinerString != null)
			cmpJoinerString.setVisible(selectedMode != FastaSequenceRenameMode.NONE);
		
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
		if (paramsPanel != null)
			paramsPanel.setVisible(selectedMode == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES);
	}
	
	private void clearConfigurationFields() {
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
			configuration.setSelectedIndexes(this.getSelectedIndexes());
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
	
	private void clearPanelParams() {
		if (this.paramsPanel != null) {
			this.paramsPanel.clear();
			
			if (this.getSelectedRenameMode() == FastaSequenceRenameMode.KNOWN_SEQUENCE_NAMES) {
				final StandardSequenceNameSummarizer summarizer = getSummarizer();
				if (summarizer != null) {
					final String firstSequence = this.getFirstSequence();
					final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
					
					final List<? extends NameField> fields = summarizer.identifyNameFields(firstSequence, configuration);
					
					this.paramsPanel.addParamPanelField(new NameField() {
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
						this.paramsPanel.addParamPanelField(field);
					}
				}
			}
		}
	}
	
	protected void updateSequenceNameInformation() {
		final String firstSequence = this.getFirstSequence();
		final String abbreviatedFirstSequence = abbreviate(firstSequence, MAXIMUM_SEQUENCE_LENGTH);
		
		this.lblFormatDetected.setForeground(Color.BLACK);
		if (firstSequence == null) {
			this.lblFirstSequence.setText("");
			this.lblFirstSequence.setToolTipText(null);
			this.lblFormatDetected.setText("No FASTA selected");
			this.lblFormatDetected.setForeground(Color.RED);
			this.lblReformatedSequence.setText("");
			this.lblReformatedSequence.setToolTipText(null);
		} else {
			final FastaSequenceRenameMode renameMode = getSelectedRenameMode();
			
			if (renameMode == null) {
				this.lblFormatDetected.setText("Unrecognized sequence name format");
				this.lblFormatDetected.setForeground(Color.RED);
				this.lblFormatDetected.setText("");
				this.lblReformatedSequence.setText("");
				this.lblReformatedSequence.setToolTipText(null);
			} else {					
				this.lblFirstSequence.setText(abbreviatedFirstSequence);
				this.lblFirstSequence.setToolTipText(firstSequence);
				
				switch (renameMode) {
				case KNOWN_SEQUENCE_NAMES: {
					final StandardSequenceNameSummarizer summarizer = getSummarizer();
					
					if (summarizer == null) {
						this.lblFormatDetected.setText("Unrecognized sequence name format");
						this.lblFormatDetected.setForeground(Color.RED);
						this.lblReformatedSequence.setText("");
						this.lblReformatedSequence.setToolTipText(null);
					} else {
						final ComposedSequenceRenameConfiguration configuration = this.createNameSummaryConfiguration();
						final String summary = summarizer.summarize(firstSequence, configuration);
						final String abbreviatedSummary = abbreviate(summary, MAXIMUM_SEQUENCE_LENGTH);
						
						this.lblFormatDetected.setText(summarizer.getDescription());
						this.lblReformatedSequence.setText(abbreviatedSummary);
						this.lblReformatedSequence.setToolTipText(summary);
						this.lblReformatedSequence.setOpaque(false);
					}
					break;
				}
				case MULTIPART_NAME: {
					try {
						final GenericComposedSequenceNameSummarizer summarizer = new GenericComposedSequenceNameSummarizer();
						final String summary = summarizer.summarize(firstSequence, this.createNameSummaryConfiguration());
						final String abbreviatedSummary = abbreviate(summary, MAXIMUM_SEQUENCE_LENGTH);
						
						this.lblReformatedSequence.setText(abbreviatedSummary);
						this.lblReformatedSequence.setToolTipText(summary);
						this.lblReformatedSequence.setOpaque(false);
					} catch (IllegalArgumentException iae) {
						this.lblReformatedSequence.setText(iae.getMessage());
						this.lblReformatedSequence.setToolTipText(null);
						this.lblReformatedSequence.setOpaque(true);
						this.lblReformatedSequence.setBackground(Color.RED);
					}
					
					break;
				}
				case PREFIX: {
					final String summary = prefixFastaSequenceNameRename(firstSequence, this.createPrefixRenamingConfiguration());
					final String abbreviatedSummary = abbreviate(summary, MAXIMUM_SEQUENCE_LENGTH);
					
					this.lblReformatedSequence.setText(abbreviatedSummary);
					this.lblReformatedSequence.setToolTipText(summary);
					this.lblReformatedSequence.setOpaque(false);
					
					break;
				}
				default:
				}
			}
		}
	}
	
	protected static class ParamsPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private final GridLayout layout;
		private final SortedSet<NameField> selectedFields;
		
		public ParamsPanel() {
			super(new GridLayout(1, 1));
			
			this.layout = (GridLayout) this.getLayout();
			this.selectedFields = new TreeSet<>(new Comparator<NameField>() {
				@Override
				public int compare(NameField o1, NameField o2) {
					return Integer.compare(o1.getIndex(), o2.getIndex());
				}
			});
		}
		
		public void clear() {
			this.removeAll();
			this.selectedFields.clear();
			this.layout.setRows(1);
		}

		public void addParamPanelField(final NameField field) {
			final JCheckBox chkField = new JCheckBox(field.getName(), true);
			
			this.add(chkField);
			this.layout.setRows(this.layout.getRows() + 1);
			this.selectedFields.add(field);
			
			chkField.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (chkField.isSelected()) {
						selectedFields.add(field);
					} else {
						selectedFields.remove(field);
					}
					
					fireStateChanged();
				}
			});
		}
		
		public SortedSet<NameField> getSelectedFields() {
			return unmodifiableSortedSet(selectedFields);
		}

		public int[] getSelectedIndexes() {
			return this.getSelectedIndexes(0);
		}

		public int[] getSelectedIndexes(int offset) {
			final int[] indexes = new int[this.selectedFields.size()];
			
			int i = 0;
			for (NameField field : this.selectedFields) {
				indexes[i++] = field.getIndex() + offset;
			}
			
			return indexes;
		}
		
		public List<String> getSelectedIndexesList() {
			return getSelectedIndexesList(0);
		}
		
		public List<String> getSelectedIndexesList(int offset) {
			final int[] indexes = getSelectedIndexes(offset);
			final List<String> indexesList = new ArrayList<>(indexes.length);
			
			for (int index : indexes) {
				indexesList.add(Integer.toString(index));
			}
			
			return indexesList;
		}
		
	    public void addChangeListener(ChangeListener l) {
	        listenerList.add(ChangeListener.class, l);
	    }

	    public void removeChangeListener(ChangeListener l) {
	        listenerList.remove(ChangeListener.class, l);
	    }

	    public ChangeListener[] getChangeListeners() {
	        return listenerList.getListeners(ChangeListener.class);
	    }

	    protected void fireStateChanged() {
	    	final ChangeEvent event = new ChangeEvent(this);
	    	
	    	for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
	    		listener.stateChanged(event);
	    	}
	    }
	}
}
