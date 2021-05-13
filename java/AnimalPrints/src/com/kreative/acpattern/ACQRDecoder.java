package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ACQRDecoder {
	private final BufferedImage srcImage;
	private final int frameIndex;
	private final int frameCount;
	private final int parity;
	private final byte[] data;
	
	public ACQRDecoder(BufferedImage image) {
		try {
			Class<?> cls = Class.forName("com.kreative.acpattern.ACQRDecoderImpl");
			Constructor<?> con = cls.getConstructor(BufferedImage.class);
			Object obj = con.newInstance(image);
			this.srcImage = image;
			this.frameIndex = ((Number)cls.getMethod("getFrameIndex").invoke(obj)).intValue();
			this.frameCount = ((Number)cls.getMethod("getFrameCount").invoke(obj)).intValue();
			this.parity = ((Number)cls.getMethod("getQRParity").invoke(obj)).intValue();
			this.data = (byte[])cls.getMethod("getQRData").invoke(obj);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (SecurityException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (InstantiationException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("no QR decoder found");
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("no QR decoder found");
		}
	}
	
	public BufferedImage getImage() { return srcImage; }
	public int getFrameIndex() { return frameIndex; }
	public int getFrameCount() { return frameCount; }
	public int getQRParity() { return parity; }
	public byte[] getQRData() { return data; }
}
