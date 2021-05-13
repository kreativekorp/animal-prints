package com.kreative.acpattern;

import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ACQRView {
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
				int rows = (int)Math.ceil(Math.sqrt(acnl.getFrames()));
				JPanel panel = new JPanel(new GridLayout(rows, 0));
				for (int i = 0, n = acnl.getFrames(); i < n; i++) {
					ACQRCard card = acnl.getQRCard(i);
					JLabel label = new JLabel(new ImageIcon(card.getCardImage()));
					panel.add(label);
				}
				
				JFrame frame = new JFrame(file.getName());
				frame.setContentPane(panel);
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			} catch (Exception e) {
				System.out.println("ERROR: " + e);
			}
		}
	}
}
