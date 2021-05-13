package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class ACDecoder {
	private final ACQRDecoder[][] partialCodes = new ACQRDecoder[256][];
	private final List<Object> decodedObjects = new ArrayList<Object>();
	
	public Object add(File file) {
		Object o = decode(file);
		if (o instanceof ACQRDecoder) {
			ACQRDecoder decoder = (ACQRDecoder)o;
			int count = decoder.getFrameCount();
			if (count > 1) {
				int parity = decoder.getQRParity();
				if (partialCodes[parity] == null) {
					partialCodes[parity] = new ACQRDecoder[count];
				}
				int index = decoder.getFrameIndex();
				partialCodes[parity][index] = decoder;
				for (ACQRDecoder d : partialCodes[parity]) {
					if (d == null) return null;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (ACQRDecoder d : partialCodes[parity]) {
					byte[] data = d.getQRData();
					out.write(data, 0, data.length);
				}
				partialCodes[parity] = null;
				o = new ACNLFile(out.toByteArray());
				decodedObjects.add(o);
				return o;
			} else {
				o = new ACNLFile(decoder.getQRData());
				decodedObjects.add(o);
				return o;
			}
		} else {
			decodedObjects.add(o);
			return o;
		}
	}
	
	public int partialCodeCount() {
		int count = 0;
		for (ACQRDecoder[] partialCode : partialCodes) {
			if (partialCode != null) {
				count++;
			}
		}
		return count;
	}
	
	public List<ACQRDecoder[]> partialCodes() {
		List<ACQRDecoder[]> list = new ArrayList<ACQRDecoder[]>();
		for (ACQRDecoder[] partialCode : partialCodes) {
			if (partialCode != null) {
				list.add(partialCode);
			}
		}
		return list;
	}
	
	public List<Object> decodedObjects() {
		return Collections.unmodifiableList(decodedObjects);
	}
	
	private static Object decode(File file) {
		String fn = file.getName().toLowerCase();
		if (fn.endsWith(".acnh")) {
			if (file.length() >= 0x2A8) return new ACNHFile(readFile(file));
			throw new IllegalArgumentException("not in the expected format");
		}
		if (fn.endsWith(".acnl")) {
			if (file.length() >= 0x26C) return new ACNLFile(readFile(file));
			throw new IllegalArgumentException("not in the expected format");
		}
		if (fn.endsWith(".acww")) {
			if (file.length() >= 0x228) return new ACWWFile(readFile(file));
			throw new IllegalArgumentException("not in the expected format");
		}
		if (fn.endsWith(".dat") || fn.endsWith(".bin")) {
			if (file.length() >= 0x8A8) return new ACNHFile(readFile(file)); // ACNH pro
			if (file.length() >= 0x870) return new ACNLFile(readFile(file)); // ACNL pro
			if (file.length() >= 0x2A8) return new ACNHFile(readFile(file)); // ACNH basic
			if (file.length() >= 0x26C) return new ACNLFile(readFile(file)); // ACNL basic
			if (file.length() >= 0x228) return new ACWWFile(readFile(file)); // ACWW
			throw new IllegalArgumentException("not an AC pattern file");
		}
		try {
			BufferedImage image = ImageIO.read(file);
			try { return new ACQRDecoder(image); }
			catch (Exception e) { return image; }
		} catch (IOException ioe) {
			throw new IllegalArgumentException("not a readable image", ioe);
		}
	}
	
	private static byte[] readFile(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) out.write(b);
			in.close();
			out.close();
			return out.toByteArray();
		} catch (IOException ioe) {
			throw new IllegalStateException("could not read file", ioe);
		}
	}
}
