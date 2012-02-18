package de.xzise;

import java.util.Random;

import org.spoutcraft.spoutcraftapi.util.FixedLocation;

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

	public static boolean canSlimeSpawn(final FixedLocation loc) {
		return MinecraftUtil.canSlimeSpawn(loc.getBlock().getChunk().getX(), loc.getBlock().getChunk().getZ(), loc.getWorld().getSeed());
	}

	public static boolean canSlimeSpawn(final int chunkX, final int chunkZ, final long seed) {
		Random rnd = new Random(seed + chunkX * chunkX * 4987142 + chunkX * 5947611 + chunkZ * chunkZ * 4392871L + chunkZ * 389711 ^ 987234911L);
		return rnd.nextInt(10) == 0;
	}

	public static int getChunkCoordinate(final int blockCoordinate) {
		return blockCoordinate >> 4;
	}
}
