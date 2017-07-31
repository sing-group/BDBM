/*-
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;

import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.bdbm.gui.command.input.BuildComponent;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class ComponentFactory {
	public interface ValueCallback<T> {
		public void callback(T value);
	}
	
	private static class EmptyValueCallback<T> implements ValueCallback<T> {
		@Override
		public void callback(T value) {}
	}
	
	public interface ValuesProvider<T> {
		public T[] listValues(SequenceType type);
	}
	
	public static class DatabaseValuesProvider implements ValuesProvider<Database> {
		private final BDBMController controller;

		public DatabaseValuesProvider(BDBMController controller) {
			this.controller = controller;
		}
		
		@Override
		public Database[] listValues(SequenceType type) {
			if (type == SequenceType.NUCLEOTIDE) {
				return controller.listNucleotideDatabases();
			} else if (type == SequenceType.PROTEIN) {
				return controller.listProteinDatabases();
			} else {
				throw new IllegalArgumentException("Unknown option: " + type);
			}
		}
	}
	
	public static class FastaValuesProvider implements ValuesProvider<Fasta> {
		private final BDBMController controller;

		public FastaValuesProvider(BDBMController controller) {
			this.controller = controller;
		}
		
		@Override
		public Fasta[] listValues(SequenceType type) {
			if (type == SequenceType.NUCLEOTIDE) {
				return controller.listNucleotideFastas();
			} else if (type == SequenceType.PROTEIN) {
				return controller.listProteinFastas();
			} else {
				throw new IllegalArgumentException("Unknown option: " + type);
			}
		}
	}
	
	public static <T> JComboBox<SequenceType> createComponentForSequenceType(
		final Component parent,
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver,
		final JComboBox<T> cmbValues,
		final ValuesProvider<T> valuesProvider,
		final String defaultValue
	) {
		return createComponentForSequenceType(
			parent, option, receiver, cmbValues, valuesProvider, defaultValue,
			new EmptyValueCallback<T[]>()
		);
	}
	
	public static <T> JComboBox<SequenceType> createComponentForSequenceType(
		final Component parent,
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver,
		final JList<T> listValues,
		final ValuesProvider<T> valuesProvider,
		final String defaultValue
	) {
		return createComponentForSequenceType(
			parent, option, receiver, listValues,
			valuesProvider, defaultValue,
			new EmptyValueCallback<T[]>()
		);
	}
	
	private static abstract class CustomParameterValuesReceiverWrapper<T> extends ParameterValuesReceiverWrapper {
		private final Option<SequenceType> option; 
		private final ValuesProvider<T> valuesProvider;
		private final ValueCallback<T[]> callback;
		
		public CustomParameterValuesReceiverWrapper(
			Option<SequenceType> option,
			ParameterValuesReceiver receiver,
			ValuesProvider<T> valuesProvider,
			ValueCallback<T[]> callback
		) {
			super(receiver);
			this.option = option;
			this.valuesProvider = valuesProvider;
			this.callback = callback;
		}
		
		@Override
		public void setValue(Option<?> valueOption, String value) {
			super.setValue(option, value);
			
			if (value == null) {
				updateModel(null);
				callback.callback(null);
			} else {
				final SequenceType convertedValue = 
					option.getConverter().convert(new SingleParameterValue(value));
				
				final T[] values = valuesProvider.listValues(convertedValue);
				
				updateModel(values);
				
				callback.callback(values);
			}
		}
		
		protected abstract void updateModel(T[] values);
	}
	
	public static <T> JComboBox<SequenceType> createComponentForSequenceType(
		final Component parent,
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver,
		final JList<T> listValues,
		final ValuesProvider<T> valuesProvider,
		final String defaultValue,
		final ValueCallback<T[]> callback
	) {
		final ParameterValuesReceiver pvr = new CustomParameterValuesReceiverWrapper<T>(
			option, receiver, valuesProvider, callback
		) {
			@Override
			protected void updateModel(T[] values) {
				final DefaultListModel<T> model = new DefaultListModel<T>();
				
				if (values != null && values.length > 0) {
					for (T value : values) {
						model.addElement(value);
					}
				}
				
				listValues.setModel(model);
			}
		};
		
		pvr.setValue(option, defaultValue);
		
		return BuildComponent.forEnum(parent, option, pvr);
	}
	
	public static <T> JComboBox<SequenceType> createComponentForSequenceType(
		final Component parent,
		final Option<SequenceType> option, 
		final ParameterValuesReceiver receiver,
		final JComboBox<T> cmbValues,
		final ValuesProvider<T> valuesProvider,
		final String defaultValue,
		final ValueCallback<T[]> callback
	) {
		final ParameterValuesReceiver pvr = new CustomParameterValuesReceiverWrapper<T>(
			option, receiver, valuesProvider, callback
		) {
			@Override
			protected void updateModel(T[] values) {
				if (values == null || values.length == 0) {
					cmbValues.setModel(new DefaultComboBoxModel<T>());
					cmbValues.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Model Changed"));
				} else {
					cmbValues.setModel(new DefaultComboBoxModel<T>(values));
					cmbValues.setSelectedIndex(0);
				}
			}
		};
		
		pvr.setValue(option, defaultValue);
		
		return BuildComponent.forEnum(parent, option, pvr);
	}
	
	public static <T extends SequenceEntity> JComboBox<T> createComponentForSequenceEntityValues(
		final Option<?> option, 
		final ParameterValuesReceiver receiver,
		final JComboBox<T> cmbValues,
		final String defaultValue
	) {
		return createComponentForSequenceEntityValues(
			option, receiver, cmbValues,
			defaultValue,
			new EmptyValueCallback<T>()
		);
	}
	
	public static <T extends SequenceEntity> JComboBox<T> createComponentForSequenceEntityValues(
		final Option<?> option, 
		final ParameterValuesReceiver receiver,
		final JComboBox<T> cmbValues,
		final String defaultValue,
		final ValueCallback<T> callback
	) {
		return createComponentForSequenceEntityValues(option, cmbValues, defaultValue, 
			new ValueCallback<T>() {
				@Override
				public void callback(T value) {
					if (value == null) {
						receiver.setValue(option, (String) null);
					} else {
						receiver.setValue(option, value.getBaseFile().getAbsolutePath());
					}
					
					callback.callback(value);
				}
			}
		);
	}
	
	public static <T extends SequenceEntity> JComboBox<T> createComponentForSequenceEntityValues(
		final Option<?> option, 
		final JComboBox<T> cmbValues,
		final String defaultValue,
		final ValueCallback<T> callback
	) {
		final ActionListener alValues = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final T value = cmbValues.getItemAt(cmbValues.getSelectedIndex());
				
				callback.callback(value);
			}
		};
		
		if (defaultValue != null) {
			final int size = cmbValues.getItemCount();

			for (int i = 0; i < size; i++) {
				final T value = cmbValues.getItemAt(i);
				
				if (value.getBaseFile().getAbsolutePath().equals(defaultValue)) {
					cmbValues.setSelectedIndex(i);
					break;
				}
			}
		}
		
		alValues.actionPerformed(null);
		cmbValues.addActionListener(alValues);
		
		return cmbValues;
	}
}
