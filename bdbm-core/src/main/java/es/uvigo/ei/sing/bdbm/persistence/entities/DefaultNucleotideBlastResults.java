/*-
 * #%L
 * BDBM Core
 * %%
 * Copyright (C) 2014 - 2017 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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

package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideExport;

public class DefaultNucleotideExport extends AbstractExport implements NucleotideExport {
	public DefaultNucleotideExport(File file) {
		super(SequenceType.NUCLEOTIDE, file);
	}
	
	@Override
	public List<NucleotideExportEntry> listEntries() {
		final File[] files = this.getDirectory().listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		final List<NucleotideExportEntry> exportEntries = new ArrayList<NucleotideExportEntry>(files.length);
		
		for (File file : files) {
			exportEntries.add(new DefaultNucleotideExportEntry(this, file));
		}
		
		return exportEntries;
	}
	
	public class DefaultNucleotideExportEntry extends DefaultExportEntry implements NucleotideExportEntry {
		public DefaultNucleotideExportEntry(Export export, File baseFile) {
			super(export, baseFile);
		}
	}
}
