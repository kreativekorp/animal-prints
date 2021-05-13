package com.kreative.acpattern.gui;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.acpattern.ACBaseFile;
import com.kreative.acpattern.ACEncoder;
import com.kreative.acpattern.ACNHFile;
import com.kreative.acpattern.ACNLFile;
import com.kreative.acpattern.ACWWFile;

public class ConvertMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	public ConvertMenu(final Frame frame, final Object source) {
		super("Convert");
		
		JMenuItem nh = new ConvertMenuItem("to ACNH", frame, source, ACEncoder.ACNH);
		nh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		nh.setEnabled(frame != null && source != null && isCompatible(source, ACNHFile.class));
		add(nh);
		
		JMenuItem nl = new ConvertMenuItem("to ACNL", frame, source, ACEncoder.ACNL);
		nl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		nl.setEnabled(frame != null && source != null && isCompatible(source, ACNLFile.class));
		add(nl);
		
		JMenuItem ww = new ConvertMenuItem("to ACWW", frame, source, ACEncoder.ACWW);
		ww.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		ww.setEnabled(frame != null && source != null && isCompatible(source, ACWWFile.class));
		add(ww);
	}
	
	private static boolean isCompatible(Object source, Class<? extends ACBaseFile> cls) {
		if (source instanceof ACBaseFile) {
			if (((ACBaseFile)source).isProPattern()) {
				return false;
			}
		}
		return !cls.isInstance(source);
	}
}
