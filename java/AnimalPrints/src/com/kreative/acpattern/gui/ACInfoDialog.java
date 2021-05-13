package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class ACInfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final ACInfoPanel panel = new ACInfoPanel();
	private boolean confirmed = false;
	
	public ACInfoDialog(Dialog parent, String title) {
		super(parent, title);
		build();
	}
	
	public ACInfoDialog(Frame parent, String title) {
		super(parent, title);
		build();
	}
	
	public ACInfoDialog(Window parent, String title) {
		super(parent, title);
		build();
	}
	
	private void build() {
		JButton ok = new JButton("OK");
		JButton ng = new JButton("Cancel");
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(ok);
		buttons.add(ng);
		
		JPanel main = new JPanel(new BorderLayout(12, 12));
		main.add(panel, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.PAGE_END);
		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(main);
		setDefaultButton(getRootPane(), ok);
		setCancelButton(getRootPane(), ng);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmed = true;
				dispose();
			}
		});
		
		ng.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmed = false;
				dispose();
			}
		});
	}
	
	public ACInfoPanel getInfoPanel() { return panel; }
	public boolean isConfirmed() { return confirmed; }
	
	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return confirmed;
	}
	
	private static void setDefaultButton(final JRootPane rp, final JButton b) {
		rp.setDefaultButton(b);
	}
	
	private static void setCancelButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		rp.getActionMap().put("cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
}
