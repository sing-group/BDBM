/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2018 Miguel Reboiro-Jato, Critina P. Vieira, Hugo
 *       López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge
 *       Vieira
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
package es.uvigo.ei.sing.bdbm.fasta;

import java.util.List;
import java.util.stream.Collectors;

public class Sequence {
	private String name;
	private List<String> fragments;

	public Sequence(String name, List<String> fragments) {
		this.name = name;
		this.fragments = fragments;
	}

	public String getName() {
		return name;
	}

	public String getChain() {
		return fragments.stream().collect(Collectors.joining());
	}

	public List<String> getFragments() {
		return fragments;
	}

	public int getLength() {
		return this.getChain().length();
	}

	@Override
	public String toString() {
		return this.name + "\n" + this.getChain();
	}
}
