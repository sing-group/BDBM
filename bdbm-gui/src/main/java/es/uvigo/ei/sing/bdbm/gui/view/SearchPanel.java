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

package es.uvigo.ei.sing.bdbm.gui.view;

import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class SearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JTextArea textArea;
	
	private final JTextField txtSearch;
	private final JRadioButton rbExactMatch;
	private final JRadioButton rbIgnoreCase;
	private final JRadioButton rbRegularExpression;
	private final Highlighter.HighlightPainter highlightPainter;
	private final Highlighter.HighlightPainter highlightPainterSelected;
	
	private final JButton btnNext;
	private final JButton btnPrevious;
	
	private final LinkedList<FoundLocation> foundLocations;
	private FoundLocation currentLocation;

	public SearchPanel(JTextArea textArea) {
		this.textArea = textArea;
		
		this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
		this.highlightPainterSelected = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
		
		this.foundLocations = new LinkedList<>();
		
		final JLabel lblSearch = new JLabel("Find:");
		this.txtSearch = new JTextField();
		final JButton btnSearch = new JButton("Search");
		
		this.rbExactMatch = new JRadioButton("Exact match", true);
		this.rbIgnoreCase = new JRadioButton("Ignore case", false);
		this.rbRegularExpression = new JRadioButton("Regular expression", false);
		
		final ButtonGroup matchGroup = new ButtonGroup();
		matchGroup.add(this.rbExactMatch);
		matchGroup.add(this.rbIgnoreCase);
		matchGroup.add(this.rbRegularExpression);
		
		this.btnNext = new JButton("Next");
		this.btnNext.setEnabled(false);
		this.btnPrevious = new JButton("Previous");
		this.btnPrevious.setEnabled(false);
		final JButton btnClear = new JButton("Clear");
		this.txtSearch.setColumns(12);
		
		final JPanel matchPanel = new JPanel(new GridLayout(3, 1));
		matchPanel.setBorder(createCompoundBorder(
			createEmptyBorder(8, 4, 8, 4),
			createTitledBorder("Search options")
		));
		
		matchPanel.add(this.rbExactMatch);
		matchPanel.add(this.rbIgnoreCase);
		matchPanel.add(this.rbRegularExpression);
		
		final GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(lblSearch, gbc);
		
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		this.add(this.txtSearch, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		this.add(matchPanel, gbc);
		
		gbc.gridy++;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		this.add(btnSearch, gbc);
		
		gbc.gridx = 2;
		this.add(btnClear, gbc);
		
		gbc.gridy++;
		gbc.gridx = 1;
		this.add(btnPrevious, gbc);
		
		gbc.gridx = 2;
		this.add(btnNext, gbc);
		
		final ActionListener alSearch = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSearch();
			}
		};
		
		txtSearch.addActionListener(alSearch);
		btnSearch.addActionListener(alSearch);
		
		rbExactMatch.addActionListener(alSearch);
		rbIgnoreCase.addActionListener(alSearch);
		rbRegularExpression.addActionListener(alSearch);
		
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearSearch();
			}
		});
		
		btnNext.addActionListener(new MoveToLocationActionListener() {
			@Override
			protected boolean isThereAnyMore() {
				return isCurrentTheLastLocation();
			}
			
			@Override
			protected FoundLocation popLocation() {
				return popNextLocation();
			}
		});
		
		btnPrevious.addActionListener(new MoveToLocationActionListener() {
			@Override
			protected boolean isThereAnyMore() {
				return isCurrentTheFirstLocation();
			}
			
			@Override
			protected FoundLocation popLocation() {
				return popPreviousLocation();
			}
		});
		
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (!isShowing())
					clearSearch();
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
	}

	public void clearSearch() {
		this.txtSearch.setText("");
		this.textArea.getHighlighter().removeAllHighlights();
		
		this.btnNext.setEnabled(false);
		this.btnPrevious.setEnabled(false);
	}
	
	public void updateSearch() {
		this.textArea.getHighlighter().removeAllHighlights();
		this.foundLocations.clear();
		this.currentLocation = null;
		
		String textToFind = txtSearch.getText().trim();
		
		if (!textToFind.isEmpty()) {
			String text = textArea.getText();
			
			if (this.rbRegularExpression.isSelected()) {
				try {
					final Pattern pattern = Pattern.compile(textToFind);
					this.txtSearch.setBackground(Color.WHITE);
					
					final Matcher matcher = pattern.matcher(text);
					
					while (matcher.find()) {
						final FoundLocation location = new FoundLocation(
							matcher.start(), matcher.end()
						);
						this.foundLocations.add(location);
						
						this.changeHighlightPainter(location, highlightPainter);
					}
				} catch (PatternSyntaxException pse) {
					this.txtSearch.setBackground(Color.RED);
				}
			} else {
				if (this.rbIgnoreCase.isSelected()) {
					text = text.toUpperCase();
					textToFind = textToFind.toUpperCase();
				}
				
				final int textToFindLength = textToFind.length();
				
				int index = 0;
				while ((index = text.indexOf(textToFind, index)) != -1) {
					final FoundLocation location = new FoundLocation(
						index, index + textToFindLength
					);
					this.foundLocations.add(location);
					
					this.changeHighlightPainter(location, highlightPainter);
					
					index += textToFindLength + 1;
				}
			}
		}
		
		this.moveToLocation(this.popNextLocation());
		
		if (this.btnNext.isEnabled())
			this.btnNext.grabFocus();
	}
	
	private FoundLocation popNextLocation() {
		if (this.foundLocations.isEmpty()) {
			return null;
		} else if (this.isCurrentTheLastLocation()) {
			this.currentLocation = null;
			
			return null;
		} else {
			final int indexOfCurrentLocation = this.foundLocations.indexOf(this.currentLocation);
			this.currentLocation = this.foundLocations.get(indexOfCurrentLocation + 1);
			
			return this.currentLocation;
		}
	}
	
	private FoundLocation popPreviousLocation() {
		if (this.foundLocations.isEmpty()) {
			return null;
		} else if (this.isCurrentTheFirstLocation()) {
			this.currentLocation = null;
			
			return null;
		} else {
			final int indexOfCurrentLocation = this.foundLocations.indexOf(this.currentLocation);
			this.currentLocation = this.foundLocations.get(indexOfCurrentLocation - 1);
			
			return this.currentLocation;
		}
	}
	
	private boolean isCurrentTheLastLocation() {
		if (this.foundLocations.isEmpty()) {
			return true;
		} else if (this.currentLocation == null) {
			return false;
		} else {
			return this.foundLocations.getLast().equals(this.currentLocation);
		}
	}
	
	private boolean isCurrentTheFirstLocation() {
		if (this.foundLocations.isEmpty()) {
			return true;
		} else if (this.currentLocation == null) {
			return false;
		} else {
			return this.foundLocations.getFirst().equals(this.currentLocation);
		}
	}
	
	private void moveToLocation(FoundLocation location) {
		this.moveToLocation(null, location);
	}
	
	private void moveToLocation(FoundLocation from, FoundLocation to) {
		if (foundLocations.isEmpty()) {
			// Inconsistent state
		} else {
			if (from != null) {
				this.changeHighlightPainter(from, highlightPainter);
			}
			
			if (to != null) {
				this.changeHighlightPainter(to, highlightPainterSelected);
				
                textArea.setCaretPosition(to.getStart());
                textArea.moveCaretPosition(to.getEnd());
			}
		}
		
		btnPrevious.setEnabled(!isCurrentTheFirstLocation());
		btnNext.setEnabled(!isCurrentTheLastLocation());
	}
	
	private void changeHighlightPainter(
		FoundLocation location,
		Highlighter.HighlightPainter painter
	) {
		try {
			final Highlighter highlighter = textArea.getHighlighter();
			
			if (location.getHighlightMark() != null)
				highlighter.removeHighlight(location.getHighlightMark());
			
	        final Object highlightMark = highlighter.addHighlight(
	    		location.getStart(), location.getEnd(), painter
			);
	        
	        location.setHighlightMark(highlightMark);
		} catch (BadLocationException ble) {
			throw new RuntimeException(ble);
		}
	}
	
	private abstract class MoveToLocationActionListener
	implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (foundLocations.isEmpty()) {
				// Inconsistent state
			} else if (isThereAnyMore()) {
				// Inconsistent state
			} else {
				moveToLocation(currentLocation, popLocation());
			}
		}
		
		protected abstract boolean isThereAnyMore();
		protected abstract FoundLocation popLocation();
	}

	private static class FoundLocation {
		private final int start, end;
		private Object highlightMark;

		public FoundLocation(int start, int end) {
			this.start = start;
			this.end = end;
			this.highlightMark = null;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
		
		public Object getHighlightMark() {
			return highlightMark;
		}
		
		public void setHighlightMark(Object highlightMark) {
			this.highlightMark = highlightMark;
		}
	}
}
