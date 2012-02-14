package de.xzise;

public final class ColorUtil {

	private ColorUtil() {}

	public static int getRGB(final int r, final int g, final int b) {
		return getRGB(r, g, b, 0xFF);
	}

	public static int getRGB(final int r, final int g, final int b, final int a) {
		return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
	}

	public static int getAlphaFromRGB(final int rgb) {
		return (rgb >> 24) & 0xFF;
	}

	public static int getRedFromRGB(final int rgb) {
		return (rgb >> 16) & 0xFF;
	}

	public static int getGreenFromRGB(final int rgb) {
		return (rgb >>  8) & 0xFF;
	}
	
	public static int getBlueFromRGB(final int rgb) {
		return (rgb >>  0) & 0xFF;
	}

	public static double getRGBPercentage(final int value) {
		return (value & 0xFF) / (double) 0xFF;
	}

	public static int getFromRGBPercentage(final double value) {
		return (int) (Math.round(value * 0xFF)) & 0xFF;
	}

	public static double mixAlphaChannels(final double upper, final double lower) {
		return upper + lower * (1 - upper);
	}

	public static double mixColorChannel(final double upper, final double lower, final double upperAlpha, final double lowerAlpha, final double resultAlpha) {
		// Prevent DivBy0: If the result alpha "is" zero the result color is also "nothing"
		return resultAlpha < 0.00001 ? 0.0 : (upper * upperAlpha + lower * lowerAlpha * (1 - upperAlpha)) / resultAlpha;
	}

	public static int betweenRGB(final int value) {
		return MinecraftUtil.between(value, 0, 255);
	}
}
