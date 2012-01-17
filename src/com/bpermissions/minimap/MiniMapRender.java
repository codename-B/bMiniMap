package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapCache.XZ;
/**
 * I guess this is big enough to deserve it's own class
 * @author codename_B
 *
 */
class MiniMapRender extends Thread {

	public static MiniMapRender single;

	private final MiniMap parent;

	public static final Color transparent = new Color(255, 255, 255 ,0);

	private BufferedImage image;

	public ByteBuffer buffer;
	
	public MiniMapCache cache = MiniMapCache.getInstance();

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

	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public int[] getHighestBlockYandID(World world, int x, int z) {
		int[] yid = {0, 0};
		for (int i = world.getMaxHeight() - 1; i >= 0; i--) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id > 0) {
				yid[0] = i;
				yid[1] = id;
				return yid;
			}
		}
		return yid;
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
		for (int i = world.getMaxHeight() - 1; i >= 0; i--) {
			int id = world.getBlockTypeIdAt(x, i, z);
			if (id == 1)
				return i;
		}
		return 0;
	}

	@Override
	/**
	 * Asynchronously updates the minimap
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
				//int j = player.getLocation().getBlockY();
				int k = player.getLocation().getBlockZ();

				this.heightMap(world, player, i, k);

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

	public int getShading(int y, World world) {
		int height = world.getMaxHeight();
		return y-(height/2);
	}

	/* 
	 * HD minimap is the focus - we should work on transparent blocks
	 */
	public void heightMap(World world, ActivePlayer player, int i, int k) {
		long start = System.currentTimeMillis();
		int scale = MiniMapWidget.scale;

		/*
		 * Generate the image and apply shading 
		 */
		int tx, tz;
		int y, id, dy;
		
		int width = MiniMap.width;

		BufferedImage image = null;

		image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

		Graphics gr = image.getGraphics();

		gr.setColor(transparent);

		for (int x = -width/2; x < width/2; x++)
			for (int z = -width/2; z < width/2; z++) {

				if(System.currentTimeMillis()-start > 1000+MiniMapWidget.scale*4 || MiniMapWidget.scale != scale) {
					gr.dispose();

					gr = this.image.getGraphics();
					gr.drawImage(image, 0, 0, 256, 256, null);
					gr.dispose();
					// Can we help stop the memory leak here?
					image.flush();
					image = null;
					return;
				}
				// Use the scale to scale RELATIVELY
				tx = (int) (i + (x/(3-scale)));
				tz = (int) (k + (z/(3-scale)));
				
				int yid[] = getHighestBlockYandID(world, tx, tz);
				
				y = yid[0];
				id = yid[1];
				
				// Get the data if it's in the cache
				if(y == 0 && id == 0 && cache.contains(tx, tz)) {
					XZ data = cache.get(tx, tz);
					y = data.getY();
					id = data.getID();
				} else if(y != 0 && id != 0) {
					cache.put(tx, tz, yid);
				}
				
				dy = this.getShading(y, world)*4;
				
				int sx = x;
				int sz = z;
				
				Color color = new Color(map.getRGB(id, sx, sz));
				
				int r, g, b;
				// rgb set
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				// do shading
				if(dy>32)
					dy = dy-dy/16;
				if(dy<-32)
					dy = dy+dy/16;
				r = rel(r+dy);
				g = rel(g+dy);
				b = rel(b+dy);
				// then color in
				image.setRGB(x+width/2, z+width/2, new Color(r, g, b).getRGB());
			}

		gr.dispose();

		try {
			File file = new File("test.png");
			ImageIO.write(image, "png", file);
		} catch (Exception e) {

		}

		gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		// Can we help stop the memory leak here?
		image.flush();
		image = null;
	}
	
	public int rel(int input) {
		if(input > 255)
			return 255;
		else if(input < 0)
			return 0;
		return input;
	}

	/**
	 * Another nice convenience method, I love navigation!
	 * 
	 * @return MiniMap
	 */
	public MiniMap getParent() {
		return parent;
	}

}