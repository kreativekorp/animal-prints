package com.kreative.acpattern;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class ACQRDecoderImpl {
	private final BufferedImage srcImage;
	private final int frameIndex;
	private final int frameCount;
	private final int parity;
	private final byte[] data;
	
	public ACQRDecoderImpl(BufferedImage image) {
		try {
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			HashMap<DecodeHintType,Object> hints = new HashMap<DecodeHintType,Object>();
			Object possibleFormats = Arrays.asList(BarcodeFormat.QR_CODE);
			hints.put(DecodeHintType.POSSIBLE_FORMATS, possibleFormats);
			Result r = new MultiFormatReader().decode(bitmap, hints);
			byte[] data = r.getRawBytes();
			if (
				data.length >= 0x26F &&
				(data[0] & 0xFF) == 0x40 &&
				(data[1] & 0xFF) == 0x26 &&
				(data[2] & 0xF0) == 0xC0
			) {
				// basic design
				this.srcImage = image;
				this.frameIndex = 0;
				this.frameCount = 1;
				this.parity = 0;
				this.data = new byte[0x26C];
				for (int i = 0; i < 0x26C; i++) {
					int h = (data[i + 2] & 0x0F) << 4;
					int l = (data[i + 3] & 0xF0) >> 4;
					this.data[i] = (byte)(h | l);
				}
				return;
			}
			if (
				data.length >= 0x222 &&
				(data[0] & 0xFF) >= 0x30 &&
				(data[0] & 0xFF) <= 0x33 &&
				(data[1] & 0xF0) == 0x30 &&
				(data[2] & 0x0F) == 0x04 &&
				(data[3] & 0xFF) == 0x02 &&
				(data[4] & 0xFF) == 0x1C
			) {
				// pro design
				this.srcImage = image;
				this.frameIndex = (data[0] & 0x0F);
				this.frameCount = 4;
				this.parity = ((data[1] & 0x0F) << 4) | ((data[2] & 0xF0) >> 4);
				this.data = new byte[0x21C];
				for (int i = 0; i < 0x21C; i++) {
					this.data[i] = data[i + 5];
				}
				return;
			}
			throw new IllegalArgumentException("not an AC QR code");
		} catch (NotFoundException e) {
			throw new IllegalArgumentException("no code in image");
		}
	}
	
	public BufferedImage getImage() { return srcImage; }
	public int getFrameIndex() { return frameIndex; }
	public int getFrameCount() { return frameCount; }
	public int getQRParity() { return parity; }
	public byte[] getQRData() { return data; }
}
