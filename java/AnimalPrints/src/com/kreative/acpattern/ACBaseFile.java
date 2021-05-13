package com.kreative.acpattern;

import java.awt.image.BufferedImage;

public interface ACBaseFile {
	public String getTitle();
	public void setTitle(String title);
	public int getCreatorID();
	public void setCreatorID(int id);
	public String getCreatorName();
	public void setCreatorName(String name);
	public int getTownID();
	public void setTownID(int id);
	public String getTownName();
	public void setTownName(String name);
	public BufferedImage getImage();
	public void setImage(BufferedImage image, boolean optimize);
	public byte[] getData();
	public void setData(byte[] data);
	public boolean isProPattern();
	public String getFileExtension();
}
