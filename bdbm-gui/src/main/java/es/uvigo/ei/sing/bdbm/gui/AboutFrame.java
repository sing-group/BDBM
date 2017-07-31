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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class AboutFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private static final int FRAME_WIDTH = 360;
	private static final int LINE_HEIGHT = 15;
	private static final int IMAGE_HEIGHT = 86;
	
	public final static ImageIcon IMAGE_ABOUT = new ImageIcon(AboutFrame.class.getResource("images/About.png"));
	public final static ImageIcon IMAGE_MEG = new ImageIcon(AboutFrame.class.getResource("images/IBMC.png"));
	public final static ImageIcon IMAGE_SING = new ImageIcon(AboutFrame.class.getResource("images/SING.png"));
	
	private final static String LINK_MEG = "http://evolution.ibmc.up.pt/";
	private final static String LINK_SING = "http://sing.ei.uvigo.es";

	public AboutFrame(Frame parent) {
		super(parent, "About", false);
		this.setIconImage(IMAGE_ABOUT.getImage());
		
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.white);
		panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
		
		final JLabel lblImageMEG = new JLabel(AboutFrame.IMAGE_MEG, SwingConstants.CENTER);
		final JLabel lblTitleMEG, lblLinkMEG;
		final JLabel lblImageSING = new JLabel(AboutFrame.IMAGE_SING, SwingConstants.CENTER);
		final JLabel lblTitleSING, lblLinkSING;

		final JLabel[] megLabels = new JLabel[] {
			lblTitleMEG = new JLabel("Molecular Evolution Group", SwingConstants.CENTER),
			new JLabel("Instituto de Biologia Molecular e Celular", SwingConstants.CENTER),
			new JLabel("Rua do Campo Alegre, 823", SwingConstants.CENTER),
			new JLabel("4150-180 Porto", SwingConstants.CENTER),
			new JLabel("Portugal", SwingConstants.CENTER),
			lblLinkMEG = new JLabel(AboutFrame.LINK_MEG)
		};
		
		final JLabel[] singLabels = new JLabel[] {
			lblTitleSING = new JLabel("SING Group", SwingConstants.CENTER),
			new JLabel("Informatics Department", SwingConstants.CENTER),
			new JLabel("Higher Technical School of Computer Engineering", SwingConstants.CENTER),
			new JLabel("University of Vigo at Ourense Campus", SwingConstants.CENTER),
			new JLabel("32004 Ourense", SwingConstants.CENTER),
			new JLabel("Spain", SwingConstants.CENTER),
			lblLinkSING = new JLabel(AboutFrame.LINK_SING)
		};
		
		lblLinkMEG.setHorizontalAlignment(SwingConstants.CENTER);
		lblLinkMEG.setFocusable(false);
		lblLinkSING.setHorizontalAlignment(SwingConstants.CENTER);
		lblLinkSING.setFocusable(false);

		fixSize(lblImageMEG, AboutFrame.FRAME_WIDTH, AboutFrame.IMAGE_HEIGHT);
		panel.add(lblImageMEG);
		for (JLabel label : megLabels) {
			fixSize(label, FRAME_WIDTH, LINE_HEIGHT);
			panel.add(label);
		}
		
		panel.add(Box.createVerticalStrut(20));
		
		fixSize(lblImageSING, AboutFrame.FRAME_WIDTH, AboutFrame.IMAGE_HEIGHT);
		panel.add(lblImageSING);
		for (JLabel label : singLabels) {
			fixSize(label, FRAME_WIDTH, LINE_HEIGHT);
			panel.add(label);
		}
		
		this.setContentPane(panel);
		
		lblTitleMEG.setFont(lblTitleMEG.getFont().deriveFont(Font.BOLD));
		lblTitleSING.setFont(lblTitleSING.getFont().deriveFont(Font.BOLD));
		
		lblLinkMEG.setForeground(Color.BLUE);
		lblLinkSING.setForeground(Color.BLUE);
		lblLinkMEG.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLinkSING.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		lblImageMEG.addMouseListener(new URLLinkAction(AboutFrame.LINK_MEG));
		lblImageSING.addMouseListener(new URLLinkAction(AboutFrame.LINK_SING));
		
		lblLinkMEG.addMouseListener(new URLLinkAction(AboutFrame.LINK_MEG));
		lblLinkSING.addMouseListener(new URLLinkAction(AboutFrame.LINK_SING));
		
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	private final static void fixSize(Component component, int width, int height) {
		if (component == null || (height <= 0 && width <= 0)) return;
		
		Dimension size = new Dimension(
			(width <= 0)?component.getWidth():width,
			(height <= 0)?component.getHeight():height		
		);
		
		component.setPreferredSize(size);
		component.setMinimumSize(size);
		component.setMaximumSize(size);
		component.setSize(size);
	}
	
	private final class URLLinkAction extends MouseAdapter {
		private final String url;

		public URLLinkAction(String url) {
			this.url = url;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException | URISyntaxException ex) {
					JOptionPane.showConfirmDialog(
						AboutFrame.this,
						"Sorry, but the URL couldn't be opened.",
						"URL Error",
						JOptionPane.OK_OPTION,
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		}
	}
}
