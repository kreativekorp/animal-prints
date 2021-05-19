package com.kreative.acpattern.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import com.kreative.acpattern.ACNHFile;
import com.kreative.acpattern.robot.PatternBot;
import com.kreative.acpattern.robot.SerialPort;
import com.kreative.acpattern.robot.SwitchController;

public class SendToACNHFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ACHeader header = new ACHeader();
	private final JComboBox portSelector = new JComboBox(new Object[0]);
	private final JButton refreshButton = new JButton("Refresh");
	private final JButton openButton = new JButton("Open");
	private final JButton pairButton = new JButton("Pair");
	private final JButton sendButton = new JButton("Send");
	private final JButton stopButton = new JButton("Stop");
	private final JButton closeButton = new JButton("Close");
	private final JMenuItem refreshMI = new JMenuItem("Refresh");
	private final JMenuItem openMI = new JMenuItem("Open");
	private final JMenuItem pairMI = new JMenuItem("Pair");
	private final JMenuItem sendMI = new JMenuItem("Send");
	private final JMenuItem stopMI = new JMenuItem("Stop");
	private final JMenuItem closeMI = new JMenuItem("Close");
	
	private ACNHFile acnh = null;
	private SerialPort port = null;
	private SwitchController ctrl = null;
	private Thread ctrlThread = null;
	
	public SendToACNHFrame() {
		super("Send to ACNH");
		doRefresh();
		doClose();
		
		// Content Pane
		
		JPanel selectorPanel = new JPanel(new BorderLayout(4,4));
		selectorPanel.add(new JLabel("Select a serial port:"), BorderLayout.PAGE_START);
		selectorPanel.add(portSelector, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1,4,4));
		buttonPanel.add(refreshButton);
		buttonPanel.add(openButton);
		buttonPanel.add(pairButton);
		buttonPanel.add(sendButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(closeButton);
		
		JPanel labelPanel = new JPanel(new GridLayout(0,1,4,4));
		labelPanel.add(new JLabel("Update the list of serial ports."));
		labelPanel.add(new JLabel("Open the selected serial port."));
		labelPanel.add(new JLabel("Press L + R on the controller."));
		labelPanel.add(new JLabel("Send the pattern to ACNH."));
		labelPanel.add(new JLabel("Stop the current operation."));
		labelPanel.add(new JLabel("Close the selected serial port."));
		
		JPanel buttonLabelPanel = new JPanel(new BorderLayout(8,8));
		buttonLabelPanel.add(buttonPanel, BorderLayout.LINE_START);
		buttonLabelPanel.add(labelPanel, BorderLayout.CENTER);
		
		JPanel controlPanel = new JPanel(new BorderLayout(12,12));
		controlPanel.add(selectorPanel, BorderLayout.PAGE_START);
		controlPanel.add(buttonLabelPanel, BorderLayout.CENTER);
		controlPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JPanel headerControlPanel = new JPanel(new BorderLayout());
		headerControlPanel.add(header, BorderLayout.PAGE_START);
		headerControlPanel.add(controlPanel, BorderLayout.CENTER);
		
		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.PAGE_AXIS));
		helpPanel.add(new JLabel(new ImageIcon(
			SendToACNHFrame.class.getResource("nsw-pattern.png")
		)));
		helpPanel.add(Box.createVerticalStrut(4));
		helpPanel.add(new JLabel(
			"<html>Open the custom design editor.<br>" +
			"a. Make sure the leftmost color is selected.<br>" +
			"b. Make sure the pencil tool is selected.<br>" +
			"c. Make sure the smallest size is selected.</html>"
		));
		helpPanel.add(Box.createVerticalStrut(20));
		helpPanel.add(new JLabel(new ImageIcon(
			SendToACNHFrame.class.getResource("nsw-pair.png")
		)));
		helpPanel.add(Box.createVerticalStrut(4));
		helpPanel.add(new JLabel(
			"If you see this screen, click the Pair button."
		));
		helpPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topWrap(helpPanel), BorderLayout.LINE_START);
		mainPanel.add(topWrap(headerControlPanel), BorderLayout.CENTER);
		
		// Menu Bar
		
		JMenu firmwareMenu = new JMenu("Save Firmware");
		firmwareMenu.add(new SaveFirmwareMenuItem(
			"for ATmega16u2 (Arduino Uno, Arduino Mega)...",
			SwitchController.Firmware.M16U2
		));
		firmwareMenu.add(new SaveFirmwareMenuItem(
			"for ATmega32u4 (Sparkfun Pro Micro, Adafruit ItsyBitsy)...",
			SwitchController.Firmware.M32U4
		));
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ACMenuBar.NewMenuItem());
		fileMenu.add(new ACMenuBar.OpenMenuItem());
		fileMenu.add(firmwareMenu);
		fileMenu.add(new ACMenuBar.CloseMenuItem(this));
		if (!ACMenuBar.IS_MAC_OS) fileMenu.add(new ACMenuBar.ExitMenuItem());
		
		refreshMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ACMenuBar.SHORTCUT_KEY));
		openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ACMenuBar.SHORTCUT_KEY));
		pairMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ACMenuBar.SHORTCUT_KEY));
		sendMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ACMenuBar.SHORTCUT_KEY));
		stopMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ACMenuBar.SHORTCUT_KEY));
		closeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ACMenuBar.SHORTCUT_KEY));
		
		JMenu serialMenu = new JMenu("Serial");
		serialMenu.add(refreshMI);
		serialMenu.add(openMI);
		serialMenu.add(pairMI);
		serialMenu.add(sendMI);
		serialMenu.add(stopMI);
		serialMenu.add(closeMI);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(serialMenu);
		
		// Finish Up
		
		setContentPane(mainPanel);
		setJMenuBar(menuBar);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		
		refreshButton.addActionListener(new RefreshActionListener());
		openButton.addActionListener(new OpenActionListener());
		pairButton.addActionListener(new PairActionListener());
		sendButton.addActionListener(new SendActionListener());
		stopButton.addActionListener(new StopActionListener());
		closeButton.addActionListener(new CloseActionListener());
		refreshMI.addActionListener(new RefreshActionListener());
		openMI.addActionListener(new OpenActionListener());
		pairMI.addActionListener(new PairActionListener());
		sendMI.addActionListener(new SendActionListener());
		stopMI.addActionListener(new StopActionListener());
		closeMI.addActionListener(new CloseActionListener());
	}
	
	public void setACNHFile(ACNHFile acnh) {
		header.setSource(this.acnh = acnh);
		pack();
	}
	
	private void doRefresh() {
		Object selection = portSelector.getSelectedItem();
		Object[] ports = SerialPort.listPorts().toArray();
		portSelector.setModel(new DefaultComboBoxModel(ports));
		portSelector.setSelectedItem(selection);
	}
	
	private void doOpen() {
		port = (SerialPort)portSelector.getSelectedItem();
		if (port != null) {
			try {
				port.open(9600);
				ctrl = new SwitchController(port.getOutputStream());
				setOpenedState();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(
					SendToACNHFrame.this,
					"Could not open serial port because an error occurred: " + e,
					"Open",
					JOptionPane.ERROR_MESSAGE
				);
				doClose();
			}
		}
	}
	
	private void doClose() {
		if (port != null) {
			try { port.close(); }
			catch (IOException e) {}
		}
		port = null;
		ctrl = null;
		setClosedState();
	}
	
	private void doPair() {
		setStartedState();
		ctrlThread = new Thread() {
			@Override
			public void run() {
				if (ctrl != null) ctrl.pairController();
				setStoppedState();
				ctrlThread = null;
			}
		};
		ctrlThread.start();
	}
	
	private void doSend() {
		setStartedState();
		ctrlThread = new Thread() {
			@Override
			public void run() {
				if (ctrl != null && acnh != null) {
					PatternBot.ensureConnected(ctrl);
					if (PatternBot.sendPalette(acnh, ctrl)) {
						if (PatternBot.sendPattern(acnh, ctrl)) {
							JOptionPane.showMessageDialog(
								SendToACNHFrame.this,
								"Pattern sent successfully.",
								"Send",
								JOptionPane.INFORMATION_MESSAGE
							);
						}
					}
				}
				setStoppedState();
				ctrlThread = null;
			}
		};
		ctrlThread.start();
	}
	
	private void doStop() {
		if (ctrlThread != null) ctrlThread.interrupt();
	}
	
	private void setOpenedState() {
		refreshButton.setEnabled(false); refreshMI.setEnabled(false);
		openButton.setEnabled(false); openMI.setEnabled(false);
		pairButton.setEnabled(true); pairMI.setEnabled(true);
		sendButton.setEnabled(true); sendMI.setEnabled(true);
		stopButton.setEnabled(false); stopMI.setEnabled(false);
		closeButton.setEnabled(true); closeMI.setEnabled(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void setClosedState() {
		refreshButton.setEnabled(true); refreshMI.setEnabled(true);
		openButton.setEnabled(true); openMI.setEnabled(true);
		pairButton.setEnabled(false); pairMI.setEnabled(false);
		sendButton.setEnabled(false); sendMI.setEnabled(false);
		stopButton.setEnabled(false); stopMI.setEnabled(false);
		closeButton.setEnabled(false); closeMI.setEnabled(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	private void setStartedState() {
		pairButton.setEnabled(false); pairMI.setEnabled(false);
		sendButton.setEnabled(false); sendMI.setEnabled(false);
		stopButton.setEnabled(true); stopMI.setEnabled(true);
		closeButton.setEnabled(false); closeMI.setEnabled(false);
	}
	
	private void setStoppedState() {
		pairButton.setEnabled(true); pairMI.setEnabled(true);
		sendButton.setEnabled(true); sendMI.setEnabled(true);
		stopButton.setEnabled(false); stopMI.setEnabled(false);
		closeButton.setEnabled(true); closeMI.setEnabled(true);
	}
	
	private class RefreshActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doRefresh();
		}
	}
	
	private class OpenActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doOpen();
		}
	}
	
	private class PairActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doPair();
		}
	}
	
	private class SendActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doSend();
		}
	}
	
	private class StopActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doStop();
		}
	}
	
	private class CloseActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doClose();
		}
	}
	
	private class SaveFirmwareMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveFirmwareMenuItem(final String name, final SwitchController.Firmware fw) {
			super(name);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FileDialog fd = new FileDialog(
						SendToACNHFrame.this,
						"Save Firmware",
						FileDialog.SAVE
					);
					fd.setFile("Joystick_" + fw.name().toLowerCase() + ".hex");
					fd.setVisible(true);
					if (fd.getDirectory() == null || fd.getFile() == null) return;
					File file = new File(fd.getDirectory(), fd.getFile());
					try {
						FileOutputStream out = new FileOutputStream(file);
						SwitchController.writeFirmware(fw, out);
						out.flush();
						out.close();
					} catch (Exception ioe) {
						JOptionPane.showMessageDialog(
							SendToACNHFrame.this,
							"Could not write to file: " + ioe,
							"Save Firmware",
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
	
	private static JPanel topWrap(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.PAGE_START);
		return p;
	}
}
