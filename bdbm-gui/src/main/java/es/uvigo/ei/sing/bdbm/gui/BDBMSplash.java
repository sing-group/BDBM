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

package es.uvigo.ei.sing.bdbm.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class BDBMSplash extends JFrame {
	private static final long serialVersionUID = 1L;
	private final static ImageIcon IMAGE_SPLASH = new ImageIcon(BDBMSplash.class.getResource("images/splash.png"));
	private final static ImageIcon IMAGE_LOADING = new ImageIcon(BDBMSplash.class.getResource("images/loader.gif"));
	
	public BDBMSplash() {
		super();
		
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JLayeredPane contentPane = new JLayeredPane();
		contentPane.setPreferredSize(
			new Dimension(IMAGE_SPLASH.getIconWidth(), IMAGE_SPLASH.getIconHeight())
		);
		
		final JLabel lblSplash = new JLabel(IMAGE_SPLASH);
		lblSplash.setBounds(0, 0, IMAGE_SPLASH.getIconWidth(), IMAGE_SPLASH.getIconHeight());
		
		final JLabel lblLoadingIcon = new JLabel("Loading database...", IMAGE_LOADING, JLabel.LEADING);
		lblLoadingIcon.setForeground(Color.WHITE);
		lblLoadingIcon.setFont(lblLoadingIcon.getFont().deriveFont(Font.BOLD));
		
		lblLoadingIcon.setBounds(
			4, IMAGE_SPLASH.getIconHeight() - IMAGE_LOADING.getIconHeight() - 4, 
			IMAGE_SPLASH.getIconWidth(), IMAGE_LOADING.getIconHeight()
		);
		contentPane.add(lblSplash, 0, 0);
		contentPane.add(lblLoadingIcon, 1, 1);
		
		this.setContentPane(contentPane);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
