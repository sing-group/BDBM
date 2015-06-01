/*
 * #%L
 * BDBM Core
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
package es.uvigo.ei.sing.bdbm.fasta;

import java.io.File;

public interface FastaParserListener {
	public abstract void parseStart(File file) throws FastaParseException;
	public abstract void sequenceStart(File file) throws FastaParseException;
	public abstract void sequenceNameRead(File file, String sequenceName) throws FastaParseException;
	public abstract void sequenceFragmentRead(File file, String sequenceFragment) throws FastaParseException;
	public abstract void sequenceEnd(File file) throws FastaParseException;
	public abstract void parseEnd(File file) throws FastaParseException;
}
