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

package es.uvigo.ei.sing.bdbm.fasta;

import java.io.File;

public class FastaParserAdapter implements FastaParserListener {
	@Override
	public void parseStart(File file) throws FastaParseException {}

	@Override
	public void sequenceStart(File file) throws FastaParseException {}

	@Override
	public void sequenceNameRead(File file, String sequenceName) throws FastaParseException {}

	@Override
	public void sequenceFragmentRead(File file, String sequence) throws FastaParseException {}

	@Override
	public void sequenceEnd(File file) throws FastaParseException {}
	
	@Override
	public void emptyLine(File file) {}

	@Override
	public void parseEnd(File file) throws FastaParseException {}
}
