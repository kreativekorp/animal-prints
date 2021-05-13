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

public class ExportMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	public ExportMenu(final Frame frame, final Object source) {
		super("Export");
		add(new ExportMenuItem("Image...", frame, source, ACEncoder.IMAGE));
		
		addSeparator();
		JMenuItem nh1 = new ExportMenuItem("ACNH (.acnh)...", frame, source, ACEncoder.ACNH);
		JMenuItem nh2 = new ExportMenuItem("ACNH (image)...", frame, source, ACEncoder.ACNH_IMAGE);
		JMenuItem nh3 = new ExportMenuItem("ACNH (PBL)...", frame, source, ACEncoder.ACNH_PBL);
		if (source instanceof ACNHFile) {
			nh1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY));
			nh2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.ALT_MASK));
			nh3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		}
		boolean nhok = (frame != null && source != null && isCompatible(source, ACNHFile.class));
		nh1.setEnabled(nhok); add(nh1);
		nh2.setEnabled(nhok); add(nh2);
		nh3.setEnabled(nhok); add(nh3);
		
		addSeparator();
		JMenuItem nl1 = new ExportMenuItem("ACNL (.acnl)...", frame, source, ACEncoder.ACNL);
		JMenuItem nl2 = new ExportMenuItem("ACNL (image)...", frame, source, ACEncoder.ACNL_IMAGE);
		JMenuItem nl3 = new ExportMenuItem("ACNL (QR code)...", frame, source, ACEncoder.ACNL_QR);
		JMenuItem nl4 = new ExportMenuItem("ACNL (PBL)...", frame, source, ACEncoder.ACNL_PBL);
		if (source instanceof ACNLFile) {
			nl1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY));
			nl2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.ALT_MASK));
			nl3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK));
			nl4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		}
		boolean nlok = (frame != null && source != null && isCompatible(source, ACNLFile.class));
		nl1.setEnabled(nlok); add(nl1);
		nl2.setEnabled(nlok); add(nl2);
		nl3.setEnabled(nlok); add(nl3);
		nl4.setEnabled(nlok); add(nl4);
		
		addSeparator();
		JMenuItem ww1 = new ExportMenuItem("ACWW (.acww)...", frame, source, ACEncoder.ACWW);
		JMenuItem ww2 = new ExportMenuItem("ACWW (image)...", frame, source, ACEncoder.ACWW_IMAGE);
		JMenuItem ww3 = new ExportMenuItem("ACWW (PBL)...", frame, source, ACEncoder.ACWW_PBL);
		if (source instanceof ACWWFile) {
			ww1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY));
			ww2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.ALT_MASK));
			ww3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
		}
		boolean wwok = (frame != null && source != null && isCompatible(source, ACWWFile.class));
		ww1.setEnabled(wwok); add(ww1);
		ww2.setEnabled(wwok); add(ww2);
		ww3.setEnabled(wwok); add(ww3);
	}
	
	private static boolean isCompatible(Object source, Class<? extends ACBaseFile> cls) {
		if (source instanceof ACBaseFile) {
			if (((ACBaseFile)source).isProPattern()) {
				return cls.isInstance(source);
			}
		}
		return true;
	}
}
