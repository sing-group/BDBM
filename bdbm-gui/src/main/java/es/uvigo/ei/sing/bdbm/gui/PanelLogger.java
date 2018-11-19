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

package es.uvigo.ei.sing.bdbm.gui;

import static es.uvigo.ei.sing.bdbm.gui.command.input.FileInputComponentBuilder.getCurrentDirectory;
import static javax.swing.JFileChooser.APPROVE_OPTION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import es.uvigo.ei.sing.bdbm.log.ExecutionObservableAppender;

public class PanelLogger extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	private final JTextArea taLogger;
	
	private Action copySelectedTextAction;
	private Action exportLogToFileAction;
	private Action clearLogAction;

	public PanelLogger(ExecutionObservableAppender appender) {
		super(new BorderLayout());
		
		this.taLogger = new JTextArea();
		this.taLogger.setCaretColor(Color.BLACK);
		this.taLogger.setOpaque(true);
		this.taLogger.setBackground(Color.BLACK);
		this.taLogger.setForeground(Color.WHITE);
		this.taLogger.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		this.taLogger.setEditable(false);
		this.taLogger.setComponentPopupMenu(getTextAreaPopupMenu());
		
		final JScrollPane scrollPane = new JScrollPane(this.taLogger);
		scrollPane.setBackground(Color.BLACK);
		this.add(scrollPane, BorderLayout.CENTER);
		
		appender.addObserver(this);
	}

  private JPopupMenu getTextAreaPopupMenu() {
    JPopupMenu menu = new JPopupMenu() {
      private static final long serialVersionUID = 1L;

      @Override
      public void setVisible(boolean b) {
        checkActionsState();
        super.setVisible(b);
      }
    };

    menu.add(getCopySelectedTextAction());
    menu.add(getExportLogToFileAction());
    menu.add(new JSeparator());
    menu.add(getClearLogAction());

    return menu;
  }

  private void checkActionsState() {
    this.copySelectedTextAction
      .setEnabled(this.taLogger.getSelectedText() != null && !this.taLogger.getSelectedText().isEmpty());
    this.exportLogToFileAction.setEnabled(this.taLogger.getText() != null && !this.taLogger.getText().isEmpty());
  }

  private Action getCopySelectedTextAction() {
    if (this.copySelectedTextAction == null) {
      this.copySelectedTextAction = new AbstractAction("Copy selection") {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          copySelectedText();
        }
      };
    }
    return this.copySelectedTextAction;
  }

  private void copySelectedText() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection selection = new StringSelection(taLogger.getSelectedText());
    clipboard.setContents(selection, selection);
  }

  private Action getExportLogToFileAction() {
    if (this.exportLogToFileAction == null) {
      this.exportLogToFileAction = new AbstractAction("Export log to file") {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          exportLogToFile();
        }
      };
    }
    return this.exportLogToFileAction;
  }

  private void exportLogToFile() {
    JFileChooser fileChooser = new JFileChooser(new File("."));
    File currentDirectory = getCurrentDirectory();
    if (currentDirectory != null) {
      fileChooser.setCurrentDirectory(currentDirectory);
    }

    if (fileChooser.showOpenDialog(this) == APPROVE_OPTION) {
      Path outputFile = fileChooser.getSelectedFile().toPath();

      try {
        Files.write(outputFile, this.taLogger.getText().getBytes());
      } catch (IOException e) {
        JOptionPane.showMessageDialog(
          this,
          "There was an error writing the log to " + outputFile.toFile().getAbsolutePath(),
          "Error",
          JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  private Action getClearLogAction() {
    if (this.clearLogAction == null) {
      this.clearLogAction = new AbstractAction("Clear log") {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          clearLog();
        }
      };
    }
    return this.clearLogAction;
  }
  
  private void clearLog() {
    this.taLogger.setText("");
    this.taLogger.setCaretPosition(this.taLogger.getText().length());
  }

  @Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (this.taLogger != null)
			this.taLogger.setBackground(bg);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (this.taLogger != null)
			this.taLogger.setForeground(fg);
	}
	
	@Override
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(isOpaque);
		if (this.taLogger != null)
			this.taLogger.setOpaque(isOpaque);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			this.taLogger.append(((String) arg) + '\n');
			this.taLogger.setCaretPosition(this.taLogger.getText().length());
		}
	}
}
