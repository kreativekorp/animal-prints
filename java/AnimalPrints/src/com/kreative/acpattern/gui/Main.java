package com.kreative.acpattern.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.kreative.acpattern.ACDecoder;
import com.kreative.acpattern.ACNHFile;
import com.kreative.acpattern.ACNLFile;
import com.kreative.acpattern.ACView;
import com.kreative.acpattern.ACWWFile;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Animal Prints"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
			Object allUnnamed = getModule.invoke(ACView.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
		} catch (Exception e) {}
		
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
			aacn.setAccessible(true);
			aacn.set(tk, "Animal Prints");
		} catch (Exception e) {}
		
		for (String arg : args) openFile(new File(arg));
		if (decoder.partialCodeCount() > 0) {
			JOptionPane.showMessageDialog(
				null, "Some QR codes were incomplete. Please include all four QR codes for pro patterns.",
				"Open", JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	public static void newFromClipboard() {
		BufferedImage bi = pasteImage();
		if (bi != null) openObject("From Clipboard", bi);
	}
	
	public static void open() {
		FileDialog fd = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) return;
		File file = new File(fd.getDirectory(), fd.getFile());
		open(file);
	}
	
	public static void open(File file) {
		if (file == null) open();
		else open(new File[]{file});
	}
	
	public static void open(File[] files) {
		for (File file : files) openFile(file);
		if (decoder.partialCodeCount() > 0) {
			JOptionPane.showMessageDialog(
				null, "Please open the next QR code.",
				"Open", JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	private static ACDecoder decoder = new ACDecoder();
	private static JFrame openFile(File file) {
		try {
			Object o = decoder.add(file);
			if (o == null) return null; // partial QR code
			return openObject(file.getName(), o);
		} catch (Exception re) {
			JOptionPane.showMessageDialog(
				null, "Error reading " + file.getName() + ": " + re,
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static JFrame openObject(String title, Object o) {
		if (o instanceof ACNHFile) {
			JFrame f = new ACNHFrame(title, (ACNHFile)o);
			f.setVisible(true);
			return f;
		}
		if (o instanceof ACNLFile) {
			JFrame f = new ACNLFrame(title, (ACNLFile)o);
			f.setVisible(true);
			return f;
		}
		if (o instanceof ACWWFile) {
			JFrame f = new ACWWFrame(title, (ACWWFile)o);
			f.setVisible(true);
			return f;
		}
		if (o instanceof BufferedImage) {
			JFrame f = new ImageFrame(title, (BufferedImage)o);
			f.setVisible(true);
			return f;
		}
		JOptionPane.showMessageDialog(
			null, "Error reading " + title + ": unknown data type: " + o.getClass(),
			"Open", JOptionPane.ERROR_MESSAGE
		);
		return null;
	}
	
	public static void copyImage(BufferedImage image) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Clipboard cb = tk.getSystemClipboard();
		ImageSelection is = new ImageSelection(image);
		cb.setContents(is, is);
	}
	
	public static BufferedImage pasteImage() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Clipboard cb = tk.getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			try {
				return toBufferedImage((Image)cb.getData(DataFlavor.imageFlavor));
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static BufferedImage toBufferedImage(Image image) {
		if (image == null) return null;
		if (image instanceof BufferedImage) return (BufferedImage)image;
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) < 1000) {
			int w = image.getWidth(null);
			int h = image.getHeight(null);
			if (w < 0 || h < 0) continue;
			if (w < 1 || h < 1) break;
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			boolean success = g.drawImage(image, 0, 0, null);
			g.dispose();
			if (success) return bi;
		}
		return null;
	}
	
	private static SendToACNHFrame sendToACNH = null;
	public static void sendToACNH(ACNHFile acnh) {
		if (sendToACNH == null) sendToACNH = new SendToACNHFrame();
		sendToACNH.setACNHFile(acnh);
		sendToACNH.setVisible(true);
	}
}
