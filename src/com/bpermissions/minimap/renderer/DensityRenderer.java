package com.bpermissions.minimap.renderer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapCache;

import de.xzise.ColorUtil;

public class DensityRenderer extends ImageRenderer {

	private final MiniMapCache cache;

	public DensityRenderer(final int width, final int height, final MiniMapCache cache) {
		super(width, height);
		this.cache = cache;
	}

	@Override
	public String getName() {
		return "Density renderer";
	}

	@Override
	protected int getColor(ActivePlayer player, int x, int z) {
		// get the density (percentage of air below the highest non air block)
		final int density = this.getDensity(player.getWorld(), x, z);
		final int channel = (density * 255) / 100;
		return ColorUtil.getRGB(channel, channel, channel);
	}

	public int getDensity(World world, int x, int z) {
		final int[] pillar = HeightRenderer.getBlockPillarCached(world, x, z, this.cache);
		int air = 0;
		int height = 0;
		for (int i = 0; i < pillar.length; i++) {
			if (pillar[i] == 0) {
				air++;
			} else {
				height = i;
			}
		}
		return height == 0 ? 0 : (air * 100) / height;
	}
}
