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
package es.uvigo.ei.sing.bdbm.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import say.swing.JFontChooser;

public class TextFileViewer extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_CHARS_LOADED = 1_000_000;

	private final JTextArea textArea;
	private final JFontChooser fontChooser;
	private final SearchPanel searchPanel;
	private final JDialog searchDialog;
	
	private final File file;

	private final long fileSize;
	
	public TextFileViewer(File file) throws IOException {
		super(new BorderLayout());
		
		this.file = file;
		
		// TEXT AREA
		final InitialRead initialRead = TextFileViewer.loadFile(file);
		this.fileSize = initialRead.getSize();
		
		this.textArea = new JTextArea(initialRead.getText());
		this.textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		this.textArea.setFont(new Font(
			Font.MONOSPACED,
			Font.PLAIN,
			this.textArea.getFont().getSize()
		));
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		this.textArea.setEditable(false);
		
		
		// SEARCH PANEL AND DIALOG
		this.searchPanel = new SearchPanel(this.textArea);
		this.searchPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		final JPanel searchPanelContainer = new JPanel(new BorderLayout());
		
		final JPanel searchPanelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JButton btnCloseSearch = new JButton("Close");
		searchPanelButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		
		searchPanelButtons.add(btnCloseSearch);
		
		searchPanelContainer.add(this.searchPanel, BorderLayout.CENTER);
		searchPanelContainer.add(searchPanelButtons, BorderLayout.SOUTH);

		this.searchDialog = new JDialog(
			SwingUtilities.getWindowAncestor(this),
			"Search",
			ModalityType.MODELESS
		);
		this.searchDialog.setContentPane(searchPanelContainer);
		this.searchDialog.pack();
		this.searchDialog.setSize(
			this.searchDialog.getWidth() + 100,
			this.searchDialog.getHeight()
		);
		this.searchDialog.setLocationRelativeTo(this);
		
		
		// OPTIONS PANEL
		final JPanel panelOptions = new JPanel(new BorderLayout());
		final JPanel panelOptionsWest = new JPanel(new FlowLayout());
		final JPanel panelOptionsEast = new JPanel(new FlowLayout());
		final JCheckBox chkLineWrap = new JCheckBox("Line wrap", true);
		final JButton btnChangeFont = new JButton("Change Font");
		
		final JButton btnSearch = new JButton("Search...");
		
		panelOptionsWest.add(btnChangeFont);
		panelOptionsWest.add(chkLineWrap);
		
		panelOptionsEast.add(btnSearch);
		
		panelOptions.add(panelOptionsWest, BorderLayout.WEST);
		panelOptions.add(panelOptionsEast, BorderLayout.EAST);
		
		this.fontChooser = new JFontChooser();
		
		this.add(new JScrollPane(this.textArea), BorderLayout.CENTER);
		this.add(panelOptions, BorderLayout.NORTH);
		
		if (this.fileSize > MAX_CHARS_LOADED) {
			this.add(new LocationPanel(), BorderLayout.SOUTH);
		}
		
		chkLineWrap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setLineWrap(chkLineWrap.isSelected());
			}
		});
		
		btnChangeFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFont();
			}
		});
		
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchDialog.setVisible(true);
			}
		});
		
		btnCloseSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchDialog.setVisible(false);
			}
		});
		
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (!TextFileViewer.this.isShowing()) {
					TextFileViewer.this.searchDialog.setVisible(false);
				}
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
	}
	
	private void updateSearch() {
		this.searchPanel.updateSearch();
	}
	
	public String getText() {
		return textArea.getText();
	}

	private void changeFont() {
		fontChooser.setSelectedFont(textArea.getFont());
		if (fontChooser.showDialog(TextFileViewer.this) == JFontChooser.OK_OPTION) {
			textArea.setFont(fontChooser.getSelectedFont());
		}
	}

	private final static InitialRead loadFile(File file) {
		if (!file.isFile()) {
			throw new IllegalArgumentException("file must be a text file: " + file.getAbsolutePath());
		}
		
		final StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			final char[] buffer = new char[4096];
			int readLength = 0;
			
			long readCount = 0l;
			int maxToRead = MAX_CHARS_LOADED;
			while (maxToRead > 0 && (readLength = br.read(buffer, 0, Math.min(buffer.length, maxToRead))) != -1) {
				sb.append(buffer, 0, readLength);
				
				maxToRead -= readLength;
				readCount += readLength;
			}
			
			if (readLength != -1) {
				while ((readLength = br.read(buffer)) != -1) {
					readCount += readLength;
				}
			}
			
			return new InitialRead(readCount, sb.toString());
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid file", e);
		}
	}
	
	private class LocationPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private final JLabel lblCurrentPosition;
		private final JButton btnPrevious;
		private final JButton btnNext;
		
		private long currentLocation;

		public LocationPanel() {
			super(new FlowLayout(FlowLayout.TRAILING));
			
			this.currentLocation = 0;
			final String zeroLocationText = locationToPercentageText(0);
			final String initialLocationText = locationToPercentageText(MAX_CHARS_LOADED);
			
			this.lblCurrentPosition = new JLabel(
				String.format("Current position: %s%%-%s%%", zeroLocationText, initialLocationText)
			);
			
			this.btnPrevious = new JButton(new AbstractAction("<") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					changeLocation(currentLocation - MAX_CHARS_LOADED);
				}
			});
			this.btnPrevious.setEnabled(false);
			this.btnPrevious.setToolTipText("Previous page");
			
			this.btnNext = new JButton(new AbstractAction(">") {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					changeLocation(currentLocation + MAX_CHARS_LOADED);
				}
			});
			this.btnNext.setToolTipText("Next page");
			
			final JButton btnSetPosition = new JButton(new AbstractAction("Set Position") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					final String oneHundredLocation = locationToPercentageText(fileSize);
					
					String input = JOptionPane.showInputDialog(
						TextFileViewer.this, 
						String.format("New Location (%s%%-%s%%)", zeroLocationText, oneHundredLocation), 
						locationToPercentageText(currentLocation)
					);
					
					if (input.endsWith("%")) {
						input = input.substring(0, input.length() - 1);
					}
					
					try {
						changeLocation(Math.round(Double.parseDouble(input)/100d * fileSize));
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(
							TextFileViewer.this,
							"Invalid position in file: " + input,
							"Invalid position",
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
			
			this.add(lblCurrentPosition);
			this.add(btnPrevious);
			this.add(btnNext);
			this.add(btnSetPosition);
		}
		
		private String locationToPercentageText(long location) {
			int precision = 0;
			
			int blockSize = MAX_CHARS_LOADED;
			
			while (fileSize / blockSize + ((fileSize%blockSize == 0)?0:1) > 100) {
				blockSize *= 10;
				precision++;
			}
			
			return String.format("%." + precision + "f", ((double) location / (double) fileSize) * 100);
		}

		private void changeLocation(long currentLocation) {
			this.currentLocation = Math.max(0, Math.min(currentLocation, fileSize - MAX_CHARS_LOADED));
			
			this.btnPrevious.setEnabled(this.currentLocation > 0);
			this.btnNext.setEnabled(this.currentLocation < fileSize - MAX_CHARS_LOADED);
			
			final String currentLocationText = locationToPercentageText(this.currentLocation);
			final String currentEndLocationText = locationToPercentageText(this.currentLocation + MAX_CHARS_LOADED);
			
			textArea.setText(loadFile(file, this.currentLocation, MAX_CHARS_LOADED));
			textArea.setCaretPosition(0);
			
			this.lblCurrentPosition.setText("Current position: " + currentLocationText + "%-" + currentEndLocationText + "%");
			
			updateSearch();
		}
	}
	
	private final static String loadFile(File file, long from, int charsToRead) {
		if (!file.isFile()) {
			throw new IllegalArgumentException("file must be a text file: " + file.getAbsolutePath());
		}
		
		final StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			final char[] buffer = new char[4096];
			int c;

			br.skip(from);
			while ((c = br.read(buffer)) != -1 && sb.length() < charsToRead) {
				sb.append(buffer,0,c);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.substring(0, Math.min(charsToRead, sb.length()));
	}
	
	private static class InitialRead {
		private final long size;
		private final String text;
		
		public InitialRead(long size, String text) {
			this.size = size;
			this.text = text;
		}

		public long getSize() {
			return size;
		}
		
		public String getText() {
			return text;
		}
	}
}
