package com.kreative.acpattern;

import java.awt.image.BufferedImage;

public enum ACEncoder {
	IMAGE {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toImage(o)};
		}
	},
	ACNH {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACNHFile(o)};
		}
	},
	ACNH_IMAGE {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACNHFile(o).getImage()};
		}
	},
	ACNH_PBL {
		@Override
		public Object[] encode(Object o) {
			ACNHFile acnh = toACNHFile(o);
			Object[] out = new Object[acnh.getFrames()];
			for (int i = 0; i < out.length; i++) out[i] = acnh.getPBLCard(i).getCardImage();
			return out;
		}
	},
	ACNL {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACNLFile(o)};
		}
	},
	ACNL_IMAGE {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACNLFile(o).getImage()};
		}
	},
	ACNL_QR {
		@Override
		public Object[] encode(Object o) {
			ACNLFile acnl = toACNLFile(o);
			Object[] out = new Object[acnl.getFrames()];
			for (int i = 0; i < out.length; i++) out[i] = acnl.getQRCard(i).getCardImage();
			return out;
		}
	},
	ACNL_PBL {
		@Override
		public Object[] encode(Object o) {
			ACNLFile acnl = toACNLFile(o);
			Object[] out = new Object[acnl.getFrames()];
			for (int i = 0; i < out.length; i++) out[i] = acnl.getPBLCard(i).getCardImage();
			return out;
		}
	},
	ACWW {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACWWFile(o)};
		}
	},
	ACWW_IMAGE {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACWWFile(o).getImage()};
		}
	},
	ACWW_PBL {
		@Override
		public Object[] encode(Object o) {
			return new Object[]{toACWWFile(o).getPBLCard().getCardImage()};
		}
	};
	
	public abstract Object[] encode(Object o);
	
	@Override
	public String toString() {
		return name().replaceAll("[^A-Za-z0-9]+", "-").toLowerCase();
	}
	
	public static ACEncoder fromString(String s) {
		s = s.trim().replaceAll("[^A-Za-z0-9]+", "_").toUpperCase();
		return ACEncoder.valueOf(s);
	}
	
	private static BufferedImage toImage(Object o) {
		if (o instanceof BufferedImage) return (BufferedImage)o;
		if (o instanceof ACBaseFile) return ((ACBaseFile)o).getImage();
		throw new IllegalArgumentException("not an AC pattern object");
	}
	
	private static void setACData(ACBaseFile dst, Object src) {
		if (src instanceof ACBaseFile) {
			if (((ACBaseFile)src).isProPattern()) {
				throw new IllegalArgumentException("cannot convert pro patterns across versions");
			}
			dst.setTitle(((ACBaseFile)src).getTitle());
			dst.setTownName(((ACBaseFile)src).getTownName());
			dst.setCreatorName(((ACBaseFile)src).getCreatorName());
		}
		dst.setTownID(-1);
		dst.setCreatorID(-1);
	}
	
	private static ACNHFile toACNHFile(Object o) {
		if (o instanceof ACNHFile) return (ACNHFile)o;
		ACNHFile acnh = new ACNHFile();
		setACData(acnh, o);
		acnh.setImage(toImage(o), true);
		return acnh;
	}
	
	private static ACNLFile toACNLFile(Object o) {
		if (o instanceof ACNLFile) return (ACNLFile)o;
		ACNLFile acnl = new ACNLFile();
		setACData(acnl, o);
		acnl.setImage(toImage(o), true);
		return acnl;
	}
	
	private static ACWWFile toACWWFile(Object o) {
		if (o instanceof ACWWFile) return (ACWWFile)o;
		ACWWFile acww = new ACWWFile();
		setACData(acww, o);
		acww.setImage(toImage(o), true);
		return acww;
	}
}
