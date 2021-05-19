package com.kreative.acpattern.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SwitchController {
	public static enum Button {
		Y, B, A, X, L, R, ZL, ZR, MINUS, PLUS,
		LSTICK, RSTICK, HOME, CAPTURE;
	}
	
	public static enum DPad {
		UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN,
		DOWN_LEFT, LEFT, UP_LEFT, CENTER;
	}
	
	private final OutputStream out;
	private int buttons;
	private int dpad;
	private int lx;
	private int ly;
	private int rx;
	private int ry;
	private int vendorSpec;
	
	public SwitchController(OutputStream out) {
		this.out = out;
		reset();
	}
	
	public synchronized void reset() {
		this.buttons = 0;
		this.dpad = DPad.CENTER.ordinal();
		this.lx = 128;
		this.ly = 128;
		this.rx = 128;
		this.ry = 128;
		this.vendorSpec = 0;
		writePacket();
	}
	
	public synchronized void writePacket() {
		try {
			out.write(buttons >> 8);
			out.write(buttons >> 0);
			out.write(dpad);
			out.write(lx);
			out.write(ly);
			out.write(rx);
			out.write(ry);
			out.write(vendorSpec);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void pressButton(Button... buttons) {
		for (Button b : buttons) this.buttons |= (1 << b.ordinal());
		writePacket();
	}
	
	public synchronized void releaseButton(Button... buttons) {
		for (Button b : buttons) this.buttons &=~ (1 << b.ordinal());
		writePacket();
	}
	
	public synchronized void pressDPad(DPad dpad) {
		this.dpad = dpad.ordinal();
		writePacket();
	}
	
	public synchronized void releaseDPad() {
		this.dpad = DPad.CENTER.ordinal();
		writePacket();
	}
	
	public synchronized void moveLeftStick(float x, float y) {
		this.lx = 128 + (int)Math.round(Math.max(-1, Math.min(1, x)) * 127);
		this.ly = 128 + (int)Math.round(Math.max(-1, Math.min(1, y)) * 127);
		writePacket();
	}
	
	public synchronized void moveRightStick(float x, float y) {
		this.rx = 128 + (int)Math.round(Math.max(-1, Math.min(1, x)) * 127);
		this.ry = 128 + (int)Math.round(Math.max(-1, Math.min(1, y)) * 127);
		writePacket();
	}
	
	private int pressDuration = 50;
	private int releaseDuration = 50;
	
	public synchronized void setPressDuration(int millis) {
		this.pressDuration = millis;
	}
	
	public synchronized void setReleaseDuration(int millis) {
		this.releaseDuration = millis;
	}
	
	public synchronized void clickButton(Button... buttons) {
		pressButton(buttons);
		sleep(pressDuration);
		releaseButton(buttons);
		sleep(releaseDuration);
	}
	
	public synchronized void clickDPad(DPad dpad) {
		pressDPad(dpad);
		sleep(pressDuration);
		releaseDPad();
		sleep(releaseDuration);
	}
	
	public synchronized void nudgeLeftStick(float x, float y) {
		moveLeftStick(x, y);
		sleep(pressDuration);
		moveLeftStick(0, 0);
		sleep(releaseDuration);
	}
	
	public synchronized void nudgeRightStick(float x, float y) {
		moveRightStick(x, y);
		sleep(pressDuration);
		moveRightStick(0, 0);
		sleep(releaseDuration);
	}
	
	public synchronized void pairController() {
		pressButton(Button.L, Button.R);
		sleep(2000);
		releaseButton(Button.L, Button.R);
		sleep(1000);
		clickButton(Button.A);
	}
	
	public synchronized boolean sleep(long millis) {
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}
	
	public static enum Firmware {
		M16U2, M32U4;
	}
	
	public static void writeFirmware(Firmware fw, OutputStream out) throws IOException {
		String name = "Joystick_" + fw.name().toLowerCase() + ".hex";
		InputStream in = SwitchController.class.getResourceAsStream(name);
		for (int b = in.read(); b >= 0; b = in.read()) out.write(b);
		in.close();
	}
}
