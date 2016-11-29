/*
 * #%L
 * BDBM GUI
 * %%
 * Copyright (C) 2014 - 2016 Miguel Reboiro-Jato, Critina P. Vieira, Hugo López-Fdez, Noé Vázquez González, Florentino Fdez-Riverola and Jorge Vieira
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
package es.uvigo.ei.sing.bdbm.gui.component;

import static java.util.Objects.requireNonNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class IndexesJTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	private Color backgroundOk;
	private Color background;
	
	public IndexesJTextField() {
		super();
		this.init();
	}

	public IndexesJTextField(String text) {
		super(text);
		this.init();
	}

	public IndexesJTextField(int columns) {
		super(columns);
		this.init();
	}

	public IndexesJTextField(String text, int columns) {
		super(text, columns);
		this.init();
	}

	public IndexesJTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		this.init();
	}

	private void init() {
		this.backgroundOk = Color.decode("#DFF0D8");
		this.background = this.getBackground();
		this.setOpaque(true);
		this.addTextListener();
		this.updateBackground();
	}
	
	private void addTextListener() {
		this.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateBackground();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateBackground();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateBackground();
			}
		});
	}
	
	public void setBackgroundOk(Color backgroundOk) {
		requireNonNull(backgroundOk, "backgroundOk color can't be null");
		
		this.backgroundOk = backgroundOk;
	}
	
	public Color getBackgroundOk() {
		return backgroundOk;
	}
	
	@Override
	public void setBackground(Color background) {
		requireNonNull(background, "background color can't be null");
		
		if (background.equals(this.backgroundOk))
			throw new IllegalArgumentException("background color must be different from backgroundOk");
		
		this.updateBackground();
		
		this.background = background;
	}

	public Color getDefaultBackground() {
		return this.background;
	}
	
	protected void updateBackground() {
		if (this.isValidIndexesExpression()) {
			super.setBackground(this.backgroundOk);
		} else {
			super.setBackground(this.background);
		}
		
		repaint();
	}
	
	public boolean isValidIndexesExpression() {
		if (this.getDocument() == null) return false;
		
		final String regex = "[1-9][0-9]*(-[1-9][0-9]*)?(,([1-9][0-9]*(-[1-9][0-9]*)?))*";
		final String text = this.getText();
		
		return text != null && text.trim().matches(regex);
	}
	
	public List<String> getIndexesList() {
		final int[] indexes = this.getIndexes();
		final List<String> indexesValues = new ArrayList<>(indexes.length);
		
		for (int index : indexes) {
			indexesValues.add(Integer.toString(index));
		}
		
		return indexesValues;
	}
	
	public int[] getIndexes() {
		final String text = this.getText().trim();
		
		if (this.isValidIndexesExpression()) {
			final String[] parts = text.split(",");
			
			final SortedSet<String> indexes = new TreeSet<>();
			for (String part : parts) {
				if (part.contains("-")) {
					final String[] minMax = part.split("-");
					final int min = Integer.parseInt(minMax[0]);
					final int max = Integer.parseInt(minMax[1]);
					
					if (min >= max) {
						throw new IllegalArgumentException("Invalid range in indexes list: " + part);
					} else {
						for (int i = min; i <= max; i++) {
							indexes.add(Integer.toString(i - 1));
						}
					}
				} else {
					final int index = Integer.parseInt(part) - 1;
					indexes.add(Integer.toString(index));
				}
			}
			
			final int[] indexesArray = new int[indexes.size()];
			int i = 0;
			for (String index : indexes) {
				indexesArray[i++] = Integer.parseInt(index);
			}
			
			return indexesArray;
		} else {
			throw new IllegalArgumentException("Invalid indexes list format: " + text);
		}
	}
}
