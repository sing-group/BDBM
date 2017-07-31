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

package es.uvigo.ei.sing.bdbm.gui.command;

import java.awt.Color;
import java.awt.Window;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.yaacli.command.Command;

public class CommandExecutionDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOG = LoggerFactory.getLogger(CommandExecutionDialog.class);
	private final static ImageIcon ICON_LOADING = new ImageIcon(CommandExecutionDialog.class.getResource("images/loader.gif"));
	
	private final SwingWorker<Void, Void> worker;

	public CommandExecutionDialog(
		final Window owner, 
		final Command command,
		final ParameterValues parameters
	) {
		super(owner);
		
		this.setTitle("BDBM");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		final JLabel runningLabel = new JLabel("Executing operation...", ICON_LOADING, JLabel.LEADING);
		runningLabel.setBackground(Color.WHITE);
		runningLabel.setOpaque(true);
		runningLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		this.setContentPane(runningLabel);

		this.worker = new CommandSwingWorker(command, parameters);
	}
	
	public void startExecution() {
		this.setVisible(true);
		this.worker.execute();
	}
	
	private final class CommandSwingWorker extends SwingWorker<Void, Void> {
		private final Command command;
		private final ParameterValues parameters;

		private CommandSwingWorker(Command command, ParameterValues parameters) {
			this.command = command;
			this.parameters = parameters;
		}
		
		@Override
		protected void done() {
			CommandExecutionDialog.this.setVisible(false);
			CommandExecutionDialog.this.dispose();
			
			try {
				worker.get();
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Error executing command", e);
				
				JOptionPane.showMessageDialog(
					CommandExecutionDialog.this.getOwner(), 
					"Error executing command: " + e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE
				);
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			command.execute(parameters);

			return null;
		}
	}
}
