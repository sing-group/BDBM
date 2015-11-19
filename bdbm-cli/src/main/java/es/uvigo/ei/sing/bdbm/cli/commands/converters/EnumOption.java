/*
 * #%L
 * BDBM CLI
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
package es.uvigo.ei.sing.bdbm.cli.commands.converters;

import es.uvigo.ei.sing.yaacli.command.option.AbstractOptionConverter;
import es.uvigo.ei.sing.yaacli.command.option.DefaultValuedOption;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class EnumOption<T extends Enum<T>> extends DefaultValuedOption<T> {
	public EnumOption(
		String paramName, String shortName,
		String description, T defaultValue
	) {
		super(paramName, shortName, description, defaultValue.name(), 
			new EnumConverter<T>(defaultValue.getDeclaringClass())
		);
	}
	
	public EnumOption(
		String paramName, String shortName,
		String description, Class<T> enumType, String defaultValue
	) {
		super(paramName, shortName, description, defaultValue, new EnumConverter<T>(enumType));
	}
	
	public EnumOption(
		String paramName, String shortName, 
		String description, T defaultValue,
		boolean optional, boolean requiresValue, boolean isMultiple
	) {
		super(
			paramName, shortName, 
			description, defaultValue.name(), 
			optional, requiresValue, isMultiple, 
			new EnumConverter<T>(defaultValue.getDeclaringClass())
		);
	}
	
	public EnumOption(
		String paramName, String shortName, 
		String description, Class<T> enumType, String defaultValue, 
		boolean optional, boolean requiresValue, boolean isMultiple
	) {
		super(
			paramName, shortName, 
			description, defaultValue, 
			optional, requiresValue, isMultiple, 
			new EnumConverter<T>(enumType)
		);
	}

	private final static class EnumConverter<E extends Enum<E>> extends AbstractOptionConverter<E> {
		private final Class<E> enumType;
		
		public EnumConverter(Class<E> enumType) {
			this.enumType = enumType;
		}

		@Override
		public Class<E> getTargetClass() {
			return this.enumType;
		}

		@Override
		public boolean canConvert(SingleParameterValue value) {
			try {
				Enum.valueOf(enumType, value.getValue());
				return true;
			} catch (IllegalArgumentException | NullPointerException e) {
				return false;
			}
		}

		@Override
		public E convert(SingleParameterValue value) {
			return Enum.valueOf(enumType, value.getValue());
		}
	}
}
