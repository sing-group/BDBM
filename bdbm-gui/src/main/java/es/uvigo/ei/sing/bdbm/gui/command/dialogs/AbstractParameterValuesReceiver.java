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

import java.util.List;

import es.uvigo.ei.sing.bdbm.gui.command.ParameterValuesReceiver;
import es.uvigo.ei.sing.yaacli.command.option.Option;

abstract class AbstractParameterValuesReceiver implements
		ParameterValuesReceiver {
	@Override
	public String getValue(Option<?> option) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getValues(Option<?> option) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasOption(Option<?> option) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasValue(Option<?> option) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeValue(Option<?> option) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(Option<?> option, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(Option<?> option, List<String> value) {
		throw new UnsupportedOperationException();
	}
}
