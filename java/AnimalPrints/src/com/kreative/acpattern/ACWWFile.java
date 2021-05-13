package com.kreative.acpattern;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ACWWFile implements ACBaseFile {
	private byte[] data;
	
	public ACWWFile() {
		this.data = new byte[0x228];
		setTownName("Unknown");
		setCreatorName("Unknown");
		setTitle("Design Pattern");
		setPalette(14);
		setUnknownB(2);
	}
	
	public ACWWFile(byte[] data) {
		this.data = data;
	}
	
	@Override
	public int getTownID() {
		return getInt16(0x200);
	}
	
	@Override
	public void setTownID(int id) {
		setInt16(0x200, id);
	}
	
	@Override
	public String getTownName() {
		return getString(0x202, 0x20A);
	}
	
	@Override
	public void setTownName(String name) {
		setString(0x202, 0x20A, name);
	}
	
	@Override
	public int getCreatorID() {
		return getInt16(0x20A);
	}
	
	@Override
	public void setCreatorID(int id) {
		setInt16(0x20A, id);
	}
	
	@Override
	public String getCreatorName() {
		return getString(0x20C, 0x214);
	}
	
	@Override
	public void setCreatorName(String name) {
		setString(0x20C, 0x214, name);
	}
	
	public int getUnknownA() {
		return getInt16(0x214);
	}
	
	public void setUnknownA(int a) {
		setInt16(0x214, a);
	}
	
	@Override
	public String getTitle() {
		return getString(0x216, 0x226);
	}
	
	@Override
	public void setTitle(String title) {
		setString(0x216, 0x226, title);
	}
	
	public int getPalette() {
		return (data[0x226] >> 4) & 0xF;
	}
	
	public void setPalette(int palette) {
		data[0x226] &= 0xF;
		data[0x226] |= (palette << 4);
	}
	
	public int getLocalColorRGB(int i) {
		return getPaletteColorRGB(getPalette(), i);
	}
	
	public int getLocalColorIndex(int srgb) {
		return getPaletteColorIndex(getPalette(), srgb);
	}
	
	public int getUnknownB() {
		return data[0x226] & 0xF;
	}
	
	public void setUnknownB(int b) {
		data[0x226] &= 0xF0;
		data[0x226] |= (b & 0xF);
	}
	
	public int getUnknownC() {
		return data[0x227] & 0xFF;
	}
	
	public void setUnknownC(int c) {
		data[0x227] = (byte)c;
	}
	
	public int[] getRGB() {
		int[] rgb = new int[1024];
		int[] palette = PALETTES[getPalette()];
		for (int dy = 0, py = 0, y = 0; y < 32; y++, py += 32, dy += 16) {
			for (int dx = dy, px = py, x = 0; x < 32; x += 2, px += 2, dx++) {
				int b = data[dx] & 0xFF;
				rgb[px + 0] = palette[b & 15];
				rgb[px + 1] = palette[b >> 4];
			}
		}
		return rgb;
	}
	
	public void setRGB(int[] rgb) {
		for (int dy = 0, py = 0, y = 0; y < 32; y++, py += 32, dy += 16) {
			for (int dx = dy, px = py, x = 0; x < 32; x += 2, px += 2, dx++) {
				int p0 = getLocalColorIndex(rgb[px + 0]) & 0xF;
				int p1 = getLocalColorIndex(rgb[px + 1]) & 0xF;
				data[dx] = (byte)(p0 | (p1 << 4));
			}
		}
	}
	
	@Override
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 32, 32, getRGB(), 0, 32);
		return image;
	}
	
	public void setImage(BufferedImage image) {
		int[] rgb = new int[1024];
		image.getRGB(0, 0, 32, 32, rgb, 0, 32);
		setRGB(rgb);
	}
	
	public PBLCard.PBLEntry[] getPBLPaletteData() {
		PBLCard.PBLEntry[] palette = new PBLCard.PBLEntry[16];
		String p = (getPalette() + 1) + "/16";
		for (int i = 1; i < 16; i++) {
			palette[i] = new PBLCard.PBLEntry(
				getLocalColorRGB(i),
				Character.toString((char)('@' + i)),
				p, ("#" + i)
			);
		}
		return palette;
	}
	
	public int[][] getPBLImageData() {
		int[][] pbl = new int[32][32];
		for (int dy = 0, y = 0; y < 32; y++, dy += 16) {
			for (int dx = dy, x = 0; x < 32; x += 2, dx++) {
				int b = data[dx] & 0xFF;
				pbl[y][x + 0] = b & 15;
				pbl[y][x + 1] = b >> 4;
			}
		}
		return pbl;
	}
	
	public PBLCard getPBLCard() {
		return new PBLCard(
			getTitle(),
			getImage(), 3,
			getPBLImageData(),
			getPBLPaletteData(),
			0, 0
		);
	}
	
	@Override
	public void setImage(BufferedImage image, boolean optimize) {
		if (image.getWidth() != 32 || image.getHeight() != 32) {
			BufferedImage scaled = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaled.createGraphics();
			g.drawImage(image, 0, 0, 32, 32, null);
			g.dispose();
			image = scaled;
		}
		if (optimize) {
			int[] rgb = new int[1024];
			image.getRGB(0, 0, 32, 32, rgb, 0, 32);
			setPalette(findBestPalette(rgb));
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
		return false;
	}
	
	@Override
	public String getFileExtension() {
		return "acww";
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
	
	private String getString(int a, int b) {
		StringBuffer s = new StringBuffer();
		while (a < b) {
			int c = data[a] & 0xFF;
			if (c == 0) break;
			if (c >= 0xE0) c = 0xFFFD;
			else c = ALPHABET[c];
			s.append(Character.toChars(c));
			a++;
		}
		return s.toString();
	}
	
	private void setString(int a, int b, String s) {
		int si = 0, sn = s.length();
		encode: while (a < b) {
			if (si < sn) {
				int c = s.codePointAt(si);
				si += Character.charCount(c);
				for (int i = 0; i < 0xE0; i++) {
					if (ALPHABET[i] == c) {
						data[a++] = (byte)i;
						continue encode;
					}
				}
				data[a++] = (byte)0x9B;
			} else {
				data[a++] = 0;
			}
		}
	}
	
	// -- -- -- -- -- -- -- -- STATIC API -- -- -- -- -- -- -- -- //
	
	public static int getPaletteColorRGB(int palette, int index) {
		return PALETTES[palette & 0xF][index & 0xF];
	}
	
	public static int getPaletteColorIndex(int palette, int srgb) {
		int index = 0;
		if (srgb < 0) {
			int sr = (srgb >> 16) & 0xFF;
			int sg = (srgb >>  8) & 0xFF;
			int sb = (srgb >>  0) & 0xFF;
			int diff = Integer.MAX_VALUE;
			int p = palette & 0xF;
			for (int i = 1; i < 16; i++) {
				int drgb = PALETTES[p][i];
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
	
	public static int findBestPalette(int[] rgb) {
		long[] scores = new long[16];
		for (int srgb : rgb) {
			if (srgb < 0) {
				int sr = (srgb >> 16) & 0xFF;
				int sg = (srgb >>  8) & 0xFF;
				int sb = (srgb >>  0) & 0xFF;
				for (int p = 0; p < 16; p++) {
					int diff = Integer.MAX_VALUE;
					for (int drgb : PALETTES[p]) {
						if (drgb < 0) {
							int dr = (drgb >> 16) & 0xFF;
							int dg = (drgb >>  8) & 0xFF;
							int db = (drgb >>  0) & 0xFF;
							int d = colorDiff(sr, dr, sg, dg, sb, db);
							if (d < diff) diff = d;
						}
					}
					scores[p] += diff;
				}
			}
		}
		int palette = 0;
		long minDiff = scores[0];
		for (int p = 1; p < 16; p++) {
			if (scores[p] < minDiff) {
				palette = p;
				minDiff = scores[p];
			}
		}
		return palette;
	}
	
	private static final int colorDiff(int r1, int r2, int g1, int g2, int b1, int b2) {
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
	
	private static final int[][] PALETTES = {
		{0,0xFFFF0000,0xFFFF7534,0xFFFFAE00,0xFFFFFF00,0xFFAEFF00,0xFF55FF00,0xFF00FF00,0xFF00AE55,0xFF0055AE,0xFF0000FF,0xFF5500FF,0xFFAE00FF,0xFFFF00FF,0xFF000000,0xFFFFFFFF},
		{0,0xFFFF7D7D,0xFFFFB67D,0xFFFFE77D,0xFFFFFF7D,0xFFDFFF7D,0xFFAEFF7D,0xFF7DFF7D,0xFF55AE86,0xFF5586AE,0xFF7D7DFF,0xFFB67DFF,0xFFE77DFF,0xFFFF7DFF,0xFF000000,0xFFFFFFFF},
		{0,0xFFA60000,0xFFA63400,0xFFA67500,0xFFA6A600,0xFF75A600,0xFF34A600,0xFF00A600,0xFF005524,0xFF002455,0xFF0000A6,0xFF3400A6,0xFF7500A6,0xFFA600A6,0xFF000000,0xFFFFFFFF},
		{0,0xFF009E00,0xFF5DCF6D,0xFFB6FFDF,0xFF009E6D,0xFF55CFA6,0xFFAEFFD7,0xFF0055AE,0xFF2C86D7,0xFF5DAEFF,0xFF0000FF,0xFF4D6DFF,0xFF344DDF,0xFF1C24B6,0xFF00008E,0xFFFFFFFF},
		{0,0xFFAE7500,0xFFD7AE45,0xFFFFDF8E,0xFFFF0C3C,0xFFFF4D6D,0xFFFF969E,0xFFAE00FF,0xFFD765FF,0xFFFFCFFF,0xFFFFBE9E,0xFFDF9675,0xFFBE654D,0xFF9E3C24,0xFF7D1400,0xFFFFFFFF},
		{0,0xFFFF0000,0xFFFF5500,0xFFFFB65D,0xFFFFEFAE,0xFF7D1400,0xFFA64D34,0xFFD7866D,0xFFFFBE9E,0xFF5DAEFF,0xFF86C7FF,0xFFAEE7FF,0xFFD7FFFF,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFF00FF00,0xFF45FF45,0xFF8EFF8E,0xFFD7FFD7,0xFF0000FF,0xFF4545FF,0xFF8E8EFF,0xFFD7D7FF,0xFFFF0000,0xFFFF4545,0xFFFF8E8E,0xFFFFD7D7,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFF003400,0xFF456545,0xFF869E86,0xFFC7D7C7,0xFF7D1400,0xFFA64D2C,0xFFD78E5D,0xFFFFC78E,0xFFD7B600,0xFFE7CF3C,0xFFF7DF7D,0xFFFFF7BE,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFF0000FF,0xFFFF0000,0xFFFFFF00,0xFF4545FF,0xFFFF4545,0xFFFFFF45,0xFF8E8EFF,0xFFFF8E8E,0xFFFFFF8E,0xFFD7D7FF,0xFFFFD7D7,0xFFFFFFD7,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFF00FF00,0xFF0000FF,0xFFFF00FF,0xFF45FF45,0xFF4545FF,0xFFFF45FF,0xFF8EFF8E,0xFF8E8EFF,0xFFFF8EFF,0xFFD7FFD7,0xFFD7D7FF,0xFFFFD7FF,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFFFF0000,0xFFFF7D00,0xFFFFFF00,0xFF86FF00,0xFF00FF00,0xFF00867D,0xFF0000FF,0xFF7D00FF,0xFFFF96FF,0xFFD7B600,0xFFBE1400,0xFF5D1400,0xFF6D6D6D,0xFF000000,0xFFFFFFFF},
		{0,0xFF149665,0xFF0C7D55,0xFF148E3C,0xFF349E34,0xFFCFA64D,0xFFCF963C,0xFFBE8E4D,0xFFD78E34,0xFFAE754D,0xFF8E5D34,0xFF6D452C,0xFF86EFFF,0xFF34CFEF,0xFF00A6C7,0xFFFFFFFF},
		{0,0xFFD7DFE7,0xFFB6CFDF,0xFFE7EFEF,0xFFF7F7F7,0xFF86757D,0xFF968E6D,0xFF867D65,0xFF9E865D,0xFF759EB6,0xFFFF2C2C,0xFFFFFF00,0xFF9624FF,0xFF009EBE,0xFF000000,0xFFFFFFFF},
		{0,0xFFFFFFFF,0xFFF7EFEF,0xFFE7DFDF,0xFFD7CFCF,0xFFC7B6B6,0xFFB6A6A6,0xFFA69696,0xFF9E8686,0xFF8E6D6D,0xFF7D5D5D,0xFF6D4D4D,0xFF5D3434,0xFF4D2424,0xFF451414,0xFF340000},
		{0,0xFFFFFFFF,0xFFEFEFEF,0xFFDFDFDF,0xFFCFCFCF,0xFFB6B6B6,0xFFA6A6A6,0xFF969696,0xFF868686,0xFF6D6D6D,0xFF5D5D5D,0xFF4D4D4D,0xFF343434,0xFF242424,0xFF141414,0xFF000000},
		{0,0xFFFF8E7D,0xFFFF0000,0xFFFF7D00,0xFFFFFF00,0xFF008600,0xFF00FF00,0xFF0000FF,0xFF009EFF,0xFFD700FF,0xFFFF6DFF,0xFF9E0000,0xFFFF9600,0xFFFFBE96,0xFF000000,0xFFFFFFFF},
	};
	
	private static final int[] ALPHABET = {
		// This was determined by manually editing memory while in the letter editor.
		// The contents of the letter is stored at 0x021D84F0 while being edited.
		// x0  // x1  // x2  // x3  // x4  // x5  // x6  // x7  // x8  // x9  // xA  // xB  // xC  // xD  // xE  // xF   // WW
		000,   'A',   'B',   'C',   'D',   'E',   'F',   'G',   'H',   'I',   'J',   'K',   'L',   'M',   'N',   'O',    // 0x
		'P',   'Q',   'R',   'S',   'T',   'U',   'V',   'W',   'X',   'Y',   'Z',   'a',   'b',   'c',   'd',   'e',    // 1x
		'f',   'g',   'h',   'i',   'j',   'k',   'l',   'm',   'n',   'o',   'p',   'q',   'r',   's',   't',   'u',    // 2x
		'v',   'w',   'x',   'y',   'z',   '0',   '1',   '2',   '3',   '4',   '5',   '6',   '7',   '8',   '9',   0x0192, // 3x
		0x0160,0x0152,0x017D,0x0161,0x0153,0x017E,0x0178,0x00C0,0x00C1,0x00C2,0x00C3,0x00C4,0x00C5,0x00C6,0x00C7,0x00C8, // 4x
		0x00C9,0x00CA,0x00CB,0x00CC,0x00CD,0x00CE,0x00CF,0x00D0,0x00D1,0x00D2,0x00D3,0x00D4,0x00D5,0x00D6,0x00D8,0x00D9, // 5x
		0x00DA,0x00DB,0x00DC,0x00DD,0x00DE,0x00DF,0x00E0,0x00E1,0x00E2,0x00E3,0x00E4,0x00E5,0x00E6,0x00E7,0x00E8,0x00E9, // 6x
		0x00EA,0x00EB,0x00EC,0x00ED,0x00EE,0x00EF,0x00F0,0x00F1,0x00F2,0x00F3,0x00F4,0x00F5,0x00F6,0x00F8,0x00F9,0x00FA, // 7x
		0x00FB,0x00FC,0x00FD,0x00FE,0x00FF,0x0020,0x000A,0x0021,0x0022,0x0023,0x0024,0x0025,0x0026,0x0027,0x0028,0x0029, // 8x
		0x002A,0x002B,0x002C,0x002D,0x002E,0x002F,0x003A,0x003B,0x003C,0x003D,0x003E,0x003F,0x0040,0x005B,0x005C,0x005D, // 9x
		0x005E,0x005F,0x0060,0x007B,0x007C,0x007D,0x007E,0x20AC,0x201A,0x201E,0x2026,0x2020,0x2021,0x02C6,0x2030,0x2039, // Ax
		0x2018,0x2019,0x201C,0x201D,0x2022,0x2013,0x2014,0x02DC,0x2122,0x203A,0x00A0,0x00A1,0x00A2,0x00A3,0x00A4,0x00A5, // Bx
		0x00A6,0x00A7,0x00A8,0x00A9,0x00AA,0x00AB,0x00AC,0x00AD,0x00AE,0x00AF,0x00B0,0x00B1,0x00B2,0x00B3,0x00B4,0x00B5, // Cx
		0x00B6,0x00B7,0x00B8,0x00B9,0x00BA,0x00BB,0x00BC,0x00BD,0x00BE,0x00BF,0x00D7,0x00F7,127778,0x2605,0x2665,0x266A, // Dx
		// Characters 0xE0-0xFF are glitch characters. 0xE0-0xE2 and 0xF5-0xF7 cause the line the character is on to disappear.
		// 0xFE-0xFF cause the whole block of text the character is in to disappear. The rest just appear as random characters.
		// NOLINE,NOLINE,NOLINE,0x20AC,0x2039,0x004E,0x0040,0x21B2,'D',0x004E,0x0040,0x21B2,0x0054,0x004E,0x0040,0x21B2, // Ex
		// 0x004C,0x004E,0x0040,0x21B2,0x21B2,NOLINE,NOLINE,NOLINE,'4',0x004E,0x0040,0x21B2,0x0034,0x004E,NOTEXT,NOTEXT, // Fx
	};
}
