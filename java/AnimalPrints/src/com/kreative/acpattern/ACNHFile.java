package com.kreative.acpattern;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ACNHFile implements ACBaseFile {
	private byte[] data;
	
	public ACNHFile() {
		this(DESIGN_PATTERN);
	}
	
	public ACNHFile(int format) {
		setFormat(format);
		setUnknownA(0x09);
		setTitle("Design Pattern");
		setTownID(-1);
		setTownName("Unknown");
		setCreatorID(-1);
		setCreatorName("Unknown");
		setUnknownF(0xDD0F);
		for (int i = 0; i <= 14; i++) {
			int rgb = HVBtoRGB(0, 0, 14 - i);
			setPaletteRGB(i, rgb);
		}
	}
	
	public ACNHFile(byte[] data) {
		this.data = data;
	}
	
	public int getChecksum() {
		return getInt32(0x00);
	}
	
	public void setChecksum(int checksum) {
		setInt32(0x00, checksum);
	}
	
	public int getUnknownA() {
		return getInt32(0x04);
	}
	
	public void setUnknownA(int a) {
		setInt32(0x04, a);
	}
	
	public int getUnknownB() {
		return getInt32(0x08);
	}
	
	public void setUnknownB(int b) {
		setInt32(0x08, b);
	}
	
	public int getUnknownC() {
		return getInt32(0x0C);
	}
	
	public void setUnknownC(int c) {
		setInt32(0x0C, c);
	}
	
	@Override
	public String getTitle() {
		return getUTF16(0x10, 0x38);
	}
	
	@Override
	public void setTitle(String title) {
		setUTF16(0x10, 0x38, title);
	}
	
	@Override
	public int getTownID() {
		return getInt32(0x38);
	}
	
	@Override
	public void setTownID(int id) {
		setInt32(0x38, id);
	}
	
	@Override
	public String getTownName() {
		return getUTF16(0x3C, 0x50);
	}
	
	@Override
	public void setTownName(String name) {
		setUTF16(0x3C, 0x50, name);
	}
	
	public int getUnknownD() {
		return getInt32(0x50);
	}
	
	public void setUnknownD(int d) {
		setInt32(0x50, d);
	}
	
	@Override
	public int getCreatorID() {
		return getInt32(0x54);
	}
	
	@Override
	public void setCreatorID(int id) {
		setInt32(0x54, id);
	}
	
	@Override
	public String getCreatorName() {
		return getUTF16(0x58, 0x6C);
	}
	
	@Override
	public void setCreatorName(String name) {
		setUTF16(0x58, 0x6C, name);
	}
	
	public int getUnknownE() {
		return getInt32(0x6C);
	}
	
	public void setUnknownE(int e) {
		setInt32(0x6C, e);
	}
	
	public int getUnknownF() {
		return getInt32(0x70);
	}
	
	public void setUnknownF(int f) {
		setInt32(0x70, f);
	}
	
	public int getUnknownG() {
		return getInt32(0x74);
	}
	
	public void setUnknownG(int g) {
		setInt32(0x74, g);
	}
	
	public int getPaletteRGB(int i) {
		if ((i &= 0xF) == 0xF) return 0;
		int a = 0x78 + (i * 3);
		int r = (data[a + 0] & 0xFF) << 16;
		int g = (data[a + 1] & 0xFF) <<  8;
		int b = (data[a + 2] & 0xFF) <<  0;
		return 0xFF000000 | r | g | b;
	}
	
	public void setPaletteRGB(int i, int rgb) {
		if ((i &= 0xF) == 0xF) return;
		int a = 0x78 + (i * 3);
		data[a + 0] = (byte)(rgb >> 16);
		data[a + 1] = (byte)(rgb >>  8);
		data[a + 2] = (byte)(rgb >>  0);
	}
	
	public int[] getPaletteRGB(int[] rgb) {
		if (rgb == null || rgb.length < 15) {
			rgb = new int[15];
		}
		for (int i = 0; i < 15; i++) {
			rgb[i] = getPaletteRGB(i);
		}
		return rgb;
	}
	
	public void setPaletteRGB(int[] rgb) {
		for (int i = 0; i < 15; i++) {
			setPaletteRGB(i, rgb[i]);
		}
	}
	
	public int getLocalColorRGB(int i) {
		return getPaletteRGB(i);
	}
	
	public int getLocalColorIndex(int srgb) {
		int index = 15;
		if (srgb < 0) {
			int sr = (srgb >> 16) & 0xFF;
			int sg = (srgb >>  8) & 0xFF;
			int sb = (srgb >>  0) & 0xFF;
			int diff = Integer.MAX_VALUE;
			for (int i = 0; i < 15; i++) {
				int drgb = getPaletteRGB(i);
				if (drgb < 0) {
					int dr = (drgb >> 16) & 0xFF;
					int dg = (drgb >>  8) & 0xFF;
					int db = (drgb >>  0) & 0xFF;
					int d = colorDiff(sr, dr, sg, dg, sb, db);
					if (d < diff) {
						index = i;
						diff = d;
					}
				}
			}
		}
		return index;
	}
	
	public int getFormat() {
		return data[data.length - 3] & 0xFF;
	}
	
	public int getWidth() {
		return getWidth(getFormat());
	}
	
	public int getHeight() {
		return getHeight(getFormat());
	}
	
	public int getFrames() {
		return getFrames(getFormat());
	}
	
	public int getLength() {
		return getLength(getFormat());
	}
	
	public void setFormat(int format) {
		int newLength = getLength(format);
		if (data == null) {
			data = new byte[newLength];
		} else if (data.length != newLength) {
			int h = getUnknownH();
			byte[] newData = new byte[newLength];
			for (int i = 0; i < data.length && i < newLength; i++) {
				newData[i] = data[i];
			}
			data = newData;
			setUnknownH(h);
		}
		data[data.length - 3] = (byte)format;
	}
	
	public int getUnknownH() {
		return getInt16(data.length - 2);
	}
	
	public void setUnknownH(int h) {
		setInt16(data.length - 2, h);
	}
	
	public int[] getFrameRGB(int frame) {
		int[] rgb = new int[1024];
		int[] palette = getPaletteRGB(new int[16]);
		for (int dy = ((frame << 9) | 0xA5), py = 0, y = 0; y < 32; y++, py += 32, dy += 16) {
			for (int dx = dy, px = py, x = 0; x < 32; x += 2, px += 2, dx++) {
				int b = data[dx] & 0xFF;
				rgb[px + 0] = palette[b & 15];
				rgb[px + 1] = palette[b >> 4];
			}
		}
		return rgb;
	}
	
	public void setFrameRGB(int frame, int[] rgb) {
		for (int dy = ((frame << 9) | 0xA5), py = 0, y = 0; y < 32; y++, py += 32, dy += 16) {
			for (int dx = dy, px = py, x = 0; x < 32; x += 2, px += 2, dx++) {
				int p0 = getLocalColorIndex(rgb[px + 0]) & 0xF;
				int p1 = getLocalColorIndex(rgb[px + 1]) & 0xF;
				data[dx] = (byte)(p0 | (p1 << 4));
			}
		}
	}
	
	public BufferedImage getFrameImage(int frame) {
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 32, 32, getFrameRGB(frame), 0, 32);
		return image;
	}
	
	public void setFrameImage(int frame, BufferedImage image) {
		int[] rgb = new int[1024];
		image.getRGB(0, 0, 32, 32, rgb, 0, 32);
		setFrameRGB(frame, rgb);
	}
	
	@Override
	public BufferedImage getImage() {
		int x = 0, y = 0, w = getWidth(), h = getHeight();
		int i = 0, n = getFrames();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		while (i < n) {
			int[] rgb = getFrameRGB(i);
			image.setRGB(x, y, 32, 32, rgb, 0, 32);
			i++;
			y += 32;
			if (y >= h) {
				y = 0;
				x += 32;
			}
		}
		return image;
	}
	
	public void setImage(BufferedImage image) {
		int x = 0, y = 0, h = image.getHeight();
		int i = 0, n = getFrames();
		while (i < n) {
			int[] rgb = new int[1024];
			image.getRGB(x, y, 32, 32, rgb, 0, 32);
			setFrameRGB(i, rgb);
			i++;
			y += 32;
			if (y >= h) {
				y = 0;
				x += 32;
			}
		}
	}
	
	public PBLCard.PBLEntry[] getPBLPaletteData() {
		PBLCard.PBLEntry[] palette = new PBLCard.PBLEntry[16];
		for (int i = 0; i < 15; i++) {
			int rgb = getLocalColorRGB(i);
			int[] hvb = RGBtoHVB(
				(rgb >> 16) & 0xFF,
				(rgb >>  8) & 0xFF,
				(rgb >>  0) & 0xFF,
				null, null
			);
			palette[i] = new PBLCard.PBLEntry(
				rgb,
				Character.toString((char)('A' + i)),
				Integer.toString(hvb[0] + 1),
				Integer.toString(hvb[1] + 1),
				Integer.toString(hvb[2] + 1)
			);
		}
		return palette;
	}
	
	public int[][] getPBLImageData(int frame) {
		int[][] pbl = new int[32][32];
		for (int dy = ((frame << 9) | 0xA5), y = 0; y < 32; y++, dy += 16) {
			for (int dx = dy, x = 0; x < 32; x += 2, dx++) {
				int b = data[dx] & 0xFF;
				pbl[y][x + 0] = b & 15;
				pbl[y][x + 1] = b >> 4;
			}
		}
		return pbl;
	}
	
	public PBLCard getPBLCard(int frame) {
		return new PBLCard(
			getTitle(),
			getFrameImage(frame), 3,
			getPBLImageData(frame),
			getPBLPaletteData(),
			frame, getFrames()
		);
	}
	
	@Override
	public void setImage(BufferedImage image, boolean optimize) {
		int w = getWidth(), h = getHeight();
		if (image.getWidth() != w || image.getHeight() != h) {
			BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaled.createGraphics();
			g.drawImage(image, 0, 0, w, h, null);
			g.dispose();
			image = scaled;
		}
		if (optimize) {
			int[] rp = ColorReducer.reduce(image, 15);
			int[] p = new int[15]; int i = 0;
			for (int rgb : rp) p[i++] = rgb;
			while (i < 15) p[i++] = -1;
			setPaletteRGB(p);
		}
		setImage(image);
	}
	
	@Override
	public byte[] getData() {
		return this.data;
	}
	
	@Override
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public boolean isProPattern() {
		return getFrames() > 1;
	}
	
	@Override
	public String getFileExtension() {
		return "acnh";
	}
	
	// -- -- -- -- -- -- -- -- PRIVATE API -- -- -- -- -- -- -- -- //
	
	private int getInt16(int a) {
		int lsb = data[a + 0] & 0xFF;
		int msb = data[a + 1] & 0xFF;
		return (msb << 8) | (lsb << 0);
	}
	
	private void setInt16(int a, int v) {
		data[a + 0] = (byte)(v >> 0);
		data[a + 1] = (byte)(v >> 8);
	}
	
	private int getInt32(int a) {
		int b0 = data[a + 0] & 0xFF;
		int b1 = data[a + 1] & 0xFF;
		int b2 = data[a + 2] & 0xFF;
		int b3 = data[a + 3] & 0xFF;
		return (b3 << 24) | (b2 << 16) | (b1 << 8) | (b0 << 0);
	}
	
	private void setInt32(int a, int v) {
		data[a + 0] = (byte)(v >> 0);
		data[a + 1] = (byte)(v >> 8);
		data[a + 2] = (byte)(v >> 16);
		data[a + 3] = (byte)(v >> 24);
	}
	
	private String getUTF16(int a, int b) {
		StringBuffer s = new StringBuffer();
		while (a < b) {
			int c = getInt16(a);
			if (c == 0) break;
			s.append((char)c);
			a += 2;
		}
		return s.toString();
	}
	
	private void setUTF16(int a, int b, String s) {
		char[] sa = s.toCharArray();
		int si = 0, sn = sa.length;
		while (a < b) {
			int c = (si < sn) ? sa[si++] : 0;
			setInt16(a, c);
			a += 2;
		}
	}
	
	// -- -- -- -- -- -- -- -- STATIC API -- -- -- -- -- -- -- -- //
	
	public static final int DESIGN_PATTERN = 0;
	public static final int PRO_PATTERN = 1;
	public static final int DESIGN_TANK_TOP = 2;
	public static final int PRO_LONG_SLEEVE_DRESS_SHIRT = 3;
	public static final int PRO_SHORT_SLEEVE_TEE = 4;
	public static final int PRO_TANK_TOP = 5;
	public static final int PRO_SWEATER = 6;
	public static final int PRO_HOODIE = 7;
	public static final int PRO_COAT = 8;
	public static final int PRO_SHORT_SLEEVE_DRESS = 9;
	public static final int PRO_SLEEVELESS_DRESS = 10;
	public static final int PRO_LONG_SLEEVE_DRESS = 11;
	public static final int PRO_BALLOON_HEM_DRESS = 12;
	public static final int PRO_ROUND_DRESS = 13;
	public static final int PRO_ROBE = 14;
	public static final int PRO_BRIMMED_CAP = 15;
	public static final int PRO_KNIT_CAP = 16;
	public static final int PRO_BRIMMED_HAT = 17;
	public static final int IMPORTED_DRESS_SHORT_SLEEVE = 18;
	public static final int IMPORTED_DRESS_LONG_SLEEVE = 19;
	public static final int IMPORTED_DRESS_SLEEVELESS = 20;
	public static final int IMPORTED_SHIRT_SHORT_SLEEVE = 21;
	public static final int IMPORTED_SHIRT_LONG_SLEEVE = 22;
	public static final int IMPORTED_SHIRT_SLEEVELESS = 23;
	public static final int IMPORTED_HAT_HORNLESS = 24;
	public static final int IMPORTED_HAT_HORNED = 25;
	
	public static int getWidth(int format) {
		return (format == 0 || format == 2 || format == 24 || format == 25) ? 32 : 64;
	}
	
	public static int getHeight(int format) {
		return (format == 0 || format == 2 || format == 24 || format == 25) ? 32 : 64;
	}
	
	public static int getFrames(int format) {
		return (format == 0 || format == 2 || format == 24 || format == 25) ? 1 : 4;
	}
	
	public static int getLength(int format) {
		return (format == 0 || format == 2 || format == 24 || format == 25) ? 680 : 2216;
	}
	
	public static int HVBtoRGB(int h, int v, int b) {
		return Color.HSBtoRGB(
			(mod(h, 30) / 30f),
			(clamp(v, 0, 14) / 15f),
			((clamp(b, 0, 14) * 3 + 4) / 51f)
		);
	}
	
	public static int[] RGBtoHVB(int r, int g, int b, float[] hsb, int[] hvb) {
		hsb = Color.RGBtoHSB(r, g, b, hsb);
		if (hvb == null) hvb = new int[3];
		hvb[0] = mod((int)Math.round(hsb[0] * 30f), 30);
		hvb[1] = clamp((int)Math.round(hsb[1] * 15f), 0, 14);
		hvb[2] = clamp((int)Math.round(((hsb[2] * 51f) - 4f) / 3f), 0, 14);
		return hvb;
	}
	
	private static int mod(int val, int mod) {
		while (val < 0) val += mod;
		while (val >= mod) val -= mod;
		return val;
	}
	
	private static int clamp(int val, int min, int max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}
	
	private static int colorDiff(int r1, int r2, int g1, int g2, int b1, int b2) {
		int dr = r1 - r2;
		int dg = g1 - g2;
		int db = b1 - b2;
		float rm = (r1 + r2) / 2f;
		float rw = (rm / 256f) + 2f;
		float gw = 4f;
		float bw = ((255f - rm) / 256f) + 2f;
		return (int)Math.round(
			rw * dr * dr +
			gw * dg * dg +
			bw * db * db
		);
	}
}
