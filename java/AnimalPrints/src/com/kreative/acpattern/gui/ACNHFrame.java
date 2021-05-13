package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import com.kreative.acpattern.ACNHFile;

public class ACNHFrame extends JFrame implements Updatable {
	private static final long serialVersionUID = 1L;
	
	private final ACNHFile acnh;
	private final ACHeader header;
	private final JLabel[] pblLabel;
	private final JLabel imgLabel;
	private final PropertiesPanel props;
	
	public ACNHFrame(String title, ACNHFile acnh) {
		super(title);
		this.acnh = acnh;
		this.header = new ACHeader(acnh);
		final int n = acnh.getFrames();
		this.pblLabel = new JLabel[n];
		for (int i = 0; i < n; i++) {
			this.pblLabel[i] = new JLabel((ImageIcon)null);
		}
		this.imgLabel = new JLabel((ImageIcon)null);
		this.props = new PropertiesPanel();
		
		JTabbedPane tabs = new JTabbedPane();
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
		setJMenuBar(new ACMenuBar(this, acnh));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
	
	@Override
	public void update() {
		header.update();
		for (int i = 0, n = acnh.getFrames(); i < n; i++) {
			pblLabel[i].setIcon(new ImageIcon(acnh.getPBLCard(i).getCardImage()));
		}
		imgLabel.setIcon(new ImageIcon(getImage2x()));
		props.setProperties(
			"Checksum", hexAndDec(acnh.getChecksum(), 8),
			"Unknown A", hexAndDec(acnh.getUnknownA(), 8),
			"Unknown B", hexAndDec(acnh.getUnknownB(), 8),
			"Unknown C", hexAndDec(acnh.getUnknownC(), 8),
			"Title", acnh.getTitle(),
			"Island ID", hexAndDec(acnh.getTownID(), 8),
			"Island Name", acnh.getTownName(),
			"Unknown D", hexAndDec(acnh.getUnknownD(), 8),
			"Creator ID", hexAndDec(acnh.getCreatorID(), 8),
			"Creator Name", acnh.getCreatorName(),
			"Unknown E", hexAndDec(acnh.getUnknownE(), 8),
			"Unknown F", hexAndDec(acnh.getUnknownF(), 8),
			"Unknown G", hexAndDec(acnh.getUnknownG(), 8),
			"Palette",
			new RGBArrayPanel(
				acnh.getPaletteRGB(new int[15]),
				hvbArray(acnh.getPaletteRGB(new int[15]))
			),
			"Format", hexAndDecAndFormatString(acnh.getFormat()),
			"Unknown H", hexAndDec(acnh.getUnknownH(), 4)
		);
	}
	
	private BufferedImage getImage() {
		if (acnh.getFrames() > 1) {
			int w64 = acnh.getWidth();
			int w32 = acnh.getFrames() * 40 - 8;
			int w = Math.max(w64, w32);
			int h = acnh.getHeight() + 40;
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(acnh.getImage(), null, (w - w64) / 2, 0);
			for (int x = (w - w32) / 2, i = 0, n = acnh.getFrames(); i < n; i++, x += 40) {
				g.drawImage(acnh.getFrameImage(i), null, x, h - 32);
			}
			g.dispose();
			return image;
		} else {
			return acnh.getImage();
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
	
	private static String[] hvbArray(int[] rgb) {
		String[] sa = new String[rgb.length];
		for (int i = 0; i < rgb.length; i++) {
			int r = (rgb[i] >> 16) & 0xFF;
			int g = (rgb[i] >>  8) & 0xFF;
			int b = (rgb[i] >>  0) & 0xFF;
			int[] hvb = ACNHFile.RGBtoHVB(r, g, b, null, null);
			sa[i] = "H:" + (hvb[0]+1) + " V:" + (hvb[1]+1) + " B:" + (hvb[2]+1);
		}
		return sa;
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
			case ACNHFile.DESIGN_PATTERN: return "Design Pattern";
			case ACNHFile.PRO_PATTERN: return "Pro Pattern";
			case ACNHFile.DESIGN_TANK_TOP: return "Tank Top";
			case ACNHFile.PRO_LONG_SLEEVE_DRESS_SHIRT: return "Long-Sleeve Dress Shirt";
			case ACNHFile.PRO_SHORT_SLEEVE_TEE: return "Short-Sleeve Tee";
			case ACNHFile.PRO_TANK_TOP: return "Tank Top";
			case ACNHFile.PRO_SWEATER: return "Sweater";
			case ACNHFile.PRO_HOODIE: return "Hoodie";
			case ACNHFile.PRO_COAT: return "Coat";
			case ACNHFile.PRO_SHORT_SLEEVE_DRESS: return "Short-Sleeve Dress";
			case ACNHFile.PRO_SLEEVELESS_DRESS: return "Sleeveless Dress";
			case ACNHFile.PRO_LONG_SLEEVE_DRESS: return "Long-Sleeve Dress";
			case ACNHFile.PRO_BALLOON_HEM_DRESS: return "Balloon-Hem Dress";
			case ACNHFile.PRO_ROUND_DRESS: return "Round Dress";
			case ACNHFile.PRO_ROBE: return "Robe";
			case ACNHFile.PRO_BRIMMED_CAP: return "Brimmed Cap";
			case ACNHFile.PRO_KNIT_CAP: return "Knit Cap";
			case ACNHFile.PRO_BRIMMED_HAT: return "Brimmed Hat";
			case ACNHFile.IMPORTED_DRESS_SHORT_SLEEVE: return "Short-Sleeve Dress (Imported)";
			case ACNHFile.IMPORTED_DRESS_LONG_SLEEVE: return "Long-Sleeve Dress (Imported)";
			case ACNHFile.IMPORTED_DRESS_SLEEVELESS: return "Sleeveless Dress (Imported)";
			case ACNHFile.IMPORTED_SHIRT_SHORT_SLEEVE: return "Short-Sleeve Shirt (Imported)";
			case ACNHFile.IMPORTED_SHIRT_LONG_SLEEVE: return "Long-Sleeve Shirt (Imported)";
			case ACNHFile.IMPORTED_SHIRT_SLEEVELESS: return "Sleeveless Shirt (Imported)";
			case ACNHFile.IMPORTED_HAT_HORNLESS: return "Hornless Hat (Imported)";
			case ACNHFile.IMPORTED_HAT_HORNED: return "Horned Hat (Imported)";
			default: return null;
		}
	}
}
