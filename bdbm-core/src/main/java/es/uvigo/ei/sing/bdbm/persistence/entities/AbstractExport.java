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
package es.uvigo.ei.sing.bdbm.persistence.entities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.watcher.PollingRepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcher;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryWatcherListener;

public abstract class AbstractExport extends AbstractSequenceEntity implements Export {
	public static Export newExport(SequenceType sequenceType, File directory) {
		switch (sequenceType) {
		case PROTEIN:
			return new DefaultProteinExport(directory);
		case NUCLEOTIDE:
			return new DefaultNucleotideExport(directory);
		default:
			throw new IllegalStateException("Unknown sequence type");
		}
	}
	
	protected final RepositoryWatcher watcher;
	
	protected AbstractExport(SequenceType type, File file) {
		super(type, file);
		
		this.watcher = new PollingRepositoryWatcher();
		this.watcher.addRepositoryWatcherListener(new ExportRepositoryListener());
		this.watcher.register(this.getBaseFile());
	}
	
	private final class ExportRepositoryListener implements RepositoryWatcherListener {
		@Override
		public void repositoryChanged(RepositoryWatcherEvent event) {
			switch (event.getType()) {
			case DELETE:
				if (event.getFile().equals(AbstractExport.this.getBaseFile())) {
					AbstractExport.this.watcher.clear();
					AbstractExport.this.watcher.removeRepositoryWatcherListener(this);
				}
				// No break
			case CREATE:
				AbstractExport.this.setChanged();
				AbstractExport.this.notifyObservers(event.getFile());
				break;
			}
		}
	}
	
	@Override
	public ExportEntry getExportEntry(String name) {
		for (ExportEntry entry : this.listEntries()) {
			if (entry.getName().equals(name))
				return entry;
		}
		
		return null;
	}
	
	@Override
	public void deleteExportEntry(ExportEntry entry)
	throws IllegalArgumentException, IOException {
		if (this.listEntries().contains(entry)) {
			FileUtils.deleteDirectory(entry.getBaseFile());
		} else {
			throw new IllegalArgumentException("ExportEntry doesn't belongs to this Export");
		}
	}

	@Override
	public File getDirectory() {
		return this.getBaseFile();
	}
	
	@Override
	public int compareTo(Export o) {
		return this.getDirectory().compareTo(o.getDirectory());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getDirectory() == null) ? 0 : this.getDirectory().hashCode());
		result = prime * result + ((this.getType() == null) ? 0 : this.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Export))
			return false;
		
		Export other = (Export) obj;
		if (this.getDirectory() == null) {
			if (other.getDirectory()!= null)
				return false;
		} else if (!this.getDirectory().equals(other.getDirectory()))
			return false;
		if (this.getType() != other.getType())
			return false;
		return true;
	}
	
	public class DefaultExportEntry implements ExportEntry {
		private final Export export;
		private final File baseFile;
		
		protected DefaultExportEntry(Export export, File baseFile) {
			this.export = export;
			this.baseFile = baseFile;
		}
		
		@Override
		public Export getExport() {
			return this.export;
		}
		
		@Override
		public SequenceType getType() {
			return AbstractExport.this.getType();
		}
		
		@Override
		public File getBaseFile() {
			return this.baseFile;
		}
		
		@Override
		public String getName() {
			return this.getBaseFile().getName();
		}
		
		@Override
		public File getOutFile() {
			return new File(this.getBaseFile(), this.getBaseFile().getName() + ".out");
		}
		
		@Override
		public File getSummaryFastaFile() {
			return new File(this.getBaseFile(), this.getBaseFile().getName() + ".fasta");
		}
		
		@Override
		public synchronized File[] getSequenceFiles() {
			return this.getBaseFile().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".txt");
				}
			});
		}
		
		@Override
		public synchronized void deleteSequenceFiles() {
			for (File sequenceFile : this.getSequenceFiles()) {
				sequenceFile.delete();
			}
		}

		@Override
		public String toString() {
			return this.getName();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.getBaseFile() == null) ? 0 : this.getBaseFile().hashCode());
			result = prime * result + ((this.getType() == null) ? 0 : this.getType().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ExportEntry))
				return false;
			
			ExportEntry other = (ExportEntry) obj;
			if (this.getBaseFile() == null) {
				if (other.getBaseFile() != null)
					return false;
			} else if (!this.getBaseFile().equals(other.getBaseFile()))
				return false;
			if (this.getType() != other.getType())
				return false;
			return true;
		}
	}
//	
//	@Override
//	public String toString() {
//		return "EXPORT: " + this.getName();
//	}
}
