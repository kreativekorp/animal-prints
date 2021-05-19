package com.kreative.acpattern.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class SerialPort {
	@SuppressWarnings("unchecked")
	public static List<SerialPort> listPorts() {
		try {
			Class<?> cls = Class.forName("com.kreative.acpattern.robot.SerialPortImpl");
			Object ports = cls.getMethod("listPorts").invoke(null);
			return (List<SerialPort>)ports;
		} catch (Throwable t) {
			return Arrays.<SerialPort>asList();
		}
	}
	
	public abstract String getName();
	public abstract boolean isOpen();
	public abstract void open(int speed) throws IOException;
	public abstract InputStream getInputStream();
	public abstract OutputStream getOutputStream();
	public abstract void close() throws IOException;
}
