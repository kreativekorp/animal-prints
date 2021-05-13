package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ACNLDebug {
	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			System.out.println(file.getName());
			try {
				FileInputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (int b = in.read(); b >= 0; b = in.read()) out.write(b);
				in.close();
				out.close();
				
				ACNLFile acnl = new ACNLFile(out.toByteArray());
				System.out.println("\t" + acnl.getTitle() + " (" + acnl.getFormat() + ")");
				System.out.println("\t" + acnl.getCreatorName() + " (" + acnl.getCreatorID() + ")");
				System.out.println("\t" + acnl.getTownName() + " (" + acnl.getTownID() + ")");
				
				System.out.print("\t");
				int[] palette = acnl.getPaletteRGB(new int[15]);
				int[] indices = acnl.getPaletteGlobalIndex(new int[15]);
				for (int i = 0; i < 15; i++) {
					printRGB(palette[i], " " + hex(indices[i], 2) + " ");
				}
				System.out.println();
				System.out.println(
					"\tG:" + hex(acnl.getCreatorGender(), 2) +
					"\tg:" + hex(acnl.getCreatorReserved(), 2) +
					"\tL:" + hex(acnl.getTownLanguage(), 2) +
					"\tl:" + hex(acnl.getTownReserved(), 2) +
					"\tR:" + hex(acnl.getTownCountry(), 2) +
					"\tr:" + hex(acnl.getTownRegion(), 2) +
					"\tB:" + hex(acnl.getUnknownB(), 2) +
					"\tC:" + hex(acnl.getUnknownC(), 2) +
					"\tD:" + hex(acnl.getUnknownD(), 4)
				);
				
				BufferedImage image = acnl.getImage();
				int w = image.getWidth();
				int h = image.getHeight();
				int[] rgb = new int[w * h];
				image.getRGB(0, 0, w, h, rgb, 0, w);
				for (int py = 0, y = 0; y < h; y += 2, py += w*2) {
					System.out.print("\t");
					for (int px = py, x = 0; x < w; x++, px++) {
						printPixels(rgb[px], rgb[px + w]);
					}
					System.out.println();
				}
			} catch (Exception e) {
				System.out.println("ERROR: " + e);
			}
		}
	}
	
	private static String hex(int val, int len) {
		String h = Integer.toHexString(val).toUpperCase();
		while (h.length() < len) h = "0" + h;
		return h;
	}
	
	private static void printRGB(int rgb, String label) {
		int r = ((rgb >> 16) & 0xFF);
		int g = ((rgb >>  8) & 0xFF);
		int b = ((rgb >>  0) & 0xFF);
		int k = (r*30 + g*59 + b*11);
		System.out.print("\u001B[48;2;" + r + ";" + g + ";" + b + "m");
		System.out.print((k < 12750) ? "\u001B[97m" : "\u001B[30m");
		System.out.print(label);
		System.out.print("\u001B[0m");
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
