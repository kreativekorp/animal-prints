package com.kreative.acpattern.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import com.kreative.acpattern.ACEncoder;

public class ConvertMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	private static final String CONVERTED = " (Converted)";
	
	public ConvertMenuItem(final String title, final Frame frame, final Object source, final ACEncoder format) {
		super(title);
		if (frame == null || source == null || format == null) {
			setEnabled(false);
		} else {
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String t = frame.getTitle();
						if (!t.endsWith(CONVERTED)) t += CONVERTED;
						Object[] dest = format.encode(source);
						for (Object o : dest) Main.openObject(t, o);
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(
							frame, "Could not convert pattern: " + ee,
							"Convert", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
}
