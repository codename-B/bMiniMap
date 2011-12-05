package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Map;
import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.World.Environment;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;
/**
 * I guess this is big enough to deserve it's own class
 * @author codename_B
 *
 */
class MiniMapRender extends Thread {
	
	public static MiniMapRender single;

	private final MiniMap parent;

	// Yes, it's the coordinates image!
	
	public final Color transparent = new Color(255, 255, 255 ,0);
	
	//public LinkedList<MiniMapLocation> locList = new LinkedList<MiniMapLocation>();
	
	private BufferedImage image;
	
	public ByteBuffer buffer;
	
	TextureMapper map = new TextureMapper();

	/**
	 * MiniMapRender runs the miniMap render async
	 * 
	 * @param parent
	 */
	MiniMapRender(MiniMap parent) {
		this.parent = parent;
		image = parent.getImage();
	}
	
	
	public int getRelativeShading(World world, int x, int y, int z) {
		int amount = 0;
		if(world.getBlockTypeIdAt(x+1, y, z) == 0)
			amount ++;
		else
			amount--;
		if(world.getBlockTypeIdAt(x-1, y, z) == 0)
			amount ++;
		else
			amount--;
		if(world.getBlockTypeIdAt(x, y, z+1) == 0)
			amount ++;
		else
			amount--;
		if(world.getBlockTypeIdAt(x, y, z-1) == 0)
			amount ++;
		else
			amount--;
		return amount;
	}
	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public int getHighestBlockY(World world, int x, int z) {
		for (int i = 127; i >= 0; i--) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id > 0)
				return i;
		}
		return 0;
	}
	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * Also this one only gets stone
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public int getHighestStoneY(World world, int x, int z) {
		for (int i = 127; i >= 0; i--) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id == 1)
				return i;
		}
		return 0;
	}

	@Override
	/**
	 * Asyncynously updates the minimap
	 */
	public void run() {
		while (parent.getParent().isEnabled) {
			long start = System.currentTimeMillis();
			int scale = MiniMapWidget.scale;
			try {
				
				image = parent.getImage();
				ActivePlayer player = parent.getParent().getClient()
						.getActivePlayer();
				
				World world = player.getWorld();
				
				int i = player.getLocation().getBlockX();
				int j = player.getLocation().getBlockY();
				int k = player.getLocation().getBlockZ();
				
				//MiniMapLocation loc = new MiniMapLocation(world, i, k);
				//locList.add(loc);
				//if(locList.size() > 128)
				//	locList.remove(0);
				
				int zoom = 32+MiniMapWidget.scale;
				if(getHighestStoneY(world, i, k) > j && j < 90) {
					this.caveMap(world, player, zoom, i, k);
				} else if(world.getEnvironment() == Environment.NETHER) {
					this.caveMap(world, player, zoom, i, k);
				}	
				else {
					this.heightMap(world, player, zoom, i, k);
				}
				
				/*
				 * Cut image into a circle
				 */
				for(int x=0; x<MiniMap.width; x++)
					for(int z=0; z<MiniMap.width; z++) {
						int center = MiniMap.radius;
						
						int xd = (x-center);
						int zd = (z-center);
						int distance = (xd*xd + zd*zd);
						
						if(distance >= (MiniMap.radius-2)*(MiniMap.radius-2))
						image.setRGB(x, z, transparent.getRGB());
					}
				// Then finally send it to the buffer!
				buffer = TextureUtils.convertImageData(image, 256);

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				long finish = 500-(System.currentTimeMillis()-start);
				if(finish > 0 && MiniMapWidget.scale == scale)
				sleep(finish);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public int getDiff(World world, int x, int i, int z) {
		int shade = 0;
		
		for(int y=0; y<-20; y--)
		if((world.getBlockAt(x, i, z).getRelative(0,y,0).getTypeId() == 0))
		shade++;
		
		for(int y=0; y<20; y++)
		if((world.getBlockAt(x, i, z).getRelative(0,y,0).getTypeId() == 0))
		shade++;
		
		return shade*10;
	}
	
	public MiniMapLocation inList(int x, int z) {
		//for(MiniMapLocation loc : locList)
		//	if(loc.nearby(x, z, 0))
		//		return loc;
		return null;
	}
	
	public void caveMap(World world, ActivePlayer player, int radius, int i, int k) { 
		
		int tx, tz, ty;
		//Map<Integer, Integer[]> pairs = new HashMap<Integer, Integer[]>();
		BufferedImage image = new BufferedImage(radius*2, radius*2, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = -radius; x < radius; x++)
			for (int z = -radius; z < radius; z++) {
				
				tx = (int) (i + x);
				tz = (int) (k + z);
				ty = player.getLocation().getBlockY();
				
				//MiniMapLocation test = inList(tx, tz);
				//if(test != null) {
				//int index = test.hashCode();
				//Integer[] pair = {(x+MiniMap.radius), (z+MiniMap.radius)};
				//pairs.put(index, pair);
				//}
				
				int shade = getDiff(world, tx, ty,
						tz);
				
				Color color = new Color(shade, shade, shade);
				image.setRGB(x + radius, z + radius,
						color.getRGB());
			}
		//writePath(pairs);
		Graphics gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		image = null;
	}
	
	public void heightMap(World world, ActivePlayer player, int radius, int i, int k) {
		long start = System.currentTimeMillis();
		int sStart = MiniMapWidget.scale;
		
		/*
		 * Generate the image and apply shading 
		 */
		int tx, tz;
		int y, id, dy, diff;
		//Map<Integer, Integer[]> pairs = new HashMap<Integer, Integer[]>();
		int py = player.getLocation().getBlockY();
		
		BufferedImage image = null;
		if(MiniMapWidget.scale == 0)
		image = new BufferedImage(radius*2*16, radius*2*16, BufferedImage.TYPE_INT_RGB);
		else
		image = new BufferedImage(radius*2, radius*2, BufferedImage.TYPE_INT_RGB);
		Graphics gr = image.getGraphics();
		for (int x = -radius; x < radius; x++)
			for (int z = -radius; z < radius; z++) {
				
				if(System.currentTimeMillis()-start > 500+MiniMapWidget.scale*4 || MiniMapWidget.scale != sStart) {
					gr.dispose();
					// TODO Add pathing back
					//writePath(pairs);
					gr = this.image.getGraphics();
					gr.drawImage(image, 0, 0, 256, 256, null);
					gr.dispose();
					// Can we help stop the memory leak here?
					image.flush();
					image = null;
					return;
				}
				
				tx = (int) (i + x);
				tz = (int) (k + z);
				
				//MiniMapLocation test = inList(tx, tz);
				//if(test != null) {
				//int index = test.hashCode();
				//Integer[] pair = {(x+MiniMap.radius), (z+MiniMap.radius)};
				//pairs.put(index, pair);
				//}
				
				y = getHighestBlockY(world, tx, tz);
				id = world.getBlockTypeIdAt(tx, y, tz);
				dy = ((y-py) + (y-64));
				diff = getRelativeShading(world, tx, y, tz)*15;
				
				dy = dy+diff+world.getBlockAt(tx, y+1, tz).getLightLevel()*5;
				
				if(MiniMapWidget.scale == 0) {
				
				BufferedImage tile = map.getTexture(id);
				
				for(int m=0; m<16; m++)
					for(int n=0; n<16; n++) {
						Color color = new Color(tile.getRGB(m, n));
						int r = color.getRed() + dy;
						if (r > 255)
						r = 255;
						if (r < 0)
						r = 0;
						int g = (color.getGreen() + dy);
						if (g > 255)
						g = 255;
						if (g < 0)
						g = 0;
						int b = color.getBlue() + dy;
						if (b > 255)
						b = 255;
						if (b < 0)
						b = 0;
						color = new Color(r, g, b);
						tile.setRGB(m, n, color.getRGB());
						color = null;
				}
				
				gr.drawImage(tile, (x+radius)*16, (z+radius)*16, null);
				} else {
					Color color = map.getColor(id);
					int r = color.getRed() + dy;
					if (r > 255)
					r = 255;
					if (r < 0)
					r = 0;
					int g = (color.getGreen() + dy);
					if (g > 255)
					g = 255;
					if (g < 0)
					g = 0;
					int b = color.getBlue() + dy;
					if (b > 255)
					b = 255;
					if (b < 0)
					b = 0;
					color = new Color(r, g, b);
					image.setRGB(x+image.getWidth()/2, z+image.getHeight()/2, color.getRGB());
					color = null;
				}
			}
		gr.dispose();
		// TODO Add pathing back
		//writePath(pairs);
		gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		// Can we help stop the memory leak here?
		image.flush();
		image = null;
	}
	
	public void writePath(Map<Integer, Integer[]> pairs) {
		/*
		 * Now we have navigation history, handy if you
		 * want to find your way back to whence you came from!
		 */
		if(pairs.size() > 1) {
		Graphics gr = image.getGraphics();
		gr.setColor(Color.RED);
		
		//for(int p=locList.getFirst().hashCode()+1; p<locList.getLast().hashCode()-1; p++) {
		//	if(pairs.containsKey(p) && pairs.containsKey(p+1)) {
			//Integer[] a = pairs.get(p);
			//Integer[] b = pairs.get(p+1);
			try {
			//int x1 = a[0];
			//int y1 = a[1];
			//int x2 = b[0];
			//int y2 = b[1];
			
			//gr.drawLine(x1, y1, x2, y2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
		
		//}
		//pairs.clear();
		//gr.dispose();
		}
	}

	/**
	 * Another nice convenience method, I love navigation!
	 * 
	 * @return MiniMap
	 */
	public MiniMap getParent() {
		return parent;
	}
	
	/*
	 * A couple of static utility classes
	 */

	static class MiniMapLocation {
		
		private final int x;
		private final int z;
		private final World world;
		private final int time;
		
		private static int index = 0;
		
		public MiniMapLocation(World world, int x, int z) {
			this.x = x;
			this.z = z;
			this.world = world;
			index++;
			time = index;
		}
		
		public int getX() {
			return x;
		}
		
		public int getZ() {
			return z;
		}
		
		public World getWorld() {
			return world;
		}
		
		@Override
		public int hashCode() {
			return (int) time;
		}

		public boolean nearby(int x, int z, int range) {
			if(x >= getX()-range && x <= getX()+range)
				if(z >= getZ()-range && z <= getZ()+range)
				return true;
		return false;
		}
	}

}