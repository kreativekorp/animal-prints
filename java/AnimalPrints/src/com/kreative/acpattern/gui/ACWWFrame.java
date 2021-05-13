package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.kreative.acpattern.ACWWFile;

public class ACWWFrame extends JFrame implements Updatable {
	private static final long serialVersionUID = 1L;
	
	private final ACWWFile acww;
	private final ACHeader header;
	private final JLabel pblLabel;
	private final JLabel imgLabel;
	private final PropertiesPanel props;
	
	public ACWWFrame(String title, ACWWFile acww) {
		super(title);
		this.acww = acww;
		this.header = new ACHeader(acww);
		this.pblLabel = new JLabel((ImageIcon)null);
		this.imgLabel = new JLabel((ImageIcon)null);
		this.props = new PropertiesPanel();
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Paint by Letters", pblLabel);
		tabs.addTab("Image", imgLabel);
		tabs.addTab("Properties", props);
		update();
		
		JPanel main = new JPanel(new BorderLayout());
		main.add(header, BorderLayout.PAGE_START);
		main.add(tabs, BorderLayout.CENTER);
		
		setContentPane(main);
		setJMenuBar(new ACMenuBar(this, acww));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
	
	@Override
	public void update() {
		header.update();
		pblLabel.setIcon(new ImageIcon(acww.getPBLCard().getCardImage()));
		imgLabel.setIcon(new ImageIcon(getImage2x()));
		props.setProperties(
			"Town ID", hexAndDec(acww.getTownID(), 4),
			"Town Name", acww.getTownName(),
			"Creator ID", hexAndDec(acww.getCreatorID(), 4),
			"Creator Name", acww.getCreatorName(),
			"Unknown A", hexAndDec(acww.getUnknownA(), 4),
			"Title", acww.getTitle(),
			"Palette", (acww.getPalette()+1) + "/16",
			null, new RGBArrayPanel(getPaletteRGB(acww.getPalette()), null),
			"Unknown B", hexAndDec(acww.getUnknownB(), 1),
			"Unknown C", hexAndDec(acww.getUnknownC(), 2)
		);
	}
	
	private BufferedImage getImage2x() {
		BufferedImage image = acww.getImage();
		int w = image.getWidth() * 2;
		int h = image.getHeight() * 2;
		BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaled.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		return scaled;
	}
	
	private static String hexAndDec(int val, int len) {
		String h = Integer.toHexString(val).toUpperCase();
		while (h.length() < len) h = "0" + h;
		return h + " (" + val + ")";
	}
	
	private static int[] getPaletteRGB(int palette) {
		int[] rgb = new int[15];
		for (int i = 0; i < 15; i++) {
			rgb[i] = ACWWFile.getPaletteColorRGB(palette, i + 1);
		}
		return rgb;
	}
}
