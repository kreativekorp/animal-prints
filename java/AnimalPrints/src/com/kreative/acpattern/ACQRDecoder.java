package com.kreative.acpattern;

import java.awt.image.BufferedImage;

public class ACQRDecoder {
	private final BufferedImage srcImage;
	private final int frameIndex;
	private final int frameCount;
	private final int parity;
	private final byte[] data;
	
	public ACQRDecoder(BufferedImage image) {
		try {
			Class<?> cls = Class.forName("com.kreative.acpattern.ACQRDecoderImpl");
			Object obj = cls.getConstructor(BufferedImage.class).newInstance(image);
			this.srcImage = image;
			this.frameIndex = ((Number)cls.getMethod("getFrameIndex").invoke(obj)).intValue();
			this.frameCount = ((Number)cls.getMethod("getFrameCount").invoke(obj)).intValue();
			this.parity = ((Number)cls.getMethod("getQRParity").invoke(obj)).intValue();
			this.data = (byte[])cls.getMethod("getQRData").invoke(obj);
		} catch (Throwable t) {
			throw new IllegalStateException("no QR decoder found");
		}
	}
	
	public BufferedImage getImage() { return srcImage; }
	public int getFrameIndex() { return frameIndex; }
	public int getFrameCount() { return frameCount; }
	public int getQRParity() { return parity; }
	public byte[] getQRData() { return data; }
}
