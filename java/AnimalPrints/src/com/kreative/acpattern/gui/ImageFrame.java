package com.kreative.acpattern.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class ImageFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public ImageFrame(String title, BufferedImage image) {
		super(title);
		
		final JLabel label = new JLabel(new ImageIcon(image));
		final JScrollPane pane = new JScrollPane(
			label,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		);
		final MouseAdapter listener = new MouseAdapter() {
			private Point origin;
			@Override
			public void mousePressed(MouseEvent e) {
				origin = new Point(e.getPoint());
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (origin == null) return;
				int dx = origin.x - e.getX();
				int dy = origin.y - e.getY();
				JViewport vp = pane.getViewport();
				Rectangle vr = vp.getViewRect();
				vr.x += dx;
				vr.y += dy;
				label.scrollRectToVisible(vr);
			}
		};
		label.addMouseListener(listener);
		label.addMouseMotionListener(listener);
		
		setContentPane(pane);
		setJMenuBar(new ACMenuBar(this, image));
		setSize(500, 500);
		setLocationRelativeTo(null);
	}
}
