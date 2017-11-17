/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo
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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SequenceListFastaParserAdapter extends FastaParserAdapter {
	private List<Sequence> sequences = new LinkedList<>();
	
	private String name = null;
	private List<String> sequenceFragments = new LinkedList<>();

	@Override
	public void sequenceNameRead(File file, String sequenceName) {
		this.name = sequenceName;
	}

	@Override
	public void sequenceFragmentRead(File file, String sequence) {
		this.sequenceFragments.add(sequence);
	}

	@Override
	public void sequenceEnd(File file) {
		sequences.add(new Sequence(name, new LinkedList<>(sequenceFragments)));
		sequenceFragments.clear();
	}

	public List<Sequence> getSequences() {
		return sequences;
	}
}
