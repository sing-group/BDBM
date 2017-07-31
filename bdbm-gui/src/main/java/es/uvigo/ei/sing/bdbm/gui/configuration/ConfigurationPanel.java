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

package es.uvigo.ei.sing.bdbm.gui.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uvigo.ei.sing.bdbm.gui.BDBMGUIController;

public class ConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final BDBMGUIController controller;
	
	private final JTextField txtRepository;
	private final JTextField txtBLAST;
	private final JTextField txtEMBOSS;
	private final JTextField txtBedTools;
	private final JTextField txtSplign;
	private final JTextField txtCompart;
	private final JButton btnBuildRepository;

	public ConfigurationPanel(BDBMGUIController controller) {
		super();
		
		this.controller = controller;
		
//		this.setPreferredSize(new Dimension(600, 140));
		
		final GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		this.setLayout(layout);
		
		final JLabel lblRepository = new JLabel("Repository Path");
		final JLabel lblBLAST = new JLabel("BLAST Path");
		final JLabel lblEMBOSS = new JLabel("EMBOSS Path");
		final JLabel lblBedTools = new JLabel("BedTools Path");
		final JLabel lblSplign = new JLabel("Splign Path");
		final JLabel lblCompart = new JLabel("Compart Path");
		
		final File repositoryPath = this.controller.getEnvironment()
			.getRepositoryPaths().getBaseDirectory();
		final File blastBD = this.controller.getEnvironment()
			.getBLASTBinaries().getBaseDirectory();
		final File embossBD = this.controller.getEnvironment()
			.getEMBOSSBinaries().getBaseDirectory();
		final File bedToolsBD = this.controller.getEnvironment()
			.getBedToolsBinaries().getBaseDirectory();
		final File splignBD = this.controller.getEnvironment()
			.getSplignBinaries().getBaseDirectory();
		final File compartBD = this.controller.getEnvironment()
			.getCompartBinaries().getBaseDirectory();
		
		this.txtRepository = new JTextField(repositoryPath.getAbsolutePath());
		this.txtBLAST = new JTextField(blastBD == null ? "" : blastBD.getAbsolutePath());
		this.txtEMBOSS = new JTextField(embossBD == null ? "" : embossBD.getAbsolutePath());
		this.txtBedTools = new JTextField(bedToolsBD == null ? "" : bedToolsBD.getAbsolutePath());
		this.txtSplign = new JTextField(splignBD == null ? "" : splignBD.getAbsolutePath());
		this.txtCompart = new JTextField(compartBD == null ? "" : compartBD.getAbsolutePath());
		
		this.txtRepository.setEditable(false);
		this.txtBLAST.setEditable(false);
		this.txtEMBOSS.setEditable(false);
		this.txtBedTools.setEditable(false);
		this.txtSplign.setEditable(false);
		this.txtCompart.setEditable(false);
		
		final JButton btnRepository = new JButton("Select...");
		final JButton btnBLASTSelect = new JButton("Select...");
		final JButton btnEMBOSSSelect = new JButton("Select...");
		final JButton btnBedToolsSelect = new JButton("Select...");
		final JButton btnSplignSelect = new JButton("Select...");
		final JButton btnCompartSelect = new JButton("Select...");
		
		final JButton btnBLASTInPath = new JButton("In system path");
		final JButton btnEMBOSSInPath = new JButton("In system path");
		final JButton btnBedToolsInPath = new JButton("In system path");
		final JButton btnSplignInPath = new JButton("In system path");
		final JButton btnCompartInPath = new JButton("In system path");
		
		this.btnBuildRepository = new JButton(new AbstractAction("Build") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationPanel.this.buildRepository();
			}
		});
		this.btnBuildRepository.setEnabled(false);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(lblRepository, Alignment.CENTER)
				.addComponent(this.txtRepository)
				.addComponent(btnRepository)
				.addComponent(this.btnBuildRepository)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(lblBLAST, Alignment.CENTER)
				.addComponent(this.txtBLAST)
				.addComponent(btnBLASTSelect)
				.addComponent(btnBLASTInPath)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(lblEMBOSS, Alignment.CENTER)
				.addComponent(this.txtEMBOSS)
				.addComponent(btnEMBOSSSelect)
				.addComponent(btnEMBOSSInPath)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(lblBedTools, Alignment.CENTER)
				.addComponent(this.txtBedTools)
				.addComponent(btnBedToolsSelect)
				.addComponent(btnBedToolsInPath)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(lblSplign, Alignment.CENTER)
				.addComponent(this.txtSplign)
				.addComponent(btnSplignSelect)
				.addComponent(btnSplignInPath)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(lblCompart, Alignment.CENTER)
				.addComponent(this.txtCompart)
				.addComponent(btnCompartSelect)
				.addComponent(btnCompartInPath)
			)
		);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup()
				.addComponent(lblRepository)
				.addComponent(lblBLAST)
				.addComponent(lblEMBOSS)
				.addComponent(lblBedTools)
				.addComponent(lblSplign)
				.addComponent(lblCompart)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(this.txtRepository)
				.addComponent(this.txtBLAST)
				.addComponent(this.txtEMBOSS)
				.addComponent(this.txtBedTools)
				.addComponent(this.txtSplign)
				.addComponent(this.txtCompart)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(btnRepository)
				.addComponent(btnBLASTSelect)
				.addComponent(btnEMBOSSSelect)
				.addComponent(btnBedToolsSelect)
				.addComponent(btnSplignSelect)
				.addComponent(btnCompartSelect)
			)
			.addGroup(layout.createParallelGroup()
				.addComponent(this.btnBuildRepository)
				.addComponent(btnBLASTInPath)
				.addComponent(btnEMBOSSInPath)
				.addComponent(btnBedToolsInPath)
				.addComponent(btnSplignInPath)
				.addComponent(btnCompartInPath)
			)
		);
		
		final Callable<Boolean> callbackRepositorySelection = new Callable<Boolean>() {
			@Override
			public Boolean call() {
				if (ConfigurationPanel.this.isValidRepositoryPath()) {
					btnBuildRepository.setEnabled(false);
				} else {
					btnBuildRepository.setEnabled(true);
					
					if (JOptionPane.showConfirmDialog(
						ConfigurationPanel.this, 
						"Repository path does not exist or its structure is incomplete. Do you wish to build repository structure?",
						"Invalid Repository",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE
					) == JOptionPane.YES_OPTION) {
						ConfigurationPanel.this.buildRepository();
					}
				}
				
				return true;
			}
		};
		btnRepository.addActionListener(
			new PathSelectionActionListener(this.txtRepository, callbackRepositorySelection)
		);
		
		final Callable<Boolean> callbackCheckBLAST = new Callable<Boolean>() {
			@Override
			public Boolean call() {
				if (ConfigurationPanel.this.isValidBLASTPath()) {
					return true;
				} else {
					JOptionPane.showMessageDialog(
						ConfigurationPanel.this, 
						"Invalid BLAST binaries path. Please, change the selected path",
						"Invalid Path",
						JOptionPane.ERROR_MESSAGE
					);
					
					return false;
				}
			}
		};
		btnBLASTSelect.addActionListener(
			new PathSelectionActionListener(this.txtBLAST, callbackCheckBLAST)
		);
		btnBLASTInPath.addActionListener(
			new SystemPathSelectionActionListener(this.txtBLAST, callbackCheckBLAST)
		);
		
		final Callable<Boolean> callbackCheckEMBOSS = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (ConfigurationPanel.this.isValidEMBOSSPath()) {
					return true;
				} else {
					JOptionPane.showMessageDialog(
						ConfigurationPanel.this, 
						"Invalid EMBOSS binaries path. Please, change the selected path",
						"Invalid Path",
						JOptionPane.ERROR_MESSAGE
					);
					
					return false;
				}
			}
		};
		btnEMBOSSSelect.addActionListener(
			new PathSelectionActionListener(this.txtEMBOSS, callbackCheckEMBOSS)
		);
		btnEMBOSSInPath.addActionListener(
			new SystemPathSelectionActionListener(this.txtEMBOSS, callbackCheckEMBOSS)
		);
		
		final Callable<Boolean> callbackCheckBedTools = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (ConfigurationPanel.this.isValidBedToolsPath()) {
					return true;
				} else {
					JOptionPane.showMessageDialog(
						ConfigurationPanel.this, 
						"Invalid bedtools binaries path. Please, change the selected path",
						"Invalid Path",
						JOptionPane.ERROR_MESSAGE
					);
					
					return false;
				}
			}
		};
		btnBedToolsSelect.addActionListener(
			new PathSelectionActionListener(this.txtBedTools, callbackCheckBedTools)
		);
		btnBedToolsInPath.addActionListener(
			new SystemPathSelectionActionListener(this.txtBedTools, callbackCheckBedTools)
		);
		
		final Callable<Boolean> callbackCheckSplign = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (ConfigurationPanel.this.isValidSplignPath()) {
					return true;
				} else {
					JOptionPane.showMessageDialog(
						ConfigurationPanel.this, 
						"Invalid splign binaries path. Please, change the selected path",
						"Invalid Path",
						JOptionPane.ERROR_MESSAGE
					);
					
					return false;
				}
			}
		};
		btnSplignSelect.addActionListener(
			new PathSelectionActionListener(this.txtSplign, callbackCheckSplign)
		);
		btnSplignInPath.addActionListener(
			new SystemPathSelectionActionListener(this.txtSplign, callbackCheckSplign)
		);
		
		final Callable<Boolean> callbackCheckCompart = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				if (ConfigurationPanel.this.isValidCompartPath()) {
					return true;
				} else {
					JOptionPane.showMessageDialog(
						ConfigurationPanel.this, 
						"Invalid compart binaries path. Please, change the selected path",
						"Invalid Path",
						JOptionPane.ERROR_MESSAGE
					);
					
					return false;
				}
			}
		};
		btnCompartSelect.addActionListener(
			new PathSelectionActionListener(this.txtCompart, callbackCheckCompart)
		);
		btnCompartInPath.addActionListener(
			new SystemPathSelectionActionListener(this.txtCompart, callbackCheckCompart)
		);
	}

	public void addConfigurationChangeListener(ConfigurationChangeEventListener listener) {
		this.listenerList.add(ConfigurationChangeEventListener.class, listener);
	}
	
	public void removeConfigurationChangeListener(ConfigurationChangeEventListener listener) {
		this.listenerList.remove(ConfigurationChangeEventListener.class, listener);
	}
	
	protected void fireChangeEvent(ConfigurationChangeEvent event) {
		final ConfigurationChangeEventListener[] listeners = 
			this.listenerList.getListeners(ConfigurationChangeEventListener.class);
		
		for (ConfigurationChangeEventListener listener : listeners) {
			listener.configurationChanged(event);
		}
	}
	
	protected void fireChange() {
		this.fireChangeEvent(new ConfigurationChangeEvent(this));
	}

	protected File getRepositoryDirectory() {
		return new File(this.txtRepository.getText());
	}

	protected String getBLASTPath() {
		return this.txtBLAST.getText().isEmpty() ?
			null : new File(this.txtBLAST.getText()).getAbsolutePath();
	}
	
	protected String getEMBOSSPath() {
		return this.txtEMBOSS.getText().isEmpty() ?
			null : new File(this.txtEMBOSS.getText()).getAbsolutePath();
	}
	
	protected String getBedToolsPath() {
		return this.txtBedTools.getText().isEmpty() ?
			null : new File(this.txtBedTools.getText()).getAbsolutePath();
	}
	
	protected String getSplignPath() {
		return this.txtSplign.getText().isEmpty() ?
			null : new File(this.txtSplign.getText()).getAbsolutePath();
	}
	
	protected String getCompartPath() {
		return this.txtCompart.getText().isEmpty() ?
			null : new File(this.txtCompart.getText()).getAbsolutePath();
	}
	
	public boolean isValidRepositoryPath() {
		return this.controller.getEnvironment()
			.getRepositoryPaths()
			.checkBaseDirectory(getRepositoryDirectory());
	}
	
	public boolean isValidBLASTPath() {
		return this.controller.getManager().checkBLASTPath(getBLASTPath());
	}
	
	protected boolean isValidEMBOSSPath() {
		return this.controller.getManager().checkEMBOSSPath(getEMBOSSPath());
	}

	protected boolean isValidBedToolsPath() {
		return this.controller.getManager().checkBedToolsPath(getBedToolsPath());
	}
	
	protected boolean isValidSplignPath() {
		return this.controller.getManager().checkSplignPath(getSplignPath());
	}
	
	protected boolean isValidCompartPath() {
		return this.controller.getManager().checkCompartPath(getCompartPath());
	}
	
	protected void buildRepository() {
		try {
			this.controller.getEnvironment()
				.getRepositoryPaths()
			.buildBaseDirectory(this.getRepositoryDirectory());
			
			this.btnBuildRepository.setEnabled(false);
			JOptionPane.showMessageDialog(
				ConfigurationPanel.this, 
				"Repository structure was correctly built.",
				"Repository Built",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			this.fireChange();
		} catch (Exception e) {
			this.btnBuildRepository.setEnabled(true);
			
			JOptionPane.showMessageDialog(
				ConfigurationPanel.this, 
				"Error building repository. Please, check path and press 'Build' or change path",
				"Repository Building Error",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	public PathsConfiguration getConfiguration() {
		if (this.isValidRepositoryPath() && this.isValidBLASTPath()) {
			final String blastPath = this.getBLASTPath();
			final String embossPath = this.getEMBOSSPath();
			final String bedToolsPath = this.getBedToolsPath();
			final String splignPath = this.getSplignPath();
			final String compartPath = this.getCompartPath();
			
			return new PathsConfiguration(
				this.getRepositoryDirectory(), 
				blastPath == null ? null : new File(blastPath),
				embossPath == null ? null : new File(embossPath),
				bedToolsPath == null ? null : new File(bedToolsPath),
				splignPath == null ? null : new File(splignPath),
				compartPath == null ? null : new File(compartPath)
			);
		} else {
			return null;
		}
	}
	
	private final class SystemPathSelectionActionListener implements
			ActionListener {
		private final JTextField txtAssociated;
		private final Callable<Boolean> callback;

		private SystemPathSelectionActionListener(JTextField txtAssociated, Callable<Boolean> callback) {
			this.txtAssociated = txtAssociated;
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final String previousPath = this.txtAssociated.getText();
			this.txtAssociated.setText("");
			
			try {
				if (this.callback.call()) {
					ConfigurationPanel.this.fireChange();
				} else {
					txtAssociated.setText(previousPath);
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private final class PathSelectionActionListener implements ActionListener {
		private final JTextField txtAssociated;
		private final Callable<Boolean> callback;

		private PathSelectionActionListener(JTextField txtAssociated, Callable<Boolean> callback) {
			this.txtAssociated = txtAssociated;
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser chooser = new JFileChooser(
				new File(txtAssociated.getText())
			);
			
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			
			if (chooser.showOpenDialog(ConfigurationPanel.this) == JFileChooser.APPROVE_OPTION) {
				final String previousPath = txtAssociated.getText();
				txtAssociated.setText(chooser.getSelectedFile().getAbsolutePath());
				
				try {
					if (this.callback.call()) {
						ConfigurationPanel.this.fireChange();
					} else {
						txtAssociated.setText(previousPath);
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}
		}
	}

	public static class ConfigurationChangeEvent extends EventObject {
		private static final long serialVersionUID = 1L;
		
		private final PathsConfiguration configuration;

		protected ConfigurationChangeEvent(ConfigurationPanel panel) {
			this(panel, panel.getConfiguration());
		}
		
		public ConfigurationChangeEvent(Object source, PathsConfiguration configuration) {
			super(source);
			this.configuration = configuration;
		}
		
		public PathsConfiguration getConfiguration() {
			return configuration;
		}
	}
	
	public static interface ConfigurationChangeEventListener extends EventListener {
		public void configurationChanged(ConfigurationChangeEvent event);
	}
}
