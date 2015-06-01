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
package es.uvigo.ei.sing.bdbm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import es.uvigo.ei.sing.bdbm.log.ExecutionObservableAppender;

public class PanelLogger extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	private final JTextArea taLogger;

	public PanelLogger(ExecutionObservableAppender appender) {
		super(new BorderLayout());
		
		this.taLogger = new JTextArea();
		this.taLogger.setCaretColor(Color.BLACK);
		this.taLogger.setOpaque(true);
		this.taLogger.setBackground(Color.BLACK);
		this.taLogger.setForeground(Color.WHITE);
		this.taLogger.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		this.taLogger.setEditable(false);
		
		final JScrollPane scrollPane = new JScrollPane(this.taLogger);
		scrollPane.setBackground(Color.BLACK);
		this.add(scrollPane, BorderLayout.CENTER);
		
		appender.addObserver(this);
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
