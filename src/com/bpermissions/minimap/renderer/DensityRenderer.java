package com.bpermissions.minimap.renderer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import de.xzise.ColorUtil;

public class DensityRenderer extends ImageRenderer {

	public DensityRenderer(final int width, final int height) {
		super(width, height);
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
		int[] yid = HeightRenderer.getHighestBlockYandID(world, x, z);
		// Should help stop the lag
		if (yid[0] == 0 && yid[1] == 0)
			return 0;

		int y = yid[0];
		int air = 0;
		// Get the total # of ores in the column
		for (int i = 0; i < y; i++) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id == 0)
				air++;
		}
		// Then return this number
		return (air * 100) / y;
	}
}
