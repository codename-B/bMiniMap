package com.bpermissions.minimap;

import java.util.HashMap;
import java.util.Map;

public class MiniMapCache {
	
	private static MiniMapCache instance = new MiniMapCache();
	
	public static MiniMapCache getInstance() {
		return instance;
	}
	
	Map<String, XZ> data = new HashMap<String, XZ>();
	
	public XZ get(int x, int z) {
		String xz = x+","+z;
		return data.get(xz);
	}
	
	public void put(int x, int z, int[] yid) {
		String xz = x+","+z;
		// Store in the cache
		data.put(xz, new XZ(x, z, yid));
	}
	
	public boolean contains(int x, int z) {
		String xz = x+","+z;
		return data.containsKey(xz);
	}
	
	static class XZ {
		
		private int x;
		private int z;
		
		private int y;
		private int id;
		
		public XZ(int x, int z, int[] yid) {
			this.x = x;
			this.z = z;
			
			this.y = yid[0];
			this.id = yid[1];
		}
		
		public int getX() {
			return x;
		}
		
		public int getZ() {
			return z;
		}
		
		public int getY() {
			return y;
			
		}
		
		public int getID() {
			return id;
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public String toString() {
			return x+","+z;
		}
	}

}
