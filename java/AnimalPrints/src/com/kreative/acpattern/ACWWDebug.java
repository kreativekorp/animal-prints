package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ACWWDebug {
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
				
				ACWWFile acww = new ACWWFile(out.toByteArray());
				System.out.println("\t" + acww.getTitle());
				System.out.println("\t" + acww.getCreatorName() + " (" + acww.getCreatorID() + ")");
				System.out.println("\t" + acww.getTownName() + " (" + acww.getTownID() + ")");
				
				System.out.print("\t");
				int p = acww.getPalette();
				for (int i = 1; i < 16; i++) {
					printRGB(acww.getLocalColorRGB(i), " " + hex(p, 1) + hex(i, 1) + " ");
				}
				System.out.println();
				System.out.println(
					"\tA:" + hex(acww.getUnknownA(), 4) +
					"\tP:" + hex(acww.getPalette(), 1) +
					"\tB:" + hex(acww.getUnknownB(), 1) +
					"\tC:" + hex(acww.getUnknownC(), 2)
				);
				
				BufferedImage image = acww.getImage();
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
