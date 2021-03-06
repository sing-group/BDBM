/*-
 * #%L
 * BDBM GUI
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

package es.uvigo.ei.sing.bdbm.gui.repository;

import java.io.File;
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.persistence.BDBMRepositoryManager;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults.BlastResultsEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultNucleotideExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.DefaultProteinExport;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;

public class RepositoryTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;

	private static final Comparator<Export> EXPORT_COMPARATOR =
		new Comparator<Export>() {
		@Override
		public int compare(Export o1, Export o2) {

			return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
		}
	};

	private final BDBMRepositoryManager repositoryManager;

	private final SortedMutableTreeNode<String, Fasta> tnFasta;
	private final SortedMutableTreeNode<String, Database> tnDatabase;
	private final SortedMutableTreeNode<String, SearchEntry> tnSearchEntry;
	private final SortedMutableTreeNode<String, BlastResults> tnBlastResults;
	private final SortedMutableTreeNode<String, Export> tnExport;

	@SuppressWarnings("unchecked")
	public RepositoryTreeModel(
		SequenceType sequenceType,
		BDBMRepositoryManager repositoryManager
	) {
		super(createTreeNodes(sequenceType, repositoryManager));
		this.repositoryManager = repositoryManager;

		this.tnFasta = (SortedMutableTreeNode<String, Fasta>) this.getChild(this.getRoot(), 0);
		this.tnDatabase = (SortedMutableTreeNode<String, Database>) this.getChild(this.getRoot(), 1);
		this.tnSearchEntry = (SortedMutableTreeNode<String, SearchEntry>) this.getChild(this.getRoot(), 2);
		this.tnBlastResults = (SortedMutableTreeNode<String, BlastResults>) this.getChild(this.getRoot(), 3);
		this.tnExport = (SortedMutableTreeNode<String, Export>) this.getChild(this.getRoot(), 4);

		this.repositoryManager.fasta().addRepositoryListener(
			new SynchronizationRepositoryListener<>(this, sequenceType, this.tnFasta)
		);
		this.repositoryManager.database().addRepositoryListener(
			new SynchronizationRepositoryListener<>(this, sequenceType, this.tnDatabase)
		);
		this.repositoryManager.searchEntry().addRepositoryListener(
			new SynchronizationRepositoryListener<>(this, sequenceType, this.tnSearchEntry)
		);
		this.repositoryManager.blastResults().addRepositoryListener(
			new SynchronizationRepositoryListener<>(this, sequenceType, this.tnBlastResults)
		);
		this.repositoryManager.export().addRepositoryListener(
			new SynchronizationRepositoryListener<>(this, sequenceType, this.tnExport)
		);
	}

	<T, C> void insertNode(SortedMutableTreeNode<T, C> parentNode, TypedMutableTreeNode<C> child) {
		final int index = parentNode.add(child);

		this.fireTreeNodesInserted(
			parentNode, parentNode.getPath(),
			new int[] { index },
			new Object[] { child }
		);
	}

	<T, C> void deleteNode(SortedMutableTreeNode<T, C> parentNode, TypedMutableTreeNode<C> child) {
		final int childIndex = parentNode.getIndex(child);

		parentNode.remove(childIndex);

		this.fireTreeNodesRemoved(
			parentNode, parentNode.getPath(),
			new int[] { childIndex },
			new Object[] { child }
		);
	}

	<C> void replaceNode(TypedMutableTreeNode<C> oldChild, TypedMutableTreeNode<C> newChild) {
		if (oldChild.getParent() instanceof DefaultMutableTreeNode) {
			final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) oldChild.getParent();
			final int childIndex = parent.getIndex(oldChild);

			if (childIndex != -1) {
				parent.remove(oldChild);
				parent.insert(newChild, childIndex);

				this.fireTreeNodesChanged(
					parent, parent.getPath(),
					new int[] { childIndex }, new Object[] { newChild }
				);
			}
		} else {
			throw new IllegalArgumentException("old node must have a DefaultMutableTreeNode parent");
		}
	}

	private static MutableTreeNode createTreeNodes(
		SequenceType sequenceType,
		BDBMRepositoryManager repositoryManager
	) {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

		root.add(createFastaNodes(
			repositoryManager.fasta().list(sequenceType)
		));

		root.add(createDatabaseNodes(
			repositoryManager.database().list(sequenceType)
		));

		root.add(createSearchEntry(
			repositoryManager.searchEntry().list(sequenceType)
		));

		root.add(createBlastResults(
			repositoryManager.blastResults().list(sequenceType)
		));

		root.add(createExportNodes(
			repositoryManager.export().list(sequenceType)
		));

		return root;
	}

	private static SortedMutableTreeNode<String, Fasta> createFastaNodes(Fasta[] fastas) {
		final SortedMutableTreeNode<String, Fasta> root = new SortedMutableTreeNode<>(
			new SequenceEntityNameComparator<Fasta>(), "FASTA Files"
		);

		for (Fasta fasta : fastas) {
			root.add(createFastaNode(fasta));
		}

		return root;
	}

	private static SortedMutableTreeNode<String, Database> createDatabaseNodes(Database[] databases) {
		final SortedMutableTreeNode<String, Database> root = new SortedMutableTreeNode<>(
			new SequenceEntityNameComparator<Database>(), "Databases"
		);

		for (Database database : databases) {
			root.add(createDatabaseNode(database));
		}

		return root;
	}

	private static SortedMutableTreeNode<String, SearchEntry> createSearchEntry(SearchEntry[] entries) {
		final SortedMutableTreeNode<String, SearchEntry> root = new SortedMutableTreeNode<>(
			new SequenceEntityNameComparator<SearchEntry>(), "Search Entries"
		);

		for (SearchEntry entry : entries) {
			root.add(createSearchEntryNode(entry));
		}

		return root;
	}

	private static SortedMutableTreeNode<String, BlastResults> createBlastResults(BlastResults[] blastResults) {
		final SortedMutableTreeNode<String, BlastResults> root = new SortedMutableTreeNode<>(
			new SequenceEntityNameComparator<BlastResults>(), "BLAST Results"
		);

		for (BlastResults result : blastResults) {
			root.add(createBlastResultsNode(result));
		}

		return root;
	}

	private static SortedMutableTreeNode<String, Export> createExportNodes(Export[] exports) {
		final SortedMutableTreeNode<String, Export> root = new SortedMutableTreeNode<>(
			EXPORT_COMPARATOR, "Export Files"
		);

		for (Export export : exports) {
			root.add(createExportNode(export));
		}

		return root;
	}

	static TypedMutableTreeNode<?> createSequenceEntityNode(SequenceEntity entity)
	throws IllegalArgumentException {
		if (entity instanceof Fasta) {
			return createFastaNode((Fasta) entity);
		} else if (entity instanceof Database) {
			return createDatabaseNode((Database) entity);
		} else if (entity instanceof SearchEntry) {
			return createSearchEntryNode((SearchEntry) entity);
		} else if (entity instanceof BlastResults) {
			return createBlastResultsNode((BlastResults) entity);
		} else if (entity instanceof Export) {
			return createExportNode((Export) entity);
		} else {
			throw new IllegalArgumentException("Unknown entity type");
		}
	}

	private static TypedMutableTreeNode<Fasta> createFastaNode(Fasta fasta) {
		return new TextFileMutableTreeNode<>(
			fasta, fasta.getFile(), RepositoryTreeRenderer.ICON_FASTA
		);
	}

	private static TypedMutableTreeNode<Database> createDatabaseNode(Database database) {
		return new TypedMutableTreeNode<Database>(database);
	}

	private static SortedMutableTreeNode<SearchEntry, Query> createSearchEntryNode(SearchEntry entry) {
		final SortedMutableTreeNode<SearchEntry, Query> node =
			new SortedMutableTreeNode<>(
				new SequenceEntityNameComparator<Query>(), entry
			);

		for (Query query : entry.listQueries()) {
			node.add(createSearchEntryQuery(query));
		}

		return node;
	}

	static TypedMutableTreeNode<Query> createSearchEntryQuery(Query query) {
		return new TextFileMutableTreeNode<>(
			query, query.getBaseFile(), RepositoryTreeRenderer.ICON_SEQUENCE
		);
	}

	private static SortedMutableTreeNode<BlastResults, BlastResultsEntry> createBlastResultsNode(BlastResults blastResults) {
		final SortedMutableTreeNode<BlastResults, BlastResultsEntry> node =
			new SortedMutableTreeNode<>(
				new SequenceEntityNameComparator<BlastResultsEntry>(), blastResults
			);

		for (BlastResultsEntry resultsEntry : blastResults.listEntries()) {
			node.add(createBlastResultsEntryNode(resultsEntry));
		}

		return node;
	}

	private static TypedMutableTreeNode<Export> createExportNode(Export export) {
		File file = export.getFile();
		if (!file.isDirectory()) {
			return new TextFileMutableTreeNode<Export>(export, file,
				RepositoryTreeRenderer.ICON_EXPORT);
		} else {
			final SortedMutableTreeNode<Export, Export> exportEntry =
				new SortedMutableTreeNode<>(EXPORT_COMPARATOR, export);

			for (File txtFile : file.listFiles()) {
				Export child = export.getType().equals(SequenceType.NUCLEOTIDE) ?
					new DefaultNucleotideExport(txtFile) :
					new DefaultProteinExport(txtFile);
				exportEntry.add(new TextFileMutableTreeNode<Export>(child, txtFile, RepositoryTreeRenderer.ICON_EXPORT));
			}

			return exportEntry;
		}
	}

	static SortedMutableTreeNode<BlastResultsEntry, String> createBlastResultsEntryNode(final BlastResultsEntry blastResultsEntry) {
		final Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				final File outFile = blastResultsEntry.getOutFile();

				if (outFile.getName().equals(o1)) {
					return -1;
				} else if (outFile.getName().equals(o2)) {
					return 1;
				}

				final File summaryFile = blastResultsEntry.getSummaryFastaFile();
				if (summaryFile != null) {
					if (summaryFile.getName().equals(o1)) {
						return -1;
					} else if (summaryFile.getName().equals(o2)) {
						return 1;
					}
				}

				return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
			}
		};

		final SortedMutableTreeNode<BlastResultsEntry, String> nodeBlastResultsEntry =
			new SortedMutableTreeNode<>(
				comparator, blastResultsEntry
			);

		nodeBlastResultsEntry.add(createBlastResultsEntryOutFileNode(blastResultsEntry.getOutFile()));

		if (blastResultsEntry.getSummaryFastaFile().canRead()) {
			nodeBlastResultsEntry.add(createBlastResultsEntrySummaryFileNode(blastResultsEntry.getSummaryFastaFile()));
		}

		for (File txtFile : blastResultsEntry.getSequenceFiles()) {
			nodeBlastResultsEntry.add(createBlastResultsEntryTextFileNode(txtFile));
		}

		return nodeBlastResultsEntry;
	}

	static TextFileMutableTreeNode<String> createBlastResultsEntryTextFileNode(File blastResultsEntryFile) {
		return new TextFileMutableTreeNode<>(
			blastResultsEntryFile.getName(), blastResultsEntryFile, RepositoryTreeRenderer.ICON_SEQUENCE
		);
	}

	static TextFileMutableTreeNode<String> createBlastResultsEntryOutFileNode(File blastResultsEntryFile) {
		return new TextFileMutableTreeNode<>(
			blastResultsEntryFile.getName(), blastResultsEntryFile, RepositoryTreeRenderer.ICON_BLAST_RESULTS_ENTRY_OUTPUT
		);
	}

	static TextFileMutableTreeNode<String> createBlastResultsEntrySummaryFileNode(File blastResultsEntryFile) {
		return new TextFileMutableTreeNode<>(
			blastResultsEntryFile.getName(), blastResultsEntryFile, RepositoryTreeRenderer.ICON_FASTA
		);
	}

	public static class TextFileMutableTreeNode<T> extends TypedMutableTreeNode<T> {
		private static final long serialVersionUID = 1L;

		private final File file;
		private final Icon icon;

		public TextFileMutableTreeNode(T value, File file) {
			this(value, file, null);
		}

		public TextFileMutableTreeNode(T value, File file, Icon icon) {
			super(value, false);

			this.file = file;
			this.icon = icon;
		}

		public File getFile() {
			return this.file;
		}

		public Icon getIcon() {
			return this.icon;
		}
	}

	private static class SequenceEntityNameComparator<T extends SequenceEntity> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			if (o1 == null) return -1;
			else if (o2 == null) return 1;
			else return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}
	}
}
