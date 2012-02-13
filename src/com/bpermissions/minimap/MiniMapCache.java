package com.bpermissions.minimap;

import org.getspout.commons.util.map.TIntPairObjectHashMap;
import org.spoutcraft.spoutcraftapi.World;

/**
 * This is a convenience class for storing XYZ and ID for XZ around the minimap.
 */
public class MiniMapCache {

	private static MiniMapCache instance = new MiniMapCache();

	public static MiniMapCache getInstance() {
		return instance;
	}

	public static final class IntArray {
		public final int[] array;
		public IntArray(final int[] array) {
			this.array = array;
		}
	}

	private final TIntPairObjectHashMap<IntArray> data = new TIntPairObjectHashMap<MiniMapCache.IntArray>();
	private World world;

	public World getWorld() {
		return this.world;
	}

	public void setWorld(final World world) {
		if ((this.world != null && !this.world.equals(world)) || (this.world == null && world != null)) {
			this.world = world;
			this.clear();
		}
	}

	public void clear() {
		this.data.clear();
	}

	public int[] get(int x, int z) {
		return this.data.get(x, z).array;
	}

	public void put(int x, int z, int[] array) {
		this.data.put(x, z, new IntArray(array));
	}

	public boolean contains(int x, int z) {
		return data.containsKey(x, z);
	}
}
