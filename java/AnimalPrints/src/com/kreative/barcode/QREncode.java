package com.kreative.barcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class QREncode {
	public static void main(String[] args) {
		boolean isData = false;
		boolean isPath = true;
		int ecl = QRCode.ECL_L;
		int qcolor = -1;
		int bgcolor = -1;
		int fgcolor = 0xFF << 24;
		File output = null;
		String outfmt = null;
		boolean written = false;
		boolean opts = true;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (opts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					opts = false;
				} else if (arg.equals("-d")) {
					isData = true;
					isPath = false;
				} else if (arg.equals("-f")) {
					isData = false;
					isPath = true;
				} else if (arg.equals("-e") && argi < args.length) {
					ecl = parseECL(args[argi++].trim());
				} else if (arg.equals("-q") && argi < args.length) {
					qcolor = parseColor(args[argi++].trim(), -1);
				} else if (arg.equals("-b") && argi < args.length) {
					bgcolor = parseColor(args[argi++].trim(), -1);
				} else if (arg.equals("-c") && argi < args.length) {
					fgcolor = parseColor(args[argi++].trim(), 0xFF << 24);
				} else if (arg.equals("-p") && argi < args.length) {
					output = new File(args[argi++]);
					outfmt = "png";
				} else if (arg.equals("-j") && argi < args.length) {
					output = new File(args[argi++]);
					outfmt = "jpg";
				} else if (arg.equals("-g") && argi < args.length) {
					output = new File(args[argi++]);
					outfmt = "gif";
				} else if (arg.equals("-o")) {
					output = null;
					outfmt = null;
				} else if (arg.equals("-i")) {
					try {
						QRCode qr = new QRCode(read(System.in), ecl);
						BufferedImage image = qr.getMatrixImage(qcolor, bgcolor, fgcolor);
						if (output == null) printImage(image);
						else ImageIO.write(image, outfmt, output);
						written = true;
					} catch (IOException e) {
						System.err.println("Error: " + e);
					}
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				if (isData) try {
					QRCode qr = new QRCode(arg.getBytes(), ecl);
					BufferedImage image = qr.getMatrixImage(qcolor, bgcolor, fgcolor);
					if (output == null) printImage(image);
					else ImageIO.write(image, outfmt, output);
					written = true;
				} catch (IOException e) {
					System.err.println("Error: " + e);
				}
				if (isPath) try {
					QRCode qr = new QRCode(read(new File(arg)), ecl);
					BufferedImage image = qr.getMatrixImage(qcolor, bgcolor, fgcolor);
					if (output == null) printImage(image);
					else ImageIO.write(image, outfmt, output);
					written = true;
				} catch (IOException e) {
					System.err.println("Error: " + e);
				}
			}
		}
		if (!written) {
			try {
				QRCode qr = new QRCode(read(System.in), ecl);
				BufferedImage image = qr.getMatrixImage(qcolor, bgcolor, fgcolor);
				if (output == null) printImage(image);
				else ImageIO.write(image, outfmt, output);
				written = true;
			} catch (IOException e) {
				System.err.println("Error: " + e);
			}
		}
	}
	
	private static int parseECL(String s) {
		if (s.equalsIgnoreCase("H") || s.equals("3")) return QRCode.ECL_H;
		if (s.equalsIgnoreCase("Q") || s.equals("2")) return QRCode.ECL_Q;
		if (s.equalsIgnoreCase("M") || s.equals("1")) return QRCode.ECL_M;
		return QRCode.ECL_L;
	}
	
	private static int parseColor(String s, int def) {
		try {
			s = s.replaceAll("[^0-9A-Za-z]", "");
			int v = Integer.parseInt(s, 16);
			switch (s.length()) {
				case 1: return (v * 0x111111) | (0xFF << 24);
				case 2: return (v * 0x010101) | (0xFF << 24);
				case 3: return rgb4to8(v) | (0xFF << 24);
				case 4: return rgb4to8(v);
				case 6: return v | (0xFF << 24);
				case 8: return v;
				default: return def;
			}
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	private static int rgb4to8(int v) {
		int a = (((v >> 12) & 0xF) * 0x11) << 24;
		int r = (((v >>  8) & 0xF) * 0x11) << 16;
		int g = (((v >>  4) & 0xF) * 0x11) <<  8;
		int b = (((v >>  0) & 0xF) * 0x11) <<  0;
		return (a | r | g | b);
	}
	
	private static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		for (int b = in.read(); b >= 0; b = in.read()) bs.write(b);
		return bs.toByteArray();
	}
	
	private static byte[] read(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] data = read(in);
		in.close();
		return data;
	}
	
	private static void printImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] rgb = new int[w * h];
		image.getRGB(0, 0, w, h, rgb, 0, w);
		for (int py = 0, y = 0; y < h; y += 2, py += w*2) {
			for (int px = py, x = 0; x < w; x++, px++) {
				printPixels(rgb[px], ((y + 1) < h) ? rgb[px + w] : 0);
			}
			System.out.println();
		}
	}
	
	private static void printPixels(int upper, int lower) {
		if (upper < 0 && lower < 0) {
			System.out.print("\u001B[38;2;" + ((upper >> 16) & 0xFF) + ";" + ((upper >> 8) & 0xFF) + ";" + (upper & 0xFF) + "m");
			System.out.print("\u001B[48;2;" + ((lower >> 16) & 0xFF) + ";" + ((lower >> 8) & 0xFF) + ";" + (lower & 0xFF) + "m");
			System.out.print("\u2580");
			System.out.print("\u001B[0m");
			return;
		}
		if (upper < 0) {
			System.out.print("\u001B[38;2;" + ((upper >> 16) & 0xFF) + ";" + ((upper >> 8) & 0xFF) + ";" + (upper & 0xFF) + "m");
			System.out.print("\u2580");
			System.out.print("\u001B[0m");
			return;
		}
		if (lower < 0) {
			System.out.print("\u001B[38;2;" + ((lower >> 16) & 0xFF) + ";" + ((lower >> 8) & 0xFF) + ";" + (lower & 0xFF) + "m");
			System.out.print("\u2584");
			System.out.print("\u001B[0m");
			return;
		}
		System.out.print(" ");
	}
}
