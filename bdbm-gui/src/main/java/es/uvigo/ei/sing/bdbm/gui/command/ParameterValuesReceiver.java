/*-
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

import java.util.List;

import es.uvigo.ei.sing.yaacli.command.option.Option;

public interface ParameterValuesReceiver {
	public abstract String getValue(Option<?> option);
	public List<String> getValues(Option<?> option);
	public abstract boolean hasOption(Option<?> option);
	public abstract boolean hasValue(Option<?> option);
	public abstract boolean removeValue(Option<?> option);
	public abstract void setValue(Option<?> option, String value);
	public abstract void setValue(Option<?> option, List<String> value);
}
