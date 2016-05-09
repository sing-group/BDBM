/*
 * #%L
 * BDBM GUI
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
package es.uvigo.ei.sing.bdbm.gui.repository;

import java.io.File;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryEvent;
import es.uvigo.ei.sing.bdbm.persistence.watcher.RepositoryListener;

class SynchronizationRepositoryListener<T, C extends SequenceEntity> implements RepositoryListener {
	private final SequenceType sequenceType;
	private final RepositoryTreeModel treeModel;
	private final SortedMutableTreeNode<T, C> node;

	public SynchronizationRepositoryListener(
		RepositoryTreeModel treeModel, 
		SequenceType sequenceType, 
		SortedMutableTreeNode<T, C> node
	) {
		this.treeModel = treeModel;
		this.sequenceType = sequenceType;
		this.node = node;
	}
	
	@Override
	public void repositoryChanged(final RepositoryEvent event) {
		if (event.getEntity().getType() == this.sequenceType) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateTree(event);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void updateTree(RepositoryEvent event) {
		synchronized (this.treeModel) {
			final SequenceEntity entity = event.getEntity();
			final TypedMutableTreeNode<C> entityNode = 
				getUserObjectChildNode(this.node, entity);
			
			switch (event.getType()) {
			case CREATE:
				if (entityNode == null) {
					this.treeModel.insertNode(
						this.node, 
						(TypedMutableTreeNode<C>) RepositoryTreeModel.createSequenceEntityNode(entity)
					);
				}
				
				break;
			case INVALIDATED:
			case DELETE:
				if (entityNode != null) {
					this.treeModel.deleteNode(this.node, entityNode);
				}
				
				break;
			case MODIFY:
				final File modifiedFile = event.getModifiedFile();
				
				if (entityNode != null) {
					if (entity instanceof SearchEntry) {
						final SearchEntry entry = (SearchEntry) entity;
						final SortedMutableTreeNode<SearchEntry, Query> searchEntryNode =
							(SortedMutableTreeNode<SearchEntry, Query>) entityNode;
						
						if (modifiedFile.exists()) {
							addSearchEntryQuery(entry, modifiedFile, searchEntryNode);
						} else {
							removeSearchEntryQuery(modifiedFile, searchEntryNode);
						}
					} else if (entity instanceof Export) {
						final Export export = (Export) entity;
						final SortedMutableTreeNode<Export, ExportEntry> exportNode =
							(SortedMutableTreeNode<Export, ExportEntry>) entityNode;
						
						if (modifiedFile.exists()) {
							addExportEntry(export, modifiedFile, exportNode);
						} else {
							removeExportEntry(modifiedFile, exportNode);
						}
					} else if (entity.getBaseFile().equals(modifiedFile)) {
						this.treeModel.replaceNode(
							entityNode, 
							(TypedMutableTreeNode<C>) RepositoryTreeModel.createSequenceEntityNode(entity)
						);
					}
				}
				break;
			}
		}
	}
	
	private static <K> TypedMutableTreeNode<K> getUserObjectChildNode(
		final SortedMutableTreeNode<?, K> node, Object userObject
	) {
		for (TypedMutableTreeNode<K> child : node.getChildNodes()) {
			if (child.getUserObject().equals(userObject)) {
				return child;
			}
		}
		
		return null;
	}
	
	private SearchEntry.Query getQueryForFile(SearchEntry searchEntry, File queryFile) {
		for (SearchEntry.Query query : searchEntry.listQueries()) {
			if (query.getBaseFile().equals(queryFile)) {
				return query;
			}
		}
		return null;
	}

	private void addSearchEntryQuery(
		final SearchEntry searchEntry,
		final File modifiedFile, 
		final SortedMutableTreeNode<SearchEntry, Query> searchEntryNode
	) {
		final Query query = this.getQueryForFile(searchEntry, modifiedFile);
		
		if (query != null && getUserObjectChildNode(searchEntryNode, query) == null) {
			this.treeModel.insertNode(
				searchEntryNode, 
				RepositoryTreeModel.createSearchEntryQuery(query)
			);
		}
	}

	private void removeSearchEntryQuery(
		final File modifiedFile,
		final SortedMutableTreeNode<SearchEntry, Query> searchEntryNode
	) {
		final TypedMutableTreeNode<Query> queryNode = 
			getSubEntityNodeForFile(searchEntryNode, modifiedFile);
		
		if (queryNode != null) {
			this.treeModel.deleteNode(searchEntryNode, queryNode);
		}
	}
	
	private ExportEntry getExportEntryForFile(Export export, File file) {
		for (ExportEntry entry : export.listEntries()) {
			if (entry.getBaseFile().equals(file) ||
				entry.getOutFile().equals(file) ||
				entry.getSummaryFastaFile().equals(file) ||
				Arrays.asList(entry.getSequenceFiles()).contains(file)
			) {
				return entry;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void addExportEntry(
		final Export export,
		final File modifiedFile, 
		final SortedMutableTreeNode<Export, ExportEntry> exportNode
	) {
		final ExportEntry entry = this.getExportEntryForFile(export, modifiedFile);
		
		if (entry != null) {
			final SortedMutableTreeNode<ExportEntry, String> entryNode = 
				(SortedMutableTreeNode<ExportEntry, String>) 
				getUserObjectChildNode(exportNode, entry);
				
			if (entry.getBaseFile().equals(modifiedFile)) {
				this.treeModel.insertNode(
					exportNode, 
					RepositoryTreeModel.createExportEntryNode(entry)
				);
			} else {
				if (modifiedFile.equals(entry.getOutFile())) {
					if (getUserObjectChildNode(entryNode, entry.getOutFile().getName()) == null)
						throw new IllegalStateException("Inconsitent Export Entry state. Missing out file.");
				} else if (modifiedFile.equals(entry.getSummaryFastaFile())) {
					if (getUserObjectChildNode(entryNode, modifiedFile.getName()) == null) {
						this.treeModel.insertNode(
							entryNode,
							RepositoryTreeModel.createExportEntrySummaryFileNode(modifiedFile)
						);
					}
				} else { // It's a .txt file
					if (getUserObjectChildNode(entryNode, modifiedFile.getName()) == null) {
						this.treeModel.insertNode(
							entryNode,
							RepositoryTreeModel.createExportEntryTextFileNode(modifiedFile)
						);
					}
				}
			}
		}
	}
	
	private static <K extends SequenceEntity> TypedMutableTreeNode<K> getSubEntityNodeForFile(
		SortedMutableTreeNode<?, K> node, File file
	) {
		for (TypedMutableTreeNode<K> child : node.getChildNodes()) {
			if (child.getUserObject().getBaseFile().equals(file)) {
				return child;
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private void removeExportEntry(
		final File modifiedFile,
		final SortedMutableTreeNode<Export, ExportEntry> exportNode
	) {
		final TypedMutableTreeNode<ExportEntry> entryNode = 
			getSubEntityNodeForFile(exportNode, modifiedFile);
		
		if (entryNode != null) { // Entry deleted
			this.treeModel.deleteNode(exportNode, entryNode);
		} else {
			for (TypedMutableTreeNode<ExportEntry> exportEntryNode : exportNode.getChildNodes()) {
				if (exportEntryNode.getUserObject().getOutFile().equals(modifiedFile)) {
					throw new IllegalStateException("Inconsitent Export Entry state. Out file can't be deleted.");
				} else if (exportEntryNode.getUserObject().getSummaryFastaFile().equals(modifiedFile)) {
					throw new IllegalStateException("Inconsitent Export Entry state. FASTA file can't be deleted.");
				} else {
					final SortedMutableTreeNode<ExportEntry, String> sortedExportEntryNode =
						(SortedMutableTreeNode<ExportEntry, String>) exportEntryNode;
					
					final TypedMutableTreeNode<String> fileNode = 
						getUserObjectChildNode(sortedExportEntryNode, modifiedFile.getName());
					
					if (fileNode != null) {
						this.treeModel.deleteNode(sortedExportEntryNode, fileNode);
					}
				}
			}
		}
	}
}