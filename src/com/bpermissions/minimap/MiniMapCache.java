package com.bpermissions.minimap;

import org.getspout.commons.util.map.TIntPairLongHashMap;

/**
 * This is a convenience class for storing XYZ and ID for XZ around the minimap.
 */
public class MiniMapCache {

	private static MiniMapCache instance = new MiniMapCache();

	public static MiniMapCache getInstance() {
		return instance;
	}

	TIntPairLongHashMap data = new TIntPairLongHashMap();
	
	public void clear() {
		data.clear();
	}

	public int getY(int x, int z) {
		return (int) (data.get(x, z) & 0XFFFFFFFF);
	}
	
	public int getId(int x, int z) {
		return (int) ((data.get(x, z) >> 32) & 0XFFFFFFFF);
	}

	public void put(int x, int z, int y, int id) {
		data.put(x, z, (id << 32 | y));
	}

	public boolean contains(int x, int z) {
		return data.containsKey(x, z);
	}
}
