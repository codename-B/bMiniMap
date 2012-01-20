package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapCache.XZ;
/**
 * Runs the render task for the minimap and converts
 * chunk data to a BufferedImage then converts the BufferedImage to
 * a bytebuffer!
 */
class MiniMapRender extends Thread {

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

	/**
	 * Custom get highest y method (since it's more reliable it seems)
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return y
	 */
	public int[] getHighestBlockYandIDCave(ActivePlayer player, World world, int x, int z) {
		int[] yid = {0, 0};
		// null check since apparently this can NPE
		if(world == null)
			return yid;
		if(player == null)
			return yid;
		int sy = (int) player.getLocation().getY();
		if(sy > world.getMaxHeight()-20)
			sy = world.getMaxHeight()-20;
		if(sy < 20)
			sy = 20;
		// Iterate down
		for(int i=0; i<20; i++) {
			int id = world.getBlockTypeIdAt(x, sy-i, z);
			int idD = world.getBlockTypeIdAt(x, sy-i+1, z);
			if(id != 0 && idD == 0) {
				yid[0] = sy-i;
				yid[1] = id;
				return yid;
			}			
		}
		// Iterate up
		for(int i=0; i<20; i++) {
			int id = world.getBlockTypeIdAt(x, sy+i, z);
			int idD = world.getBlockTypeIdAt(x, sy+i+1, z);
			if(id != 0 && idD == 0) {
				yid[0] = sy+i;
				yid[1] = id;
				return yid;
			}
		}
		// Return the default set
		return yid;
	}
	
	public int getDensity(World world, int x, int z) {
		int[] yid = getHighestBlockYandID(world, x, z);
		// Should help stop the lag
		if(yid[0] == 0 && yid[1] == 0)
			return 0;
		
		int y = yid[0];
		int air = 0;
		// Get the total # of ores in the column
		for(int i=0; i<y; i++) {
		int id = world.getBlockTypeIdAt(x, i, z);
		if(id == 0)
			air++;
		}
		// Then return this number
		return (air*100)/y;
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
				if(MiniMapWidget.mode == 2)
					this.densityMap(world, player, i, k);
				else if(MiniMapWidget.mode == 1)
					this.caveMap(world, player, i, k);
				else
					this.heightMap(world, player, i, k);
				// Cut the image into a circle
				for(int x=0; x<MiniMap.width; x++)
					for(int z=0; z<MiniMap.width; z++) {
						int center = MiniMap.radius;
						int xd = (x-center);
						int zd = (z-center);
						int distance = (xd*xd + zd*zd);
						// distance squared is fast and efficient enough for what we need
						if(distance >= (MiniMap.radius-2)*(MiniMap.radius-2))
							image.setRGB(x, z, transparent.getRGB());
					}
				// Clear the buffer just to REALLY clean up
				if(buffer != null)
					buffer.clear();
				// And set it to the new buffer
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
				// If the minimap render takes longer than 1000ms or the scale is changed exit the render pass
				if(System.currentTimeMillis()-start > 1000 || MiniMapWidget.scale != scale) {
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
				// get the highest block y and the id of the block
				int yid[] = getHighestBlockYandID(world, tx, tz);
				// parse from the int[]
				y = yid[0];
				id = yid[1];
				// Get the data if it's in the cache and hasn't been loaded
				if(y == 0 && id == 0 && cache.contains(tx, tz)) {
					XZ data = cache.get(tx, tz);
					y = data.getY();
					id = data.getID();
				} else if(y != 0 && id != 0) {
					cache.put(tx, tz, yid);
				}
				// Calculate the shading to apply
				dy = this.getShading(y, world)*4;
				// The color for the xz
				Color color = new Color(map.getRGB(id, x, z));
				// The rgb values
				int r, g, b;
				// rgb set
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				// do shading (and even out top+bottom a little)
				if(dy>32)
					dy = dy-dy/16;
				if(dy>48)
					dy = dy-dy/16;
				if(dy<-32)
					dy = dy+dy/16;
				if(dy<-48)
					dy = dy+dy/16;
				// apply shading to the rgb
				r = rel(r+dy);
				g = rel(g+dy);
				b = rel(b+dy);
				// then color in
				image.setRGB(x+width/2, z+width/2, new Color(r, g, b).getRGB());
			}
		// Clean up after yourself
		gr.dispose();
		// Apply the image to the minimap image
		gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		// Can we help stop the memory leak here?
		image.flush();
		image = null;
	}

	/* 
	 * HD minimap is the focus - we should work on transparent blocks
	 */
	public void caveMap(World world, ActivePlayer player, int i, int k) {
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
				// If the minimap render takes longer than 1000ms or the scale is changed exit the render pass
				if(System.currentTimeMillis()-start > 1000 || MiniMapWidget.scale != scale) {
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
				// get the highest block y and the id of the block
				int yid[] = getHighestBlockYandIDCave(player, world, tx, tz);
				// parse from the int[]
				y = yid[0];
				id = yid[1];
				// Calculate the shading to apply
				dy = (0-Math.abs(player.getLocation().getBlockY()-y))*8;

				if(y == 0)
					dy = -255;

				// The color for the xz
				Color color = new Color(map.getRGB(id, x, z));
				// The rgb values
				int r, g, b;
				// rgb set
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
				// do shading (and even out top+bottom a little)
				if(dy>32)
					dy = dy-dy/16;
				if(dy>48)
					dy = dy-dy/16;
				if(dy<-32)
					dy = dy+dy/16;
				if(dy<-48)
					dy = dy+dy/16;
				// apply shading to the rgb
				r = rel(r+dy);
				g = rel(g+dy);
				b = rel(b+dy);
				// then color in
				image.setRGB(x+width/2, z+width/2, new Color(r, g, b).getRGB());
			}
		// Clean up after yourself
		gr.dispose();
		// Apply the image to the minimap image
		gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		// Can we help stop the memory leak here?
		image.flush();
		image = null;
	}
	
	/* 
	 * HD minimap is the focus - we should work on transparent blocks
	 */
	public void densityMap(World world, ActivePlayer player, int i, int k) {
		long start = System.currentTimeMillis();
		int scale = MiniMapWidget.scale;
		/*
		 * Generate the image and apply shading 
		 */
		int tx, tz;
		int dy;

		int width = MiniMap.width;

		BufferedImage image = null;

		image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

		Graphics gr = image.getGraphics();

		gr.setColor(transparent);

		for (int x = -width/2; x < width/2; x++)
			for (int z = -width/2; z < width/2; z++) {
				// If the minimap render takes longer than 2000ms or the scale is changed exit the render pass
				if(System.currentTimeMillis()-start > 2000 || MiniMapWidget.scale != scale) {
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
				// get the highest block y and the id of the block
				dy = this.getDensity(world, tx, tz);

				int adjust = (dy*255)/100;
				// The color for the xz
				Color color = new Color(adjust, adjust, adjust);
				
				// then color in
				image.setRGB(x+width/2, z+width/2, color.getRGB());
			}
		// Clean up after yourself
		gr.dispose();
		// Apply the image to the minimap image
		gr = this.image.getGraphics();
		gr.drawImage(image, 0, 0, 256, 256, null);
		gr.dispose();
		// Can we help stop the memory leak here?
		image.flush();
		image = null;
	}

	/**
	 * Just a sanity check for RGB values
	 * @param input
	 * @return int (0-255)
	 */
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