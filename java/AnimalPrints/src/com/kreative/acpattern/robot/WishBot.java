package com.kreative.acpattern.robot;

import java.io.IOException;
import java.util.List;

import com.kreative.acpattern.robot.SwitchController.Button;
import com.kreative.acpattern.robot.SwitchController.DPad;

public class WishBot {
	public static void main(String[] args) {
		List<SerialPort> ports = SerialPort.listPorts();
		if (ports.isEmpty()) {
			System.err.println("No serial ports found");
			return;
		}
		if (args.length != 1) {
			System.out.println("Must specify a serial port (listed below)");
			for (SerialPort p : ports) {
				System.out.println(p.getName());
			}
			return;
		}
		
		// Open the serial port
		String portName = args[0];
		SerialPort port = null;
		for (SerialPort p : ports) {
			if (p.getName().equalsIgnoreCase(portName)) {
				port = p;
				break;
			}
		}
		if (port == null) {
			System.err.println("Unknown serial port: " + portName);
			return;
		}
		try { port.open(9600); }
		catch (IOException e) { e.printStackTrace(); return; }
		SwitchController ctrl = new SwitchController(port.getOutputStream());
		ctrl.sleep(1000);
		
		// Deselect tool
		ctrl.clickDPad(DPad.DOWN);
		ctrl.clickDPad(DPad.DOWN);
		ctrl.clickDPad(DPad.DOWN);
		ctrl.clickDPad(DPad.DOWN);
		
		// Highest camera angle
		ctrl.nudgeRightStick(0, -1);
		ctrl.nudgeRightStick(0, -1);
		ctrl.nudgeRightStick(0, -1);
		ctrl.nudgeRightStick(0, -1);
		
		// Mash A
		while (!Thread.interrupted()) {
			ctrl.pressButton(Button.A);
			ctrl.sleep(500);
			ctrl.releaseButton(Button.A);
			ctrl.sleep(500);
		}
		
		// Close the serial port
		ctrl.sleep(1000);
		try { port.close(); }
		catch (IOException e) {}
		System.exit(0);
	}
}
