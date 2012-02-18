package com.bpermissions.minimap.renderer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapCache;
import com.bpermissions.minimap.MiniMapRender;
import com.bpermissions.minimap.TextureMapper;

import de.xzise.ColorUtil;

public class HeightRenderer extends ImageRenderer {
	
	public static final String NAME = "Height renderer";

	private final MiniMapCache cache;
	private final TextureMapper mapper;

	public HeightRenderer(final int width, final int height, final MiniMapCache cache, final TextureMapper mapper, final MiniMapRender render) {
		super(width, height, render);
		this.cache = cache;
		this.mapper = mapper;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public static int[] getBlockPillar(final World world, final int x, final int z) {
		final int[] pillar = new int[world.getMaxHeight()];
		for (int i = 0; i < world.getMaxHeight(); i++) {
			pillar[i] = world.getBlockTypeIdAt(x, i, z);
		}
		return pillar;
	}

	public static int getChunkCoordinate(final int blockCoordinate) {
		return blockCoordinate >> 4;
	}

	public static int[] getBlockPillarCached(final World world, final int x, final int z, final MiniMapCache cache) {
		cache.setWorld(world);
		final int chunkX = getChunkCoordinate(x);
		final int chunkZ = getChunkCoordinate(z);
		final int[] pillar;
		final boolean cached;
		if (!world.isChunkLoaded(chunkX, chunkZ)) {
			if (cache.contains(x, z)) {
				pillar = cache.get(x, z);
				cached = true;
			} else {
				world.loadChunk(chunkX, chunkZ);
				pillar = getBlockPillar(world, x, z);
				cached = false;
			}
		} else {
			pillar = getBlockPillar(world, x, z);
			cached = false;
		}
		if (!cached) {
			cache.put(x, z, pillar);
		}
		return pillar;
	}

	@Override
	protected int getColor(final ActivePlayer player, final int x, final int z) {
		final int[] pillar = getBlockPillarCached(player.getWorld(), x, z, this.cache);
		int y = pillar.length - 1;
		while (y >= 0 && pillar[y] == 0) {
			y--;
		}

		// Nothing there
		if (y < 0) {
			return 0;
		}
		// Calculate the shading to apply
		final int dy = HeightRenderer.getShading(y, player.getWorld()) * 4;
		// fully transparent
		double alpha = 0.0;
		double red = 0.0;
		double green = 0.0;
		double blue = 0;
		// The color for the xz
		while (alpha < 0.9 && y >= 0) {
			final int rgb = this.mapper.getRGB(pillar[y], x, z);
			final double upperAlpha = alpha;
			final double lowerAlpha = ColorUtil.getRGBPercentage(ColorUtil.getAlphaFromRGB(rgb));
			final double lowerRed = ColorUtil.getRGBPercentage(ColorUtil.getRedFromRGB(rgb));
			final double lowerGreen = ColorUtil.getRGBPercentage(ColorUtil.getGreenFromRGB(rgb));
			final double lowerBlue = ColorUtil.getRGBPercentage(ColorUtil.getBlueFromRGB(rgb));
			alpha = ColorUtil.mixAlphaChannels(upperAlpha, lowerAlpha);

			red = ColorUtil.mixColorChannel(red, lowerRed, upperAlpha, lowerAlpha, alpha);
			green = ColorUtil.mixColorChannel(green, lowerGreen, upperAlpha, lowerAlpha, alpha);
			blue = ColorUtil.mixColorChannel(blue, lowerBlue, upperAlpha, lowerAlpha, alpha);
			y--;
		}
		return this.applyDy(ColorUtil.getFromRGBPercentage(red), ColorUtil.getFromRGBPercentage(green), ColorUtil.getFromRGBPercentage(blue), dy);
	}

	public static int getShading(int y, World world) {
		int height = world.getMaxHeight();
		return y-(height/2);
	}
}
