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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import es.uvigo.ei.sing.bdbm.gui.repository.RepositoryTreeModel.TextFileMutableTreeNode;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.BlastResults;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;

public class RepositoryTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	final static ImageIcon ICON_FASTA = 
		new ImageIcon(RepositoryTreeRenderer.class.getResource("images/fasta.png"));
	final static ImageIcon ICON_REGULAR_DATABASE = 
		new ImageIcon(RepositoryTreeRenderer.class.getResource("images/database.png"));
	final static ImageIcon ICON_AGGREGATED_DATABASE = 
		new ImageIcon(RepositoryTreeRenderer.class.getResource("images/agg-database.png"));
	final static ImageIcon ICON_SEARCH_ENTRY = 
		new ImageIcon(RepositoryTreeRenderer.class.getResource("images/search-entry.png"));
	final static ImageIcon ICON_BLAST_RESULTS = 
		new ImageIcon(RepositoryTreeRenderer.class.getResource("images/export.png"));
	final static ImageIcon ICON_BLAST_RESULTS_ENTRY_OUTPUT = 
			new ImageIcon(RepositoryTreeRenderer.class.getResource("images/output.png"));
	final static ImageIcon ICON_SEQUENCE = 
			new ImageIcon(RepositoryTreeRenderer.class.getResource("images/sequence.png"));

	@Override
	public Component getTreeCellRendererComponent(
		JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row,
		boolean hasFocus
	) {
		if (value instanceof TextFileMutableTreeNode) {
			final TextFileMutableTreeNode<?> node = (TextFileMutableTreeNode<?>) value;
			
			this.setLeafIcon(node.getIcon());
			this.setOpenIcon(node.getIcon());
			this.setClosedIcon(node.getIcon());
		} else if (value instanceof DefaultMutableTreeNode) {
			final Object nodeValue = ((DefaultMutableTreeNode) value).getUserObject();
			
			if (nodeValue instanceof Fasta) {
				this.setLeafIcon(ICON_FASTA);
			} else if (nodeValue instanceof Database) {
				if (((Database) nodeValue).isAggregated()) {
					this.setLeafIcon(ICON_AGGREGATED_DATABASE);
				} else {
					this.setLeafIcon(ICON_REGULAR_DATABASE);
				}
			} else if (nodeValue instanceof SearchEntry) {
				this.setOpenIcon(ICON_SEARCH_ENTRY);
				this.setClosedIcon(ICON_SEARCH_ENTRY);
			} else if (nodeValue instanceof BlastResults) {
				this.setOpenIcon(ICON_BLAST_RESULTS);
				this.setClosedIcon(ICON_BLAST_RESULTS);
			} else {
				this.setLeafIcon(this.getDefaultLeafIcon());
				this.setOpenIcon(this.getDefaultOpenIcon());
				this.setClosedIcon(this.getDefaultOpenIcon());
			}
		} else {
			this.setLeafIcon(this.getDefaultLeafIcon());
			this.setOpenIcon(this.getDefaultOpenIcon());
			this.setClosedIcon(this.getDefaultOpenIcon());
		}
		
		return super.getTreeCellRendererComponent(
			tree, value, sel, expanded, leaf, row, hasFocus
		);
	}
}
