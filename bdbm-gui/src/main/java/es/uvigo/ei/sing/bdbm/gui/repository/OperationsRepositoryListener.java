/*
 * #%L
 * BDBM GUI
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
package es.uvigo.ei.sing.bdbm.gui.repository;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import es.uvigo.ei.sing.bdbm.cli.commands.BLASTDBAliasToolCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.GetORFCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.MakeBLASTDBCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.ReformatFastaCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.RetrieveSearchEntryCommand;
import es.uvigo.ei.sing.bdbm.cli.commands.converters.FileOption;
import es.uvigo.ei.sing.bdbm.controller.BDBMController;
import es.uvigo.ei.sing.bdbm.environment.SequenceType;
import es.uvigo.ei.sing.bdbm.gui.BDBMGUIController;
import es.uvigo.ei.sing.bdbm.gui.command.BDBMCommandAction;
import es.uvigo.ei.sing.bdbm.gui.command.CommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.BLASTDBAliasToolCommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.GetORFCommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.MakeBLASTDBCommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.ReformatFastaCommandDialog;
import es.uvigo.ei.sing.bdbm.gui.command.dialogs.RetrieveSearchEntryCommandDialog;
import es.uvigo.ei.sing.bdbm.gui.repository.RepositoryTreeModel.TextFileMutableTreeNode;
import es.uvigo.ei.sing.bdbm.persistence.entities.Database;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export;
import es.uvigo.ei.sing.bdbm.persistence.entities.Fasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.NucleotideFasta;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SequenceEntity;
import es.uvigo.ei.sing.bdbm.persistence.entities.Export.ExportEntry;
import es.uvigo.ei.sing.bdbm.persistence.entities.SearchEntry.Query;
import es.uvigo.ei.sing.yaacli.command.Command;
import es.uvigo.ei.sing.yaacli.command.option.Option;
import es.uvigo.ei.sing.yaacli.command.parameter.DefaultParameters;
import es.uvigo.ei.sing.yaacli.command.parameter.MultipleParameterValue;
import es.uvigo.ei.sing.yaacli.command.parameter.ParameterValue;
import es.uvigo.ei.sing.yaacli.command.parameter.Parameters;
import es.uvigo.ei.sing.yaacli.command.parameter.SingleParameterValue;

public class OperationsRepositoryListener extends MouseAdapter {
	private final BDBMGUIController controller;
	
	public OperationsRepositoryListener(BDBMGUIController controller) {
		this.controller = controller;
	}

	protected static Parameters singletonParameters(
		Option<?> option, String value
	) {
		final Map<Option<?>, ParameterValue<?>> values = 
			new HashMap<Option<?>, ParameterValue<?>>();
		
		values.put(option, new SingleParameterValue(value));
		
		return new DefaultParameters(values, false);
	}

	protected static Parameters singletonParameters(
		Option<?> option, List<String> value
	) {
		final Map<Option<?>, ParameterValue<?>> values = 
			new HashMap<Option<?>, ParameterValue<?>>();
		
		values.put(option, new MultipleParameterValue(value));
		
		return new DefaultParameters(values, false);
	}
	
	protected Map<Option<?>, ParameterValue<?>> createEntityParams(
		SequenceEntity entity,
		Option<SequenceType> typeOption,
		FileOption fileOption
	) {
		final Map<Option<?>, ParameterValue<?>> parameters = new HashMap<>();
		
		parameters.put(
			typeOption,
			new SingleParameterValue(entity.getType().name())
		);
		parameters.put(
			fileOption,
			new SingleParameterValue(entity.getBaseFile().getAbsolutePath())
		);
		
		return parameters;
	}
	
	protected BDBMCommandAction createBDBMCommandAction(
		Class<? extends Command> commandClass,
		Class<? extends CommandDialog> commandDialogClass,
		Parameters parameters
	) {
		try {
			final BDBMController bdbmController = this.controller.getController();
			
			return new BDBMCommandAction(
				bdbmController,
				commandClass.getConstructor(BDBMController.class).newInstance(bdbmController),
				commandDialogClass,
				parameters
			);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid command class " + commandClass.getName(), e);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (e.getSource() instanceof JTree) {
				final JTree tree = (JTree) e.getSource();
				
				final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					tree.setSelectionPath(path);
					
					if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
						final DefaultMutableTreeNode node = 
							(DefaultMutableTreeNode) path.getLastPathComponent();
						
						if (node.getUserObject() instanceof Fasta) {
							final Fasta fasta = (Fasta) node.getUserObject();
							
							final DefaultParameters makedbFastaParameters = new DefaultParameters(createEntityParams(
								fasta,
								MakeBLASTDBCommand.OPTION_DB_TYPE,
								MakeBLASTDBCommand.OPTION_INPUT
							));							
							final DefaultParameters reformatFastaParameters = new DefaultParameters(createEntityParams(
								fasta,
								ReformatFastaCommand.OPTION_FASTA_TYPE,
								ReformatFastaCommand.OPTION_FASTA
							));
							
							final Action[] actions;
							if (fasta instanceof NucleotideFasta) {
								actions = new Action[] {
									this.createBDBMCommandAction(
										MakeBLASTDBCommand.class, 
										MakeBLASTDBCommandDialog.class,
										makedbFastaParameters
									),
									this.createBDBMCommandAction(
										GetORFCommand.class, 
										GetORFCommandDialog.class, 
										singletonParameters(
											GetORFCommand.OPTION_FASTA, 
											fasta.getFile().getAbsolutePath()
										)
									),
									this.createBDBMCommandAction(
										ReformatFastaCommand.class,
										ReformatFastaCommandDialog.class,
										reformatFastaParameters
									)
								};
							} else {
								actions = new Action[] {
									this.createBDBMCommandAction(
										MakeBLASTDBCommand.class, 
										MakeBLASTDBCommandDialog.class,
										makedbFastaParameters
									),
									this.createBDBMCommandAction(
										ReformatFastaCommand.class,
										ReformatFastaCommandDialog.class,
										reformatFastaParameters
									)
								};
							}
							
							this.showPopupMenu(
								"Fasta", 
								"fasta", 
								tree, 
								fasta, 
								e.getX(), 
								e.getY(),
								actions
							);
						} else if (node.getUserObject() instanceof Database) {
							final Database database = (Database) node.getUserObject();
							
							final BDBMCommandAction retrieveCA;
	
							this.showPopupMenu(
								"Database", 
								"database", 
								tree, 
								database, 
								e.getX(), 
								e.getY(), 
								this.createBDBMCommandAction(
									BLASTDBAliasToolCommand.class, 
									BLASTDBAliasToolCommandDialog.class,
									singletonParameters(
										BLASTDBAliasToolCommand.OPTION_DATABASES, 
										Arrays.asList(database.getBaseFile().getAbsolutePath())
									)
								),
								retrieveCA = this.createBDBMCommandAction(
									RetrieveSearchEntryCommand.class,
									RetrieveSearchEntryCommandDialog.class,
									new DefaultParameters(createEntityParams(
										database, 
										RetrieveSearchEntryCommand.OPTION_DB_TYPE, 
										RetrieveSearchEntryCommand.OPTION_DATABASE
									))
								)
							);
							
							retrieveCA.addParamValue(boolean.class, this.controller.isAccessionInferEnabled());
						} else if (node.getUserObject() instanceof SearchEntry) {
							final SearchEntry searchEntry = (SearchEntry) node.getUserObject();
							
							this.showPopupMenu(
								"Search Entry", "search entry", tree, searchEntry, 
								e.getX(), e.getY()
							);
						} else if (node.getUserObject() instanceof Query) {
							final Query searchEntry = (Query) node.getUserObject();
							
							this.showPopupMenu(
								"Query", "query", tree, searchEntry, 
								e.getX(), e.getY()
							);
						} else if (node.getUserObject() instanceof Export) {
							final Export export = (Export) node.getUserObject();
							
							this.showPopupMenu(
								"Database Export", "database export", tree, export, 
								e.getX(), e.getY()
							);
						} else if (node.getUserObject() instanceof ExportEntry) {
							final ExportEntry entry = (ExportEntry) node.getUserObject();
							
							this.showPopupMenu(
								"Database Entry", "database entry", tree, entry, 
								e.getX(), e.getY()
							);
						} else if (node instanceof TextFileMutableTreeNode) {
							final File file = ((TextFileMutableTreeNode<?>) node).getFile();
							
							if (file.isFile()) {
								this.showPopupMenu(
									"File", tree, file, 
									e.getX(), e.getY()
								);
							}
						}
					}
				}
			}
		}
	}
	
	protected void showPopupMenu(
		String title, 
		String entityName, 
		Component parent, 
		SequenceEntity entity,
		int x, int y,
		Action ... additionalActions
	) {
		final JPopupMenu menu = new JPopupMenu(title);
		final JMenuItem itemTitle = new JMenuItem(title);
		itemTitle.setEnabled(false);
		itemTitle.setFont(itemTitle.getFont().deriveFont(Font.BOLD));
		itemTitle.setForeground(Color.BLACK);
		menu.add(itemTitle);
		menu.addSeparator();
		
		if (additionalActions.length > 0) {
			for (Action additionalAction : additionalActions) {
				menu.add(additionalAction);
			}
			
			menu.addSeparator();
		}

		final Window parentWindow = SwingUtilities.getWindowAncestor(parent);
		
		menu.add(new ExportToAction(parentWindow, entity, entityName));
		menu.add(new DeleteAction(
			this.controller.getController(), 
			parentWindow, 
			entity, 
			entityName
		));
		
		menu.show(parent, x, y);
	}
	
	protected void showPopupMenu(
		String title, 
		Component parent, 
		File file,
		int x, int y
	) {
		final JPopupMenu menu = new JPopupMenu(title);
		final JMenuItem itemTitle = new JMenuItem(title);
		itemTitle.setEnabled(false);
		itemTitle.setFont(itemTitle.getFont().deriveFont(Font.BOLD));
		itemTitle.setForeground(Color.BLACK);
		menu.add(itemTitle);
		menu.addSeparator();
		
		final Window parentWindow = SwingUtilities.getWindowAncestor(parent);
		
		menu.add(new ExportToAction(parentWindow, file, "File"));
		
		menu.show(parent, x, y);
	}
	
	protected static class ExportToAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private final File file;
		private final String entityName;
		private final Component parent;

		private ExportToAction(Component parent, SequenceEntity entity, String entityName) {
			this(parent, entity.getBaseFile(), entityName);
		}

		private ExportToAction(Component parent, File file, String entityName) {
			super("Export " + entityName + " to...");
			this.file = file;
			this.entityName = entityName;
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Export " + this.entityName + " to...");
			fileChooser.setMultiSelectionEnabled(false);
			
			if (this.file.isDirectory()) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
			
			if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				try {
					if (this.file.isDirectory()) {
						FileUtils.copyDirectory(this.file, fileChooser.getSelectedFile());
					} else {
						FileUtils.copyFile(this.file, fileChooser.getSelectedFile());
					}
					
					JOptionPane.showMessageDialog(
						parent,
						this.entityName + " was correctly exported to: " + this.file.getAbsolutePath() + ".",
						"Export Finished",
						JOptionPane.INFORMATION_MESSAGE
					);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(
						parent,
						"Error while exporting " + this.entityName + ": " + ioe.getMessage(),
						"Export Error",
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}

	protected static class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private final BDBMController controller;
		private final Component parent;
		private final SequenceEntity sequenceEntity;
		private final String entityName;

		private DeleteAction(
			BDBMController controller, 
			Component parent, 
			SequenceEntity sequenceEntity, 
			String entityName
		) {
			super("Delete " + entityName.toLowerCase());
			
			this.controller = controller;
			this.parent = parent;
			this.sequenceEntity = sequenceEntity;
			this.entityName = entityName;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final String lowerEntityName = this.entityName.toLowerCase();
			
			if (JOptionPane.showConfirmDialog(
				parent, 
				this.entityName + " will be deleted. Do you want to continue?",
				"Delete " + lowerEntityName,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			) == JOptionPane.YES_OPTION) {
				try {
					this.controller.delete(sequenceEntity);
					JOptionPane.showMessageDialog(
						this.parent, 
						this.entityName + " deleted",
						"Delete " + lowerEntityName,
						JOptionPane.INFORMATION_MESSAGE
					);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(
						parent, 
						"Error deleting " + lowerEntityName,
						"Delete " + lowerEntityName,
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}
}
