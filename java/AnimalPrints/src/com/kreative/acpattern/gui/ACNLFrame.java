package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.kreative.acpattern.ACNLFile;

public class ACNLFrame extends JFrame implements Updatable {
	private static final long serialVersionUID = 1L;
	
	private final ACNLFile acnl;
	private final ACHeader header;
	private final JLabel[] qrLabel;
	private final JLabel[] pblLabel;
	private final JLabel imgLabel;
	private final PropertiesPanel props;
	
	public ACNLFrame(String title, ACNLFile acnl) {
		super(title);
		this.acnl = acnl;
		this.header = new ACHeader(acnl);
		final int n = acnl.getFrames();
		this.qrLabel = new JLabel[n];
		this.pblLabel = new JLabel[n];
		for (int i = 0; i < n; i++) {
			this.qrLabel[i] = new JLabel((ImageIcon)null);
			this.pblLabel[i] = new JLabel((ImageIcon)null);
		}
		this.imgLabel = new JLabel((ImageIcon)null);
		this.props = new PropertiesPanel();
		
		JTabbedPane tabs = new JTabbedPane();
		for (int i = 0; i < n; i++) {
			String t = (n == 1) ? "QR Code" : ("QR #" + (i+1));
			tabs.addTab(t, qrLabel[i]);
		}
		for (int i = 0; i < n; i++) {
			String t = (n == 1) ? "Paint by Letters" : ("PBL #" + (i+1));
			tabs.addTab(t, pblLabel[i]);
		}
		tabs.addTab("Image", imgLabel);
		tabs.addTab("Properties", props);
		update();
		
		JPanel main = new JPanel(new BorderLayout());
		main.add(header, BorderLayout.PAGE_START);
		main.add(tabs, BorderLayout.CENTER);
		
		setContentPane(main);
		setJMenuBar(new ACMenuBar(this, acnl));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
	
	@Override
	public void update() {
		header.update();
		for (int i = 0, n = acnl.getFrames(); i < n; i++) {
			qrLabel[i].setIcon(new ImageIcon(acnl.getQRCard(i).getCardImage()));
			pblLabel[i].setIcon(new ImageIcon(acnl.getPBLCard(i).getCardImage()));
		}
		imgLabel.setIcon(new ImageIcon(getImage2x()));
		props.setProperties(
			"Title", acnl.getTitle(),
			"Creator ID", hexAndDec(acnl.getCreatorID(), 4),
			"Creator Name", acnl.getCreatorName(),
			"Gender", hexAndDec(acnl.getCreatorGender(), 2),
			"Reserved 1", hexAndDec(acnl.getCreatorReserved(), 2),
			"Town ID", hexAndDec(acnl.getTownID(), 4),
			"Town Name", acnl.getTownName(),
			"Language", hexAndDec(acnl.getTownLanguage(), 2),
			"Reserved 2", hexAndDec(acnl.getTownReserved(), 2),
			"Country", hexAndDec(acnl.getTownCountry(), 2),
			"Region", hexAndDec(acnl.getTownRegion(), 2),
			"Palette",
			new RGBArrayPanel(
				acnl.getPaletteRGB(new int[15]),
				hexArray(acnl.getPaletteGlobalIndex(new int[15]), 2)
			),
			"Unknown B", hexAndDec(acnl.getUnknownB(), 2),
			"Unknown C", hexAndDec(acnl.getUnknownC(), 2),
			"Format", hexAndDecAndFormatString(acnl.getFormat()),
			"Unknown D", hexAndDec(acnl.getUnknownD(), 4),
			"QR Parity", hexAndDec(acnl.getQRParity(), 2)
		);
	}
	
	private BufferedImage getImage() {
		if (acnl.getFrames() > 1) {
			int w64 = acnl.getWidth();
			int w32 = acnl.getFrames() * 40 - 8;
			int w = Math.max(w64, w32);
			int h = acnl.getHeight() + 40;
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(acnl.getImage(), null, (w - w64) / 2, 0);
			for (int x = (w - w32) / 2, i = 0, n = acnl.getFrames(); i < n; i++, x += 40) {
				g.drawImage(acnl.getFrameImage(i), null, x, h - 32);
			}
			g.dispose();
			return image;
		} else {
			return acnl.getImage();
		}
	}
	
	private BufferedImage getImage2x() {
		BufferedImage image = getImage();
		int w = image.getWidth() * 2;
		int h = image.getHeight() * 2;
		BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scaled.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		return scaled;
	}
	
	private static String[] hexArray(int[] vals, int len) {
		String[] ha = new String[vals.length];
		for (int i = 0; i < vals.length; i++) {
			String h = Integer.toHexString(vals[i]).toUpperCase();
			while (h.length() < len) h = "0" + h;
			ha[i] = "$" + h;
		}
		return ha;
	}
	
	private static String hexAndDec(int val, int len) {
		String h = Integer.toHexString(val).toUpperCase();
		while (h.length() < len) h = "0" + h;
		return h + " (" + val + ")";
	}
	
	private static String hexAndDecAndFormatString(int format) {
		String s1 = hexAndDec(format, 2);
		String s2 = formatString(format);
		if (s2 != null) s1 += " (" + s2 + ")";
		return s1;
	}
	
	private static String formatString(int format) {
		switch (format) {
			case ACNLFile.DRESS_LONG_SLEEVE: return "Long-Sleeve Dress";
			case ACNLFile.DRESS_SHORT_SLEEVE: return "Short-Sleeve Dress";
			case ACNLFile.DRESS_SLEEVELESS: return "Sleeveless Dress";
			case ACNLFile.SHIRT_LONG_SLEEVE: return "Long-Sleeve Shirt";
			case ACNLFile.SHIRT_SHORT_SLEEVE: return "Short-Sleeve Shirt";
			case ACNLFile.SHIRT_SLEEVELESS: return "Sleeveless Shirt";
			case ACNLFile.HAT_HORNED: return "Horned Hat";
			case ACNLFile.HAT_HORNLESS: return "Hornless Hat";
			case ACNLFile.STANDEE: return "Standee";
			case ACNLFile.CANVAS: return "Canvas";
			default: return null;
		}
	}
}
