package com.kreative.acpattern.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;

public class SerialPortImpl extends SerialPort {
	public static List<SerialPort> listPorts() {
		List<SerialPort> ports = new ArrayList<SerialPort>();
		Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();
		while (en.hasMoreElements()) {
			CommPortIdentifier id = (CommPortIdentifier)en.nextElement();
			ports.add(new SerialPortImpl(id));
		}
		return ports;
	}
	
	private CommPortIdentifier portId;
	private CommPort port;
	private InputStream in;
	private OutputStream out;
	
	private SerialPortImpl(CommPortIdentifier portId) {
		this.portId = portId;
		this.port = null;
		this.in = null;
		this.out = null;
	}
	
	@Override
	public String getName() {
		return portId.getName();
	}
	
	@Override
	public synchronized boolean isOpen() {
		return (port != null && in != null && out != null);
	}
	
	@Override
	public synchronized void open(int speed) throws IOException {
		try {
			CommPort port = portId.open(SerialPortImpl.class.toString(), 1000);
			try { ((gnu.io.SerialPort)port).setSerialPortParams(speed, 8, 1, 0); }
			catch (Exception e) { e.printStackTrace(); }
			this.port = port;
			this.in = port.getInputStream();
			this.out = port.getOutputStream();
		} catch (PortInUseException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public synchronized InputStream getInputStream() {
		return in;
	}
	
	@Override
	public synchronized OutputStream getOutputStream() {
		return out;
	}
	
	@Override
	public synchronized void close() throws IOException {
		try { if (in != null) in.close(); } finally { in = null; }
		try { if (out != null) out.close(); } finally { out = null; }
		try { if (port != null) port.close(); } finally { port = null; }
	}
	
	@Override
	public String toString() {
		return portId.getName();
	}
}
