package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ACQRDebug {
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
				
				for (int i = 0, n = acnl.getFrames(); i < n; i++) {
					BufferedImage image = acnl.getQRCode(i).getMatrixImage();
					int w = image.getWidth();
					int h = image.getHeight();
					int[] rgb = new int[w * h];
					image.getRGB(0, 0, w, h, rgb, 0, w);
					for (int py = 0, y = 0; y < h; y += 2, py += w*2) {
						System.out.print("\t");
						for (int px = py, x = 0; x < w; x++, px++) {
							printPixels(rgb[px], ((y + 1) < h) ? rgb[px + w] : 0);
						}
						System.out.println();
					}
				}
			} catch (Exception e) {
				System.out.println("ERROR: " + e);
			}
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
