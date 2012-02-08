package com.bpermissions.minimap.renderer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapCache;
import com.bpermissions.minimap.MiniMapCache.XZ;
import com.bpermissions.minimap.TextureMapper;

public class HeightRenderer extends ImageRenderer {
	
	public static final String NAME = "Height renderer";

	private final MiniMapCache cache;
	private final TextureMapper mapper;

	public HeightRenderer(final int width, final int height, final MiniMapCache cache, final TextureMapper mapper) {
		super(width, height);
		this.cache = cache;
		this.mapper = mapper;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected int getColor(final ActivePlayer player, final int x, final int z) {
		// get the highest block y and the id of the block
		int yid[] = getHighestBlockYandID(player.getWorld(), x, z);
		// parse from the int[]
		int y = yid[0];
		int id = yid[1];
		// Get the data if it's in the cache and hasn't been loaded
		if(y == 0 && id == 0 && this.cache.contains(x, z)) {
			XZ data = this.cache.get(x, z);
			y = data.getY();
			id = data.getID();
		} else if(y != 0 && id != 0) {
			this.cache.put(x, z, yid);
		}
		// Calculate the shading to apply
		final int dy = HeightRenderer.getShading(y, player.getWorld()) * 4;
		// The color for the xz
		return this.applyDy(this.mapper.getRGB(id, x, z), dy);
	}

	public static int getShading(int y, World world) {
		int height = world.getMaxHeight();
		return y-(height/2);
	}

	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public static int[] getHighestBlockYandID(World world, int x, int z) {
		int[] yid = {0, 0};
		try {
		// null check since apparently this can NPE
		if(world == null)
			return yid;
		// and then calculate it otherwise
		for (int i = world.getMaxHeight() - 1; i >= 0; i--) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id > 0) {
				yid[0] = i;
				yid[1] = id;
				return yid;
			}
		}
		return yid;
		} catch (Exception e) {
			return yid;
		}
	}
}
