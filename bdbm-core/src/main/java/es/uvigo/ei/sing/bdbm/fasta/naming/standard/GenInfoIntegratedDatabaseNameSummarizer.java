/*-
 * #%L
 * BDBM Core
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

package es.uvigo.ei.sing.bdbm.fasta.naming.standard;

import static java.util.Arrays.asList;

import java.util.List;

public class GenInfoIntegratedDatabaseNameSummarizer extends AbstractStandardNameSummarizer {
	@Override
	public String getPrefix() {
		return "gi";
	}
	
	@Override
	public String getDescription() {
		return "GenInfo Integrated Database";
	}
	
	@Override
	public List<MatcherNameField> getNameFields() {
		return asList(
			new MatcherNameField(0, "Integer", false, "\\d+")
		);
	}
}
