package com.kreative.acpattern.robot;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class SwitchControl {
	public static void main(String[] args) {
		List<SerialPort> ports = SerialPort.listPorts();
		if (ports.isEmpty()) {
			System.err.println("No serial ports found");
			return;
		}
		if (args.length == 0) {
			System.out.println("Specify a serial port:");
			for (SerialPort p : ports) {
				System.out.println(p.getName());
			}
			return;
		}
		
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
		
		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				String command = args[i].trim();
				if (command.startsWith("#") || command.startsWith("--")) continue;
				command = command.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
				if (command.equals("EXIT") || command.equals("QUIT") || command.equals("BYE")) {
					break;
				} else if (command.equals("RESET")) {
					ctrl.sleep(1000);
					try { port.close(); } catch (IOException e) {}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					try { port.open(9600); }
					catch (IOException e) { e.printStackTrace(); return; }
					ctrl = new SwitchController(port.getOutputStream());
					ctrl.sleep(1000);
				} else {
					parseCommand(ctrl, command);
				}
			}
		} else {
			Scanner scan = new Scanner(System.in);
			while (scan.hasNextLine()) {
				String command = scan.nextLine().trim();
				if (command.startsWith("#") || command.startsWith("--")) continue;
				command = command.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
				if (command.equals("EXIT") || command.equals("QUIT") || command.equals("BYE")) {
					break;
				} else if (command.equals("RESET")) {
					ctrl.sleep(1000);
					try { port.close(); } catch (IOException e) {}
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					try { port.open(9600); }
					catch (IOException e) { e.printStackTrace(); scan.close(); return; }
					ctrl = new SwitchController(port.getOutputStream());
					ctrl.sleep(1000);
				} else {
					parseCommand(ctrl, command);
				}
			}
			scan.close();
		}
		
		ctrl.sleep(1000);
		try { port.close(); }
		catch (IOException e) {}
		System.exit(0);
	}
	
	private static void parseCommand(SwitchController ctrl, String command) {
		if (command.equals("CONNECT") || command.equals("PAIR")) {
			ctrl.pairController();
		}
		if (command.equals("WAIT") || command.equals("SLEEP")) {
			ctrl.sleep(1000);
		}
		
		if (command.equals("A")) {
			ctrl.clickButton(SwitchController.Button.A);
		}
		if (command.equals("B")) {
			ctrl.clickButton(SwitchController.Button.B);
		}
		if (command.equals("X")) {
			ctrl.clickButton(SwitchController.Button.X);
		}
		if (command.equals("Y")) {
			ctrl.clickButton(SwitchController.Button.Y);
		}
		if (command.equals("L") || command.equals("LB")) {
			ctrl.clickButton(SwitchController.Button.L);
		}
		if (command.equals("R") || command.equals("RB")) {
			ctrl.clickButton(SwitchController.Button.R);
		}
		if (command.equals("ZL") || command.equals("LT")) {
			ctrl.clickButton(SwitchController.Button.ZL);
		}
		if (command.equals("ZR") || command.equals("RT")) {
			ctrl.clickButton(SwitchController.Button.ZR);
		}
		if (command.equals("PLUS") || command.equals("START")) {
			ctrl.clickButton(SwitchController.Button.PLUS);
		}
		if (command.equals("MINUS") || command.equals("SELECT")) {
			ctrl.clickButton(SwitchController.Button.MINUS);
		}
		if (command.equals("LSTICK") || command.equals("L3")) {
			ctrl.clickButton(SwitchController.Button.LSTICK);
		}
		if (command.equals("RSTICK") || command.equals("R3")) {
			ctrl.clickButton(SwitchController.Button.RSTICK);
		}
		if (command.equals("HOME")) {
			ctrl.clickButton(SwitchController.Button.HOME);
		}
		if (command.equals("CAPTURE")) {
			ctrl.clickButton(SwitchController.Button.CAPTURE);
		}
		
		if (command.equals("UP") || command.equals("NORTH") || command.equals("N")) {
			ctrl.clickDPad(SwitchController.DPad.UP);
		}
		if (command.equals("UPRIGHT") || command.equals("NORTHEAST") || command.equals("NE")) {
			ctrl.clickDPad(SwitchController.DPad.UP_RIGHT);
		}
		if (command.equals("RIGHT") || command.equals("EAST") || command.equals("E")) {
			ctrl.clickDPad(SwitchController.DPad.RIGHT);
		}
		if (command.equals("DOWNRIGHT") || command.equals("SOUTHEAST") || command.equals("SE")) {
			ctrl.clickDPad(SwitchController.DPad.DOWN_RIGHT);
		}
		if (command.equals("DOWN") || command.equals("SOUTH") || command.equals("S")) {
			ctrl.clickDPad(SwitchController.DPad.DOWN);
		}
		if (command.equals("DOWNLEFT") || command.equals("SOUTHWEST") || command.equals("SW")) {
			ctrl.clickDPad(SwitchController.DPad.DOWN_LEFT);
		}
		if (command.equals("LEFT") || command.equals("WEST") || command.equals("W")) {
			ctrl.clickDPad(SwitchController.DPad.LEFT);
		}
		if (command.equals("UPLEFT") || command.equals("NORTHWEST") || command.equals("NW")) {
			ctrl.clickDPad(SwitchController.DPad.UP_LEFT);
		}
		
		if (command.equals("LUP") || command.equals("LNORTH") || command.equals("LN")) {
			ctrl.nudgeLeftStick(0, -1);
		}
		if (command.equals("LUPRIGHT") || command.equals("LNORTHEAST") || command.equals("LNE")) {
			ctrl.nudgeLeftStick(1, -1);
		}
		if (command.equals("LRIGHT") || command.equals("LEAST") || command.equals("LE")) {
			ctrl.nudgeLeftStick(1, 0);
		}
		if (command.equals("LDOWNRIGHT") || command.equals("LSOUTHEAST") || command.equals("LSE")) {
			ctrl.nudgeLeftStick(1, 1);
		}
		if (command.equals("LDOWN") || command.equals("LSOUTH") || command.equals("LS")) {
			ctrl.nudgeLeftStick(0, 1);
		}
		if (command.equals("LDOWNLEFT") || command.equals("LSOUTHWEST") || command.equals("LSW")) {
			ctrl.nudgeLeftStick(-1, 1);
		}
		if (command.equals("LLEFT") || command.equals("LWEST") || command.equals("LW")) {
			ctrl.nudgeLeftStick(-1, 0);
		}
		if (command.equals("LUPLEFT") || command.equals("LNORTHWEST") || command.equals("LNW")) {
			ctrl.nudgeLeftStick(-1, -1);
		}
		
		if (command.equals("RUP") || command.equals("RNORTH") || command.equals("RN")) {
			ctrl.nudgeRightStick(0, -1);
		}
		if (command.equals("RUPRIGHT") || command.equals("RNORTHEAST") || command.equals("RNE")) {
			ctrl.nudgeRightStick(1, -1);
		}
		if (command.equals("RRIGHT") || command.equals("REAST") || command.equals("RE")) {
			ctrl.nudgeRightStick(1, 0);
		}
		if (command.equals("RDOWNRIGHT") || command.equals("RSOUTHEAST") || command.equals("RSE")) {
			ctrl.nudgeRightStick(1, 1);
		}
		if (command.equals("RDOWN") || command.equals("RSOUTH") || command.equals("RS")) {
			ctrl.nudgeRightStick(0, 1);
		}
		if (command.equals("RDOWNLEFT") || command.equals("RSOUTHWEST") || command.equals("RSW")) {
			ctrl.nudgeRightStick(-1, 1);
		}
		if (command.equals("RLEFT") || command.equals("RWEST") || command.equals("RW")) {
			ctrl.nudgeRightStick(-1, 0);
		}
		if (command.equals("RUPLEFT") || command.equals("RNORTHWEST") || command.equals("RNW")) {
			ctrl.nudgeRightStick(-1, -1);
		}
	}
}
