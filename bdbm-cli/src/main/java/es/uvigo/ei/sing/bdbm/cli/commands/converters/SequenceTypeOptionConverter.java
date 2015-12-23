/*
 * #%L
 * BDBM CLI
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
package es.uvigo.ei.sing.bdbm.cli.commands.converters;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.yaacli.command.option.AbstractOptionConverter;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class SequenceTypeOptionConverter extends AbstractOptionConverter<SequenceType> {
	@Override
	public SequenceType convert(SingleParameterValue value) {
		try {
			return SequenceType.valueOf(value.getValue());
		} catch (IllegalArgumentException e) {
			return SequenceType.forDBType(value.getValue());
		}
	}

	@Override
	public Class<SequenceType> getTargetClass() {
		return SequenceType.class;
	}

	@Override
	public boolean canConvert(SingleParameterValue value) {
		try {
			this.convert(value);
			return true;
		} catch (IllegalArgumentException iae) {
			return false;
		}
	}
}
