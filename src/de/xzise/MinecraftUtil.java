package de.xzise;

public class MinecraftUtil {

	private MinecraftUtil() {}

	public static int between(final int value, final int min, final int max) {
		if (value > max) {
			return max;
		} else if (value < min) {
			return min;
		} else {
			return value;
		}
	}
}
