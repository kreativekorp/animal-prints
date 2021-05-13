package com.kreative.acpattern.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RGBArrayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BORDER_COLOR = Color.lightGray;
	private static final Dimension SWATCH_SIZE = new Dimension(16, 16);
	
	public RGBArrayPanel(int[] rgb, String[] labels) {
		super(new GridLayout(1, 0, -1, -1));
		for (int i = 0; i < rgb.length; i++) {
			JLabel p = new JLabel();
			p.setOpaque(true);
			p.setForeground(contrast(rgb[i]));
			p.setBackground(new Color(rgb[i], true));
			p.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
			p.setMinimumSize(SWATCH_SIZE);
			p.setPreferredSize(SWATCH_SIZE);
			p.setMaximumSize(SWATCH_SIZE);
			if (labels == null) p.setToolTipText(hex(rgb[i]));
			else p.setToolTipText(labels[i] + " " + hex(rgb[i]));
			add(p);
		}
	}
	
	private static String hex(int rgb) {
		return "#" + Integer.toHexString(rgb | 0xFF000000).substring(2).toUpperCase();
	}
	
	private static Color contrast(int rgb) {
		if (rgb < 0) {
			int r = (rgb >> 16) & 0xFF;
			int g = (rgb >>  8) & 0xFF;
			int b = (rgb >>  0) & 0xFF;
			int k = r * 30 + g * 59 + b * 11;
			return (k < 12750) ? Color.white : Color.black;
		} else {
			return Color.black;
		}
	}
}
