package com.kreative.acpattern.robot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import com.kreative.acpattern.ACNHFile;
import com.kreative.acpattern.robot.SwitchController.Button;
import com.kreative.acpattern.robot.SwitchController.DPad;

public class PatternBot {
	public static void main(String[] args) {
		List<SerialPort> ports = SerialPort.listPorts();
		if (ports.isEmpty()) {
			System.err.println("No serial ports found");
			return;
		}
		if (args.length != 2) {
			System.out.println("Must specify a serial port (listed below) followed by an acnh file");
			for (SerialPort p : ports) {
				System.out.println(p.getName());
			}
			return;
		}
		
		// Read the ACNH file
		ACNHFile acnh;
		try {
			File file = new File(args[1]);
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (int b = in.read(); b >= 0; b = in.read()) out.write(b);
			in.close();
			out.close();
			acnh = new ACNHFile(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
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
		
		// Wait for confirmation
		System.out.println("Make sure:");
		System.out.println("1. the custom design editor is open");
		System.out.println("2. the leftmost color is selected");
		System.out.println("3. the pencil tool is selected");
		System.out.println("4. the smallest pencil size is selected");
		System.out.println("and press enter.");
		Scanner scan = new Scanner(System.in);
		if (scan.hasNextLine()) { scan.nextLine(); }
		else { scan.close(); return; }
		scan.close();
		
		// Send the pattern
		ensureConnected(ctrl);
		if (sendPalette(acnh, ctrl)) {
			if (sendPattern(acnh, ctrl)) {
				System.out.println("Done!");
			}
		}
		
		// Close the serial port
		ctrl.sleep(1000);
		try { port.close(); }
		catch (IOException e) {}
		System.exit(0);
	}
	
	public static void ensureConnected(SwitchController ctrl) {
		// Sometimes when the controller is first connected the first
		// input gets dropped for some reason. This is really annoying.
		ctrl.clickButton(Button.A);
		ctrl.clickButton(Button.A);
		ctrl.clickButton(Button.A);
		ctrl.clickButton(Button.A);
	}
	
	public static boolean sendPalette(ACNHFile acnh, SwitchController ctrl) {
		ctrl.clickButton(Button.X); // open tools
		ctrl.clickDPad(DPad.UP); // pencil tool -> color palette tool
		ctrl.clickDPad(DPad.RIGHT); // color palette tool -> color tool
		ctrl.clickButton(Button.A); // open color picker
		for (int rgb : acnh.getPaletteRGB(new int[15])) {
			if (Thread.interrupted()) return false;
			int r = (rgb >> 16) & 0xFF;
			int g = (rgb >>  8) & 0xFF;
			int b = (rgb >>  0) & 0xFF;
			int[] hvb = ACNHFile.RGBtoHVB(r, g, b, null, null);
			if (!setSlider(ctrl, hvb[0], 30)) return false; // set hue
			ctrl.clickDPad(DPad.DOWN); // hue -> vividness
			if (!setSlider(ctrl, hvb[1], 15)) return false; // set vividness
			ctrl.clickDPad(DPad.DOWN); // vividness -> brightness
			if (!setSlider(ctrl, hvb[2], 15)) return false; // set brightness
			ctrl.clickDPad(DPad.DOWN); // brightness -> hue
			ctrl.clickButton(Button.R); // next color
		}
		ctrl.clickButton(Button.A); // close color picker while saving changes
		ctrl.sleep(500); // wait for color picker closing animation
		ctrl.clickButton(Button.B); // close tools
		return true;
	}
	
	private static boolean setSlider(SwitchController ctrl, int val, int num) {
		if ((val * 2) < num) {
			// Move slider all the way left
			for (int i = 0; i < num; i++) {
				if (Thread.interrupted()) return false;
				ctrl.clickDPad(DPad.LEFT);
			}
			// Move right until at specified value
			for (int i = 0; i < val; i++) {
				if (Thread.interrupted()) return false;
				ctrl.clickDPad(DPad.RIGHT);
			}
		} else {
			// Move slider all the way right
			for (int i = 0; i < num; i++) {
				if (Thread.interrupted()) return false;
				ctrl.clickDPad(DPad.RIGHT);
			}
			// Move left until at specified value
			for (int i = num - 1; i > val; i--) {
				if (Thread.interrupted()) return false;
				ctrl.clickDPad(DPad.LEFT);
			}
		}
		return true;
	}
	
	public static boolean sendPattern(ACNHFile acnh, SwitchController ctrl) {
		if (acnh.isProPattern()) {
			throw new IllegalArgumentException("Cannot draw pro patterns yet");
		}
		// Move to upper left corner
		for (int i = 0; i < 32; i++) {
			if (Thread.interrupted()) return false;
			ctrl.clickDPad(DPad.UP_LEFT);
		}
		// Paint pattern
		int lastColorIndex = 0;
		int[][] pbl = acnh.getPBLImageData(0);
		for (int y = 0; y < pbl.length; y++) {
			if (Thread.interrupted()) return false;
			// Move to next row
			if (y != 0) ctrl.clickDPad(DPad.DOWN);
			// Paint current row
			int[] row = pbl[y];
			int start = ((y & 1) == 0) ? 0 : 31;
			int end = ((y & 1) == 0) ? 32 : -1;
			int step = ((y & 1) == 0) ? +1 : -1;
			DPad dir = ((y & 1) == 0) ? DPad.RIGHT : DPad.LEFT;
			for (int x = start; x != end; x += step) {
				if (Thread.interrupted()) return false;
				// Move to next pixel
				if (x != start) ctrl.clickDPad(dir);
				// Select the color for this pixel
				int dist = (row[x] - lastColorIndex) & 0xF;
				if (dist >= 8) dist -= 16;
				while (dist < 0) {
					if (Thread.interrupted()) return false;
					ctrl.clickButton(Button.L);
					dist++;
				}
				while (dist > 0) {
					if (Thread.interrupted()) return false;
					ctrl.clickButton(Button.R);
					dist--;
				}
				// Paint current pixel
				ctrl.clickButton(Button.A);
				lastColorIndex = row[x];
			}
		}
		return true;
	}
}
