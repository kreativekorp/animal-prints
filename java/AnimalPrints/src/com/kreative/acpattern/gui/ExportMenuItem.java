package com.kreative.acpattern.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import com.kreative.acpattern.ACBaseFile;
import com.kreative.acpattern.ACEncoder;

public class ExportMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	private static String lastSaveDirectory = null;
	
	public ExportMenuItem(final String title, final Frame frame, final Object source, final ACEncoder format) {
		super(title);
		if (frame == null || source == null || format == null) {
			setEnabled(false);
		} else {
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FileDialog fd = new FileDialog(frame, "Export", FileDialog.SAVE);
					if (lastSaveDirectory != null) fd.setDirectory(lastSaveDirectory);
					fd.setVisible(true);
					String ds = fd.getDirectory(), fs = fd.getFile();
					fd.dispose();
					if (ds == null || fs == null) return;
					File file = new File((lastSaveDirectory = ds), fs);
					try {
						Object[] o = format.encode(source);
						try {
							write(o, file);
						} catch (Exception we) {
							JOptionPane.showMessageDialog(
								frame, "Could not write to file: " + we,
								"Export", JOptionPane.ERROR_MESSAGE
							);
						}
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(
							frame, "Could not convert pattern: " + ee,
							"Export", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
	
	private static void write(Object[] data, File file) throws IOException {
		if (data.length == 1) {
			write(data[0], file);
		} else {
			for (int i = 0; i < data.length; i++) {
				write(data[i], i, file);
			}
		}
	}
	
	private static void write(Object data, File file) throws IOException {
		if (data instanceof ACBaseFile) {
			String ext = ((ACBaseFile)data).getFileExtension();
			file = addExtension(file, "." + ext);
			FileOutputStream out = new FileOutputStream(file);
			out.write(((ACBaseFile)data).getData());
			out.flush();
			out.close();
			return;
		}
		if (data instanceof BufferedImage) {
			String format = getExtension(file, "png");
			file = addExtension(file, "." + format);
			ImageIO.write((BufferedImage)data, format, file);
			return;
		}
		if (data instanceof byte[]) {
			String ext = getExtension(file, "bin");
			file = addExtension(file, "." + ext);
			FileOutputStream out = new FileOutputStream(file);
			out.write((byte[])data);
			out.flush();
			out.close();
			return;
		}
		throw new IllegalArgumentException("unknown data type: " + data.getClass());
	}
	
	private static void write(Object data, int i, File file) throws IOException {
		if (data instanceof ACBaseFile) {
			String ext = ((ACBaseFile)data).getFileExtension();
			file = addExtension(file, "." + (i+1) + "." + ext);
			FileOutputStream out = new FileOutputStream(file);
			out.write(((ACBaseFile)data).getData());
			out.flush();
			out.close();
			return;
		}
		if (data instanceof BufferedImage) {
			String format = getExtension(file, "png");
			file = addExtension(file, "." + (i+1) + "." + format);
			ImageIO.write((BufferedImage)data, format, file);
			return;
		}
		if (data instanceof byte[]) {
			String ext = getExtension(file, "bin");
			file = addExtension(file, "." + (i+1) + "." + ext);
			FileOutputStream out = new FileOutputStream(file);
			out.write((byte[])data);
			out.flush();
			out.close();
			return;
		}
		throw new IllegalArgumentException("unknown data type: " + data.getClass());
	}
	
	private static String getExtension(File file, String def) {
		String fn = file.getName().toLowerCase();
		int o = fn.lastIndexOf('.');
		if (o > 0) return fn.substring(o + 1);
		return def;
	}
	
	private static File addExtension(File file, String ext) {
		if (file.getName().toLowerCase().endsWith(ext.toLowerCase())) return file;
		return new File(file.getParent(), file.getName() + ext);
	}
}
