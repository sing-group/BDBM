/*-
 * #%L
 * BDBM CLI
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

package es.uvigo.ei.sing.bdbm.cli.commands;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.yaacli.command.AbstractCommand;
import es.uvigo.ei.sing.yaacli.command.option.Option;

public abstract class BDBMOptionCommand extends AbstractCommand {
	private static final String REFLECTION_OPTION_PREFIX = "OPTION_";

	@Override
	protected List<Option<?>> createOptions() {
		final Class<? extends BDBMOptionCommand> clazz = this.getClass();
		
		final Field[] fields = clazz.getFields();
		final List<Option<?>> options = new LinkedList<Option<?>>();
		for (Field field : fields) {
			if (isStatic(field.getModifiers())
				&& field.getName().startsWith(REFLECTION_OPTION_PREFIX)
				&& Option.class.isAssignableFrom(field.getType())
			) {
				try {
					options.add((Option<?>) field.get(null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return options;
	}
}
