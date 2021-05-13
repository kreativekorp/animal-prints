package com.kreative.acpattern;

import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PBLView {
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
				
				JComponent c = makeComponent(file, out.toByteArray());
				
				JFrame frame = new JFrame(file.getName());
				frame.setContentPane(c);
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			} catch (Exception e) {
				System.out.println("ERROR: " + e);
			}
		}
	}
	
	private static JComponent makeComponent(File file, byte[] data) {
		String fn = file.getName().toLowerCase();
		if (fn.endsWith(".acnh")) {
			ACNHFile acnh = new ACNHFile(data);
			int rows = (int)Math.ceil(Math.sqrt(acnh.getFrames()));
			JPanel panel = new JPanel(new GridLayout(rows, 0));
			for (int i = 0, n = acnh.getFrames(); i < n; i++) {
				PBLCard card = acnh.getPBLCard(i);
				JLabel label = new JLabel(new ImageIcon(card.getCardImage()));
				panel.add(label);
			}
			return panel;
		}
		if (fn.endsWith(".acnl")) {
			ACNLFile acnl = new ACNLFile(data);
			int rows = (int)Math.ceil(Math.sqrt(acnl.getFrames()));
			JPanel panel = new JPanel(new GridLayout(rows, 0));
			for (int i = 0, n = acnl.getFrames(); i < n; i++) {
				PBLCard card = acnl.getPBLCard(i);
				JLabel label = new JLabel(new ImageIcon(card.getCardImage()));
				panel.add(label);
			}
			return panel;
		}
		if (fn.endsWith(".acww")) {
			ACWWFile acww = new ACWWFile(data);
			PBLCard card = acww.getPBLCard();
			JLabel label = new JLabel(new ImageIcon(card.getCardImage()));
			return label;
		}
		throw new IllegalArgumentException("Unknown file type");
	}
}
