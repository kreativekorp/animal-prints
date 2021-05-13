package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

public class ACConvert {
	public static void main(String[] args) {
		if (args.length == 0) { printHelp(); return; }
		ACDecoder decoder = new ACDecoder();
		String designName = null;
		Integer creatorID = null;
		String creatorName = null;
		Integer townID = null;
		String townName = null;
		ACEncoder encoder = ACEncoder.IMAGE;
		String format = "png";
		File output = null;
		boolean opts = true;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (opts && arg.startsWith("-")) {
				if (arg.equals("--")) {
					opts = false;
				} else if (arg.equals("-n") && argi < args.length) {
					String name = args[argi++].trim();
					if (name.length() == 0) designName = null;
					else designName = name;
				} else if (arg.equals("-c") && argi < args.length) {
					String name = args[argi++].trim();
					if (name.length() == 0) {
						creatorID = null;
						creatorName = null;
						continue;
					}
					if (name.startsWith("0x") || name.startsWith("0X")) {
						try { creatorID = Integer.parseInt(name.substring(2), 16); continue; }
						catch (NumberFormatException e) {}
					}
					try { creatorID = Integer.parseInt(name); }
					catch (NumberFormatException e) { creatorName = name; }
				} else if (arg.equals("-t") && argi < args.length) {
					String name = args[argi++].trim();
					if (name.length() == 0) {
						townID = null;
						townName = null;
						continue;
					}
					if (name.startsWith("0x") || name.startsWith("0X")) {
						try { townID = Integer.parseInt(name.substring(2), 16); continue; }
						catch (NumberFormatException e) {}
					}
					try { townID = Integer.parseInt(name); }
					catch (NumberFormatException e) { townName = name; }
				} else if (arg.equals("-e") && argi < args.length) {
					String encoderName = args[argi++].trim();
					try {
						encoder = ACEncoder.fromString(encoderName);
					} catch (Exception e) {
						System.err.println("Unknown format: " + encoderName);
					}
				} else if (arg.equals("-f") && argi < args.length) {
					format = args[argi++].trim();
				} else if (arg.equals("-o") && argi < args.length) {
					output = new File(args[argi++]);
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				try {
					File file = new File(arg);
					Object o = decoder.add(file);
					if (o == null) continue; // partial QR code
					if (o instanceof ACBaseFile) {
						if (designName != null) ((ACBaseFile)o).setTitle(designName);
						if (creatorID != null) ((ACBaseFile)o).setCreatorID(creatorID);
						if (creatorName != null) ((ACBaseFile)o).setCreatorName(creatorName);
						if (townID != null) ((ACBaseFile)o).setTownID(townID);
						if (townName != null) ((ACBaseFile)o).setTownName(townName);
					}
					try {
						Object[] data = encoder.encode(o);
						if (output == null) {
							File parent = file.getParentFile();
							String baseName = file.getName();
							write(data, format, parent, baseName, true);
						} else if (output.isDirectory()) {
							File parent = output;
							String baseName = file.getName();
							write(data, format, parent, baseName, true);
						} else {
							File parent = output.getParentFile();
							String baseName = output.getName();
							write(data, format, parent, baseName, false);
						}
					} catch (Exception ce) {
						System.err.println("Error converting " + arg + ": " + ce);
					}
				} catch (Exception re) {
					System.err.println("Error reading " + arg + ": " + re);
				}
			}
		}
		if (decoder.partialCodeCount() > 0) {
			System.err.println("Some QR codes were incomplete. Please include all four QR codes for pro patterns.");
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("ACConvert - Convert Animal Crossing design patterns.");
		System.out.println();
		System.out.println("Reads:");
		System.out.println("  .acnh, .acnl, .acww files; ACNL QR codes; PNG, JPEG, GIF, etc.");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -n <name>     Set design pattern name.");
		System.out.println("  -c <id|name>  Set creator ID (decimal or hex with 0x prefix) or name.");
		System.out.println("  -t <id|name>  Set town/island ID (decimal or hex with 0x prefix) or name.");
		System.out.println("  -e <format>   Set general output format. One of:");
		System.out.println("                  image     32x32 or 64x64 image");
		System.out.println("                  acnh      binary .acnh file");
		System.out.println("                  acnl      binary .acnl file");
		System.out.println("                  acww      binary .acww file");
		System.out.println("                  acnl-qr   ACNL QR code");
		System.out.println("                  acnh-pbl  ACNH paint-by-letters");
		System.out.println("                  acnl-pbl  ACNL paint-by-letters");
		System.out.println("                  acww-pbl  ACWW paint-by-letters");
		System.out.println("  -f <format>   Set format for image output: png, jpg, gif, etc.");
		System.out.println("  -o <path>     Set output file or directory.");
		System.out.println("  --            Treat remaining arguments as file names.");
		System.out.println();
	}
	
	private static void write(Object[] data, String format, File parent, String baseName, boolean appendExt) {
		if (data.length == 1) {
			String ext = getFileExtension(data[0], format);
			String name = appendExt ? (baseName + "." + ext) : baseName;
			write(data[0], format, new File(parent, name));
		} else {
			for (int i = 0; i < data.length; i++) {
				String ext = getFileExtension(data[i], format);
				String name = baseName + "." + (i+1) + "." + ext;
				write(data[i], format, new File(parent, name));
			}
		}
	}
	
	private static void write(Object data, String format, File file) {
		try {
			if (data instanceof ACBaseFile) {
				FileOutputStream out = new FileOutputStream(file);
				out.write(((ACBaseFile)data).getData());
				out.flush();
				out.close();
				return;
			}
			if (data instanceof BufferedImage) {
				ImageIO.write((BufferedImage)data, format, file);
				return;
			}
			if (data instanceof byte[]) {
				FileOutputStream out = new FileOutputStream(file);
				out.write((byte[])data);
				out.flush();
				out.close();
				return;
			}
			throw new IllegalArgumentException("unknown data type: " + data.getClass());
		} catch (Exception we) {
			System.err.println("Error writing " + file.getName() + ": " + we);
		}
	}
	
	private static String getFileExtension(Object data, String format) {
		if (data instanceof ACBaseFile) return ((ACBaseFile)data).getFileExtension();
		if (data instanceof BufferedImage) return format;
		if (data instanceof byte[]) return "bin";
		throw new IllegalArgumentException("unknown data type: " + data.getClass());
	}
}
