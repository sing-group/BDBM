/*
 * #%L
 * BDBM API
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
package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observer;

public interface Export extends SequenceEntity, Comparable<Export> {
	public abstract File getDirectory();
	public abstract List<? extends ExportEntry> listEntries();
	public abstract ExportEntry getExportEntry(String name);
	public abstract void deleteExportEntry(ExportEntry entry)
	throws IllegalArgumentException, IOException;
	
	// Observable methods
	public void addObserver(Observer o);
	public int countObservers();
	public void deleteObserver(Observer o);
	public void deleteObservers();
	public boolean hasChanged();
	public void notifyObservers();
	public void notifyObservers(Object arg);
	
	public interface ExportEntry extends SequenceEntity {
		public Export getExport();
		public File getOutFile();
		public File[] getSequenceFiles();
		public File getSummaryFastaFile();
		public void deleteSequenceFiles();
	}
}
