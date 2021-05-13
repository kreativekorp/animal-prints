package com.kreative.acpattern.gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.acpattern.ACBaseFile;

public class ACMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public ACMenuBar(final Frame frame, final Object source) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new NewMenuItem());
		fileMenu.add(new OpenMenuItem());
		fileMenu.add(new ConvertMenu(frame, source));
		if (source instanceof ACBaseFile) fileMenu.add(new ExportMenu(frame, source));
		fileMenu.add(new CloseMenuItem(frame));
		if (!IS_MAC_OS) fileMenu.add(new ExitMenuItem());
		add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new CopyImageMenuItem(source));
		if (frame instanceof Updatable && source instanceof ACBaseFile) {
			editMenu.add(new PasteImageMenuItem((ACBaseFile)source, (Updatable)frame));
			editMenu.add(new PasteUnoptimizedMenuItem((ACBaseFile)source, (Updatable)frame));
			editMenu.add(new EditInfoMenuItem(frame, (ACBaseFile)source, (Updatable)frame));
		}
		add(editMenu);
	}
	
	public static class NewMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewMenuItem() {
			super("New from Clipboard");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.newFromClipboard();
				}
			});
		}
	}
	
	public static class OpenMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenMenuItem() {
			super("Open...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.open();
				}
			});
		}
	}
	
	public static class CloseMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CloseMenuItem(final Window window) {
			super("Close Window");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				}
			});
		}
	}
	
	public static class ExitMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExitMenuItem() {
			super("Exit");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.gc();
					for (Window window : Window.getWindows()) {
						if (window.isVisible()) {
							window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
							if (window.isVisible()) return;
						}
					}
					System.exit(0);
				}
			});
		}
	}
	
	public static class CopyImageMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyImageMenuItem(final Object o) {
			super("Copy Image");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final BufferedImage image;
					if (o instanceof BufferedImage) image = (BufferedImage)o;
					else if (o instanceof ACBaseFile) image = ((ACBaseFile)o).getImage();
					else return;
					Main.copyImage(image);
				}
			});
		}
	}
	
	public static class PasteImageMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteImageMenuItem(final ACBaseFile ac, final Updatable u) {
			super("Paste Image");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BufferedImage image = Main.pasteImage();
					if (image != null) {
						ac.setImage(image, true);
						u.update();
					}
				}
			});
		}
	}
	
	public static class PasteUnoptimizedMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteUnoptimizedMenuItem(final ACBaseFile ac, final Updatable u) {
			super("Paste & Keep Palette");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BufferedImage image = Main.pasteImage();
					if (image != null) {
						ac.setImage(image, false);
						u.update();
					}
				}
			});
		}
	}
	
	public static class EditInfoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditInfoMenuItem(final Frame frame, final ACBaseFile ac, final Updatable u) {
			super("Edit Info...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ACInfoDialog dlg = new ACInfoDialog(frame, "Edit Info");
					dlg.getInfoPanel().pullFrom(ac);
					if (dlg.showDialog()) {
						dlg.getInfoPanel().pushTo(ac);
						u.update();
					}
				}
			});
		}
	}
	
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static final boolean IS_MAC_OS;
	static {
		boolean isMacOS;
		try { isMacOS = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
		catch (Exception e) { isMacOS = false; }
		IS_MAC_OS = isMacOS;
	}
}
