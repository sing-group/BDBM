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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import es.uvigo.ei.sing.yaacli.command.option.DefaultValuedOption;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.MultipleParameterValue;
import es.uvigo.ei.sing.yaacli.command.parameter.ParameterValue;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class ParameterValues extends Observable implements ParameterValuesReceiver, Parameters {
		private final List<Option<?>> options;
		private final Map<Option<?>, ParameterValue<?>> values;
		
		public ParameterValues(List<Option<?>> options) {
			this.values = new HashMap<Option<?>, ParameterValue<?>>();
			this.options = Collections.unmodifiableList(
				new ArrayList<Option<?>>(options)
			);
			
			for (Option<?> option : options) {
				if (option instanceof DefaultValuedOption) {
					final DefaultValuedOption<?> dvOption = 
						(DefaultValuedOption<?>) option;
					
					if (option.isMultiple()) {
						this.values.put(option, new MultipleParameterValue(Arrays.asList(dvOption.getDefaultValue())));
					} else {
						this.values.put(option, new SingleParameterValue(dvOption.getDefaultValue()));
					}
				}
			}
		}

		@Override
		public <T> T getSingleValue(Option<T> option) {
			return option.getConverter().convert(
				(SingleParameterValue) this.values.get(option)
			);
		}

		@Override
		public <T> List<T> getAllValues(Option<T> option) {
			return option.getConverter().convert(
				(MultipleParameterValue) this.values.get(option)
			);
		}
		
		@Override
		public List<String> getAllValuesString(Option<?> option) {
			return ((MultipleParameterValue) this.values.get(option)).getValue();
		}
		
		@Override
		public String getSingleValueString(Option<?> option) {
			return ((SingleParameterValue) this.values.get(option)).getValue();
		}

		@Override
		public boolean hasFlag(Option<?> option) {
			return this.values.containsKey(option);
		}

		@Override
		public boolean hasOption(Option<?> option) {
			return this.options.contains(option);
		}
		
		@Override
		public boolean removeValue(Option<?> option) {
			final ParameterValue<?> removedValue = this.values.remove(option);
			
			if (removedValue != null) {
				this.setChanged();
				this.notifyObservers(option);
				
				return true;
			} else {
				return false;
			}
		}
		
		public Map<Option<?>, ParameterValue<?>> getValues() {
			return Collections.unmodifiableMap(this.values);
		}
		
		public boolean hasValue(Option<?> option) {
			return this.values.containsKey(option);
		}
		
		@Override
		public String getValue(Option<?> option) {
			return this.hasValue(option) ?
				this.getSingleValueString(option) : null;
		}
		
		@Override
		public List<String> getValues(Option<?> option) {
			return this.getAllValuesString(option);
		}
		
		@Override
		public void setValue(Option<?> option, String value) {
			if (option.isMultiple()) {
				throw new IllegalArgumentException("option is not simple");
			}
			
			if (value == null) {
				this.values.remove(option);
			} else {
				this.values.put(option, new SingleParameterValue(value));
			}
			
			this.setChanged();
			this.notifyObservers(option);
		}
		
		@Override
		public void setValue(Option<?> option, List<String> value) {
			if (!option.isMultiple()) {
				throw new IllegalArgumentException("option is not multiple");
			}
			
			if (value == null || value.isEmpty()) {
				this.values.remove(option);
			} else {
				this.values.put(option, new MultipleParameterValue(value));
			}
			
			this.setChanged();
			this.notifyObservers(option);
		}
		
		public boolean isComplete() {
			for (Option<?> option : this.options) {
				if (!option.isOptional() &&
					(!this.values.containsKey(option) || !option.canConvert(this.values.get(option)))
				) {
					return false;
				}
			}
			
			return true;
		}
		
		public void removeNonDefaultOptions() {
			for (Option<?> option : listOptions()) {
				if (!this.options.contains(option))
					this.removeValue(option);
			}
		}

		@Override
		public List<Option<?>> listOptions() {
			return new ArrayList<>(this.values.keySet());
		}
	}