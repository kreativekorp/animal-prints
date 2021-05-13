package com.kreative.acpattern;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class PBLCard {
	private static final int WIDTH = 560;
	private static final int HEIGHT = 480;
	private static final Color BACKGROUND_COLOR = new Color(0xFAFAFA, false);
	private static final Color HATCH_COLOR = new Color(0x99C08597, true);
	private static final BasicStroke HATCH_STROKE = new BasicStroke(16, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10);
	private static final int HATCH_SPACING = 22;
	private static final Color TITLE_BAR_COLOR = new Color(0x81375C, false);
	private static final Rectangle TITLE_BAR_RECT = new Rectangle(144, 2, 272, 30);
	private static final int TITLE_BAR_RADIUS = 30;
	private static final Color TITLE_TEXT_COLOR = new Color(0xE3D5B0, false);
	private static final Font TITLE_TEXT_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 18);
	private static final int TITLE_TEXT_Y = 24;
	private static final Rectangle PBL_RECT = new Rectangle(26, 50, 385, 385);
	private static final Color PBL_GRID_COLOR = Color.lightGray;
	private static final Color PBL_GUIDE_COLOR = new Color(0xCC0000, false);
	private static final Font PBL_FONT = new Font("Arial", Font.BOLD, 12);
	private static final float PBL_FONT_SCALE = 0.8f;
	private static final int PREVIEW_CENTER_X = 486;
	private static final int PREVIEW_CENTER_Y = 98;
	private static final int PALETTE_X = 438;
	private static final int PALETTE_Y = 164;
	private static final int PALETTE_SWATCH_SIZE = 16;
	private static final int PALETTE_SPACING_X = 1;
	private static final int PALETTE_SPACING_Y = 1;
	private static final int PALETTE_TEXT_PADDING = 8;
	private static final int PALETTE_BKGD_RADIUS = 4;
	private static final Color PALETTE_BKGD_COLOR = new Color(0x81375C, false);
	private static final Color PALETTE_TEXT_COLOR = new Color(0xE3D5B0, false);
	private static final Font PALETTE_TEXT_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 12);
	private static final Color PAGENUM_STROKE_COLOR = new Color(0x05B1C5, false);
	private static final Color PAGENUM_FILL_COLOR = new Color(0x6CDFDE, false);
	private static final Rectangle PAGENUM_RECT = new Rectangle(-40, 440, 110, 60);
	private static final int PAGENUM_RADIUS = 20;
	private static final BasicStroke PAGENUM_STROKE = new BasicStroke(4);
	private static final Color PAGENUM_TEXT_SHADOW = new Color(0xE8F4F2, false);
	private static final Color PAGENUM_TEXT_COLOR = new Color(0x5B170E, false);
	private static final Font PAGENUM_TEXT_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 32);
	private static final int PAGENUM_TEXT_X = 32;
	private static final int PAGENUM_TEXT_Y = 474;
	
	private final String title;
	private final BufferedImage preview;
	private final BufferedImage pblcard;
	
	public PBLCard(String title, BufferedImage preview, int previewScale, int[][] pbl, PBLEntry[] palette, int index, int count) {
		this.title = title;
		this.preview = preview;
		this.pblcard = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = pblcard.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(HATCH_COLOR);
		g.setStroke(HATCH_STROKE);
		for (int x = -(HEIGHT+HATCH_SPACING); x < WIDTH+HATCH_SPACING; x += HATCH_SPACING+HATCH_SPACING) {
			g.drawLine(x, 0, x + HEIGHT, HEIGHT);
			g.drawLine(x, HEIGHT, x + HEIGHT, 0);
		}
		
		// Title
		g.setColor(TITLE_BAR_COLOR);
		g.fillRoundRect(TITLE_BAR_RECT.x, TITLE_BAR_RECT.y, TITLE_BAR_RECT.width, TITLE_BAR_RECT.height, TITLE_BAR_RADIUS, TITLE_BAR_RADIUS);
		g.setColor(TITLE_TEXT_COLOR);
		g.setFont(TITLE_TEXT_FONT);
		FontMetrics fm = g.getFontMetrics();
		int tx = TITLE_BAR_RECT.x + (TITLE_BAR_RECT.width - fm.stringWidth(title)) / 2;
		g.drawString(title, tx, TITLE_TEXT_Y);
		
		// PBL
		int pblHeight = pbl.length;
		int pblWidth = 0;
		for (int[] row : pbl) {
			if (row.length > pblWidth) {
				pblWidth = row.length;
			}
		}
		if (pblHeight > 0 && pblWidth > 0) {
			int pblSize = Math.min(
				(PBL_RECT.width - 1) / pblWidth,
				(PBL_RECT.height - 1) / pblHeight
			);
			pblHeight = pblHeight * pblSize + 1;
			pblWidth = pblWidth * pblSize + 1;
			int pblx = PBL_RECT.x + (PBL_RECT.width - pblWidth) / 2;
			int pbly = PBL_RECT.y + (PBL_RECT.height - pblHeight) / 2;
			g.setFont(PBL_FONT.deriveFont(pblSize * PBL_FONT_SCALE));
			FontMetrics pblfm = g.getFontMetrics();
			int pblty = (pblSize - pblfm.getHeight()) / 2 + pblfm.getAscent() + 1;
			PBLEntry[] usedPalette = new PBLEntry[palette.length];
			for (int y = pbly, j = 0; j < pbl.length; j++, y += pblSize) {
				for (int x = pblx, i = 0; i < pbl[j].length; i++, x += pblSize) {
					PBLEntry e = usedPalette[pbl[j][i]] = palette[pbl[j][i]];
					if (e != null) {
						g.setColor(e.color);
						g.fillRect(x, y, pblSize, pblSize);
						g.setColor(contrast(e.color));
						int pbltx = (pblSize - pblfm.stringWidth(e.letter)) / 2 + 1;
						g.drawString(e.letter, pbltx + x, pblty + y);
					}
				}
			}
			g.setColor(PBL_GRID_COLOR);
			for (int y = 0; y <= pblHeight; y += pblSize) {
				g.fillRect(pblx, pbly + y, pblWidth, 1);
			}
			for (int x = 0; x <= pblWidth; x += pblSize) {
				g.fillRect(pblx + x, pbly, 1, pblHeight);
			}
			g.setColor(PBL_GUIDE_COLOR);
			g.fillRect(pblx, pbly + pblHeight / 2, pblWidth, 1);
			g.fillRect(pblx + pblWidth / 2, pbly, 1, pblHeight);
			palette = usedPalette;
		}
		
		// Preview
		if (preview != null && previewScale > 0) {
			int pw = preview.getWidth() * previewScale;
			int ph = preview.getHeight() * previewScale;
			int px = PREVIEW_CENTER_X - pw / 2;
			int py = PREVIEW_CENTER_Y - ph / 2;
			g.drawImage(preview, px, py, pw, ph, null);
		}
		
		// Palette
		FontMetrics metrics = g.getFontMetrics(PALETTE_TEXT_FONT);
		int rowAscent = (PALETTE_SWATCH_SIZE - metrics.getHeight()) / 2 + metrics.getAscent() + 1;
		int columns = 0;
		for (PBLEntry e : palette) {
			if (e != null && e.labels != null) {
				if (e.labels.length > columns) {
					columns = e.labels.length;
				}
			}
		}
		int[] columnWidths = new int[columns];
		for (PBLEntry e : palette) {
			if (e != null && e.labels != null) {
				for (int i = 0; i < e.labels.length; i++) {
					if (e.labels[i] != null) {
						int w = metrics.stringWidth(e.labels[i]) + PALETTE_TEXT_PADDING;
						if (w > columnWidths[i]) columnWidths[i] = w;
					}
				}
			}
		}
		for (int y = PALETTE_Y, j = 0; j < palette.length; j++) {
			if (palette[j] != null) {
				g.setColor(PBL_GRID_COLOR);
				g.fillRect(PALETTE_X, y, PALETTE_SWATCH_SIZE, PALETTE_SWATCH_SIZE);
				g.setColor(palette[j].color);
				g.fillRect(PALETTE_X + 1, y + 1, PALETTE_SWATCH_SIZE - 2, PALETTE_SWATCH_SIZE - 2);
				g.setColor(contrast(palette[j].color));
				g.setFont(PBL_FONT.deriveFont(PALETTE_SWATCH_SIZE * PBL_FONT_SCALE));
				FontMetrics psfm = g.getFontMetrics();
				int psty = (PALETTE_SWATCH_SIZE - psfm.getHeight()) / 2 + psfm.getAscent() + 1;
				int pstx = (PALETTE_SWATCH_SIZE - psfm.stringWidth(palette[j].letter)) / 2;
				g.drawString(palette[j].letter, PALETTE_X + pstx, y + psty);
				if (palette[j].labels != null) {
					for (int x = PALETTE_X + PALETTE_SWATCH_SIZE + PALETTE_SPACING_X, i = 0; i < palette[j].labels.length; i++) {
						if (palette[j].labels[i] != null) {
							g.setColor(PALETTE_BKGD_COLOR);
							g.fillRoundRect(x, y, columnWidths[i], PALETTE_SWATCH_SIZE, PALETTE_BKGD_RADIUS, PALETTE_BKGD_RADIUS);
							g.setColor(PALETTE_TEXT_COLOR);
							g.setFont(PALETTE_TEXT_FONT);
							int pltx = (columnWidths[i] - metrics.stringWidth(palette[j].labels[i])) / 2;
							g.drawString(palette[j].labels[i], x + pltx, y + rowAscent);
						}
						x += columnWidths[i] + PALETTE_SPACING_X;
					}
				}
				y += PALETTE_SWATCH_SIZE + PALETTE_SPACING_Y;
			}
		}
		
		// Page Number
		if (count > 1) {
			String pagenum = (index + 1) + "/" + count;
			g.setColor(PAGENUM_FILL_COLOR);
			g.fillRoundRect(PAGENUM_RECT.x, PAGENUM_RECT.y, PAGENUM_RECT.width, PAGENUM_RECT.height, PAGENUM_RADIUS, PAGENUM_RADIUS);
			g.setColor(PAGENUM_STROKE_COLOR);
			g.setStroke(PAGENUM_STROKE);
			g.drawRoundRect(PAGENUM_RECT.x, PAGENUM_RECT.y, PAGENUM_RECT.width, PAGENUM_RECT.height, PAGENUM_RADIUS, PAGENUM_RADIUS);
			g.setFont(PAGENUM_TEXT_FONT);
			fm = g.getFontMetrics();
			int x = PAGENUM_TEXT_X - fm.stringWidth(pagenum) / 2;
			g.setColor(PAGENUM_TEXT_SHADOW);
			g.drawString(pagenum, x + 2, PAGENUM_TEXT_Y + 1);
			g.setColor(PAGENUM_TEXT_COLOR);
			g.drawString(pagenum, x, PAGENUM_TEXT_Y);
		}
		
		g.dispose();
	}
	
	public String getTitle() {
		return title;
	}
	
	public BufferedImage getPreview() {
		return preview;
	}
	
	public BufferedImage getCardImage() {
		return pblcard;
	}
	
	public static final class PBLEntry {
		public final Color color;
		public final String letter;
		public final String[] labels;
		public PBLEntry(int rgb, String letter, String... labels) {
			this.color = new Color(rgb, true);
			this.letter = letter;
			this.labels = labels;
		}
	}
	
	private static Color contrast(Color color) {
		if (color == null || color.getAlpha() < 128) return Color.black;
		int k = color.getRed() * 30 + color.getGreen() * 59 + color.getBlue() * 11;
		return (k < 12750) ? Color.white : Color.black;
	}
}
