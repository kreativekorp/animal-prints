package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.acpattern.ACBaseFile;

public class ACHeader extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JLabel imageLabel = icon("empty64.png");
	private final JLabel titleLabel = new JLabel("Design Pattern");
	private final JLabel creatorLabel = new JLabel("Unknown");
	private final JLabel townLabel = new JLabel("Unknown");
	private ACBaseFile source = null;
	
	public ACHeader() {
		JPanel iconPanel = column(icon("graphics.png"), icon("person.png"), icon("home.png"));
		JPanel labelPanel = column(titleLabel, creatorLabel, townLabel);
		
		JPanel iconLabelPanel = new JPanel(new BorderLayout(4, 4));
		iconLabelPanel.add(iconPanel, BorderLayout.LINE_START);
		iconLabelPanel.add(labelPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout(20, 20));
		add(imageLabel, BorderLayout.LINE_START);
		add(iconLabelPanel, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
	}
	
	public ACHeader(ACBaseFile source) {
		this();
		setSource(source);
	}
	
	public void setSource(ACBaseFile source) {
		this.source = source;
		update();
	}
	
	public void update() {
		if (source == null) {
			imageLabel.setIcon(new ImageIcon(ACHeader.class.getResource("empty64.png")));
			titleLabel.setText("Design Pattern");
			creatorLabel.setText("Unknown");
			townLabel.setText("Unknown");
		} else {
			imageLabel.setIcon(new ImageIcon(image64(source.getImage())));
			titleLabel.setText(source.getTitle());
			creatorLabel.setText(source.getCreatorName());
			townLabel.setText(source.getTownName());
		}
	}
	
	private static BufferedImage image64(BufferedImage image) {
		if (image.getWidth() == 64 && image.getHeight() == 64) return image;
		BufferedImage scaled = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaled.createGraphics();
		g.drawImage(image, 0, 0, 64, 64, null);
		g.dispose();
		return scaled;
	}
	
	private static JPanel column(Component... cc) {
		JPanel p = new JPanel(new GridLayout(0, 1, 2, 2));
		for (Component c : cc) p.add(c);
		return p;
	}
	
	private static JLabel icon(String name) {
		return new JLabel(new ImageIcon(ACHeader.class.getResource(name)));
	}
}
