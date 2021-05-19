package com.kreative.acpattern.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.acpattern.ACBaseFile;
import com.kreative.acpattern.ACEncoder;
import com.kreative.acpattern.ACNHFile;

public class SendMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	public SendMenu(final Object source) {
		super("Send");
		
		JMenuItem nh = new JMenuItem("to ACNH...");
		nh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK));
		nh.setEnabled(source != null && isCompatible(source, ACNHFile.class));
		nh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.sendToACNH((ACNHFile)ACEncoder.ACNH.encode(source)[0]);
			}
		});
		add(nh);
	}
	
	private static boolean isCompatible(Object source, Class<? extends ACBaseFile> cls) {
		if (source instanceof ACBaseFile) {
			if (((ACBaseFile)source).isProPattern()) {
				return false;
			}
		}
		return true;
	}
}
