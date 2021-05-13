package com.kreative.barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QRDecode {
	public static void main(String[] args) {
		File output = null;
		String encoding = null;
		boolean opts = true;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (opts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					opts = false;
				} else if (arg.equals("-o")) {
					output = null;
				} else if (arg.equals("-f") && argi < args.length) {
					output = new File(args[argi++]);
				} else if (arg.equals("-r")) {
					encoding = null;
				} else if (arg.equals("-e") && argi < args.length) {
					encoding = args[argi++];
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				try {
					Result r = decodeQRCode(new File(arg));
					write(output, encoding, r);
				} catch (IOException e) {
					System.err.println("Error: " + e);
				}
			}
		}
	}
	
	private static Result decodeQRCode(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		HashMap<DecodeHintType,Object> hints = new HashMap<DecodeHintType,Object>();
		Object possibleFormats = Arrays.asList(BarcodeFormat.QR_CODE);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, possibleFormats);
		try { return new MultiFormatReader().decode(bitmap, hints); }
		catch (NotFoundException e) { throw new IOException("no code in image"); }
	}
	
	private static void write(File output, String encoding, Result r) throws IOException {
		if (output == null) {
			write(System.out, encoding, r);
		} else {
			FileOutputStream out = new FileOutputStream(output);
			write(out, encoding, r);
			out.close();
		}
	}
	
	private static void write(OutputStream out, String encoding, Result r) throws IOException {
		if (encoding == null) {
			out.write(r.getRawBytes());
		} else {
			OutputStreamWriter ow = new OutputStreamWriter(out, encoding);
			PrintWriter pw = new PrintWriter(ow, true);
			pw.print(r.getText());
			pw.flush();
			ow.flush();
		}
		out.flush();
	}
}
