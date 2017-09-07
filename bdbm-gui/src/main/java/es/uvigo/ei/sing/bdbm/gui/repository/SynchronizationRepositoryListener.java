/*-
 * #%L
 * BDBM GUI
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

package es.uvigo.ei.sing.bdbm.gui.repository;

import java.io.File;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults.BlastResultsEntry;
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
					} else if (entity instanceof BlastResults) {
						final BlastResults blastResults = (BlastResults) entity;
						final SortedMutableTreeNode<BlastResults, BlastResultsEntry> blastResultsNode =
							(SortedMutableTreeNode<BlastResults, BlastResultsEntry>) entityNode;
						
						if (modifiedFile.exists()) {
							addBlastResultsEntry(blastResults, modifiedFile, blastResultsNode);
						} else {
							removeBlastResultsEntry(modifiedFile, blastResultsNode);
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
	
	private BlastResultsEntry getBlastResultsEntryForFile(BlastResults blastResults, File file) {
		for (BlastResultsEntry entry : blastResults.listEntries()) {
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
	private void addBlastResultsEntry(
		final BlastResults blastResults,
		final File modifiedFile, 
		final SortedMutableTreeNode<BlastResults, BlastResultsEntry> blastResultsNode
	) {
		final BlastResultsEntry entry = this.getBlastResultsEntryForFile(blastResults, modifiedFile);
		
		if (entry != null) {
			final SortedMutableTreeNode<BlastResultsEntry, String> entryNode = 
				(SortedMutableTreeNode<BlastResultsEntry, String>) 
				getUserObjectChildNode(blastResultsNode, entry);
				
			if (entry.getBaseFile().equals(modifiedFile)) {
				this.treeModel.insertNode(
					blastResultsNode, 
					RepositoryTreeModel.createBlastResultsEntryNode(entry)
				);
			} else {
				if (modifiedFile.equals(entry.getOutFile())) {
					if (getUserObjectChildNode(entryNode, entry.getOutFile().getName()) == null)
						throw new IllegalStateException("Inconsitent BLAST Results Entry state. Missing out file.");
				} else if (modifiedFile.equals(entry.getSummaryFastaFile())) {
					if (getUserObjectChildNode(entryNode, modifiedFile.getName()) == null) {
						this.treeModel.insertNode(
							entryNode,
							RepositoryTreeModel.createBlastResultsEntrySummaryFileNode(modifiedFile)
						);
					}
				} else { // It's a .txt file
					if (getUserObjectChildNode(entryNode, modifiedFile.getName()) == null) {
						this.treeModel.insertNode(
							entryNode,
							RepositoryTreeModel.createBlastResultsEntryTextFileNode(modifiedFile)
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
	private void removeBlastResultsEntry(
		final File modifiedFile,
		final SortedMutableTreeNode<BlastResults, BlastResultsEntry> blastResultsNode
	) {
		final TypedMutableTreeNode<BlastResultsEntry> entryNode = 
			getSubEntityNodeForFile(blastResultsNode, modifiedFile);
		
		if (entryNode != null) { // Entry deleted
			this.treeModel.deleteNode(blastResultsNode, entryNode);
		} else {
			for (TypedMutableTreeNode<BlastResultsEntry> blastResultsEntryNode : blastResultsNode.getChildNodes()) {
				if (blastResultsEntryNode.getUserObject().getOutFile().equals(modifiedFile)) {
					throw new IllegalStateException("Inconsitent BLAST Results Entry state. Out file can't be deleted.");
				} else if (blastResultsEntryNode.getUserObject().getSummaryFastaFile().equals(modifiedFile)) {
					throw new IllegalStateException("Inconsitent BLAST Results Entry state. FASTA file can't be deleted.");
				} else {
					final SortedMutableTreeNode<BlastResultsEntry, String> sortedblastResultsEntryNode =
						(SortedMutableTreeNode<BlastResultsEntry, String>) blastResultsEntryNode;
					
					final TypedMutableTreeNode<String> fileNode = 
						getUserObjectChildNode(sortedblastResultsEntryNode, modifiedFile.getName());
					
					if (fileNode != null) {
						this.treeModel.deleteNode(sortedblastResultsEntryNode, fileNode);
					}
				}
			}
		}
	}
}
