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
package es.uvigo.ei.sing.bdbm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.repository.OperationsRepositoryListener;
import es.uvigo.ei.sing.bdbm.gui.repository.RepositoryTreeModel;
import es.uvigo.ei.sing.bdbm.gui.repository.RepositoryTreeModel.TextFileMutableTreeNode;
import es.uvigo.ei.sing.bdbm.gui.repository.RepositoryTreeRenderer;
import es.uvigo.ei.sing.bdbm.gui.tabpanel.CloseableJTabbedPane;
import es.uvigo.ei.sing.bdbm.gui.tabpanel.TabCloseAdapter;
import es.uvigo.ei.sing.bdbm.gui.tabpanel.TabCloseEvent;
import es.uvigo.ei.sing.bdbm.log.LogConfiguration;
import es.uvigo.ei.sing.bdbm.persistence.BDBMRepositoryManager;

import static es.uvigo.ei.sing.bdbm.gui.FileWatcher.Watcher;

public class BDBMMainPanel extends JPanel {
	private static final String TAB_LABEL_NUCLEOTIDE = "Nucleotide";
	private static final String TAB_LABEL_PROTEIN = "Protein";

	private static final long serialVersionUID = 1L;
	
	private final JTree treeNucleotide;
	private final JTree treeProtein;

	private final CloseableJTabbedPane tbMain;
	private final JTabbedPane tpData;
	
	public BDBMMainPanel(BDBMGUIController controller) {
		super(new BorderLayout());
		
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(240);
		
		this.tpData = new JTabbedPane();
		this.tbMain = new CloseableJTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		this.treeNucleotide = createRepositoryTree(
			SequenceType.NUCLEOTIDE, 
			controller.getManager().getRepositoryManager(),
			BDBMMainPanel.TAB_LABEL_NUCLEOTIDE
		);
		this.treeProtein = createRepositoryTree(
			SequenceType.PROTEIN,
			controller.getManager().getRepositoryManager(),
			BDBMMainPanel.TAB_LABEL_PROTEIN
		);
		
		this.treeNucleotide.addMouseListener(new OperationsRepositoryListener(controller));
		this.treeProtein.addMouseListener(new OperationsRepositoryListener(controller));
		
		this.treeNucleotide.setDoubleBuffered(true);
		this.treeProtein.setDoubleBuffered(true);
		this.treeNucleotide.setShowsRootHandles(true);
		this.treeProtein.setShowsRootHandles(true);
		
		final TextFileMouseListener textFileMouseListener = 
			new TextFileMouseListener(this.tbMain);
		this.treeNucleotide.addMouseListener(textFileMouseListener);
		this.treeProtein.addMouseListener(textFileMouseListener);
		
		this.tpData.addTab(
			BDBMMainPanel.TAB_LABEL_NUCLEOTIDE, 
			new JScrollPane(this.treeNucleotide)
		);
		this.tpData.addTab(
			BDBMMainPanel.TAB_LABEL_PROTEIN, 
			new JScrollPane(this.treeProtein)
		);

		final JComponent componentMain;
		
		if (LogConfiguration.hasAnyExecutionAppender()) {
			final JSplitPane splitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitMain.setOneTouchExpandable(true);
			splitMain.setContinuousLayout(true);
			splitMain.setDividerLocation(400);
	
			final JTabbedPane tpLoggers = new JTabbedPane();
			
			if (LogConfiguration.hasStandardExecutionAppender()) {
				final PanelLogger standardPanelLogger = new PanelLogger(
					LogConfiguration.getStandardExecutionAppender()
				);
				standardPanelLogger.setForeground(new Color(0, 224, 0));
				tpLoggers.addTab("Standard Log", standardPanelLogger);
			}
			
			if (LogConfiguration.hasErrorExecutionAppender()) {
				final PanelLogger errorPanelLogger = new PanelLogger(
					LogConfiguration.getErrorExecutionAppender()
				);
				errorPanelLogger.setForeground(new Color(224, 0, 0));
				tpLoggers.addTab("Error Log", errorPanelLogger);
			}
			
			splitMain.setTopComponent(tbMain);
			splitMain.setBottomComponent(tpLoggers);
		
			componentMain = splitMain;
		} else {
			componentMain = tbMain;
		}
		
		splitPane.setLeftComponent(tpData);
		splitPane.setRightComponent(componentMain);
		
		this.add(splitPane, BorderLayout.CENTER);
	}
	
	private JTree createRepositoryTree(
		SequenceType sequenceType, 
		BDBMRepositoryManager repositoryManager,
		final String tabTitle
	) {
		final JTree tree = new JTree(
			new RepositoryTreeModel(sequenceType, repositoryManager)
		);
		tree.setCellRenderer(new RepositoryTreeRenderer());
		tree.setRootVisible(false);
		tree.setExpandsSelectedPaths(true);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION
		);
		tree.getModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesInserted(final TreeModelEvent e) {
				final TreePath path = e.getTreePath().pathByAddingChild(e.getChildren()[0]);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tree.setSelectionPath(path);
						
						tpData.setSelectedIndex(tpData.indexOfTab(tabTitle));
					}
				});
			}
			
			@Override
			public void treeNodesChanged(TreeModelEvent e) {}
			@Override
			public void treeNodesRemoved(TreeModelEvent e) {}
			@Override
			public void treeStructureChanged(TreeModelEvent e) {}
		});
		
		return tree;
	}
	
	private final static class TextFileMouseListener extends MouseAdapter {
		private final CloseableJTabbedPane tabbedPane;
		private final Map<File, TextFileViewer> views;
		private final Map<File, Thread> watchers;
		
		public TextFileMouseListener(CloseableJTabbedPane tabbedPane) {
			this.tabbedPane = tabbedPane;
			this.views = Collections.synchronizedMap(new HashMap<File, TextFileViewer>());
			this.watchers = Collections.synchronizedMap(new HashMap<File, Thread>());
			
			this.tabbedPane.addTabCloseListener(new TabCloseAdapter() {
				@Override
				public void tabClosing(TabCloseEvent event) {
					TextFileMouseListener.this.removeView(event.getTabIndex());
				}
			});
		}
		
		private void removeView(int index) {
			synchronized (this.tabbedPane) {
				final Component component = this.tabbedPane.getComponentAt(index);
				
				for (Map.Entry<File, TextFileViewer> viewer : this.views.entrySet()) {
					if (viewer.getValue().equals(component)) {
						this.views.remove(viewer.getKey());
						this.watchers.remove(viewer.getKey()).interrupt();
						break;
					}
				}
			}
		}

		private TextFileViewer addView(final File file, final Icon icon, final String tabName) {
			synchronized (this.tabbedPane) {
				if (!this.views.containsKey(file)) {
					
					try {
						final TextFileViewer viewer = new TextFileViewer(file);
						this.tabbedPane.addTab(tabName, icon, viewer);
						this.views.put(file, viewer);
						
						this.watchers.put(file, FileWatcher.watchFile(file, new Watcher() {
							@Override
							public boolean fileModified() {
								if (JOptionPane.showConfirmDialog(
									tabbedPane,
									String.format(
										"The file '%s' has been updated. "
										+ "Do you want to reload the related view?",
									tabName),
									"File updated",
									JOptionPane.YES_NO_OPTION
								) == JOptionPane.YES_OPTION) {
									try {
										final int index = tabbedPane.indexOfTab(tabName);
										final TextFileViewer view = new TextFileViewer(file);
										
										tabbedPane.setComponentAt(index, view);
										views.put(file, view);
									} catch (IOException e) {
										return false;
									}
								}
								
								return true;
							}
						}));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(
							this.tabbedPane, 
							"Error reading data file in file '" + file.getAbsolutePath() + "': " + e.getMessage(), 
							"Error Reading File", 
							JOptionPane.ERROR_MESSAGE
						);
						return null;
					} catch (IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(
							this.tabbedPane, 
							"Invalid or missing file: " + file.getAbsolutePath(), 
							"Invalid File", 
							JOptionPane.ERROR_MESSAGE
						);
						return null;
					}
				}
				
				return this.views.get(file);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && e.getSource() instanceof JTree) {
				final JTree tree = (JTree) e.getSource();
				
				final TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
				
				final Object value = path.getLastPathComponent();
				if (value instanceof TextFileMutableTreeNode) {
					final TextFileMutableTreeNode<?> node = 
						(TextFileMutableTreeNode<?>) value;
					
					final String tabName = node.getUserObject().toString();
					
					final TextFileViewer view = addView(
						node.getFile(), node.getIcon(), tabName
					);
					if (view != null)
						this.tabbedPane.setSelectedComponent(view);
				}
			}
		}
	}
}
