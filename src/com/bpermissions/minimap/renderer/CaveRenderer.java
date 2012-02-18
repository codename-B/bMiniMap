package com.bpermissions.minimap.renderer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapRender;
import com.bpermissions.minimap.TextureMapper;

public class CaveRenderer extends ImageRenderer {

	private final TextureMapper mapper;

	public CaveRenderer(final int width, final int height, final TextureMapper mapper, final MiniMapRender render) {
		super(width, height, render);
		this.mapper = mapper;
	}

	@Override
	protected int getColor(final ActivePlayer player, final int x, final int z) {
		// get the highest block y and the id of the block
		int yid[] = getHighestBlockYandIDCave(player, player.getWorld(), x, z);
		// parse from the int[]
		int y = yid[0];
		int id = yid[1];

		// Calculate the shading to apply
		final int dy;
		if (y == 0) {
			dy = -255;
		} else {
			dy = (0 - Math.abs(player.getLocation().getBlockY() - y)) * 8;
		}

		// The color for the xz
		return this.applyDy(this.mapper.getRGB(id, x, z), dy);
	}

	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public int[] getHighestBlockYandIDCave(ActivePlayer player, World world, int x, int z) {
		int[] yid = { 0, 0 };
		// null check since apparently this can NPE
		if (world == null)
			return yid;
		if (player == null)
			return yid;
		int sy = (int) player.getLocation().getY();
		if (sy > world.getMaxHeight() - 20)
			sy = world.getMaxHeight() - 20;
		if (sy < 20)
			sy = 20;
		// Iterate down
		for (int i = 0; i < 20; i++) {
			int id = world.getBlockTypeIdAt(x, sy - i, z);
			int idD = world.getBlockTypeIdAt(x, sy - i + 1, z);
			if (id != 0 && idD == 0) {
				yid[0] = sy - i;
				yid[1] = id;
				return yid;
			}
		}
		// Iterate up
		for (int i = 0; i < 20; i++) {
			int id = world.getBlockTypeIdAt(x, sy + i, z);
			int idD = world.getBlockTypeIdAt(x, sy + i + 1, z);
			if (id != 0 && idD == 0) {
				yid[0] = sy + i;
				yid[1] = id;
				return yid;
			}
		}
		// Return the default set
		return yid;
	}

	@Override
	public String getName() {
		return "Cave renderer";
	}
}
