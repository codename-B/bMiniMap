package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.spoutcraft.spoutcraftapi.World;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;
/**
 * I guess this is big enough to deserve it's own class
 * @author codename_B
 *
 */
class MiniMapRender extends Thread {

	private final MiniMap parent;
	public final Map<Integer, Color> colors;
	public final Map<Integer, List<Color>> multiColors;
	
	public final Color transparent = new Color(255, 255, 255 ,0);

	private BufferedImage image;

	public ByteBuffer buffer;

	public int id = 0;

	private BufferedImage overlay;
	
	/**
	 * MiniMapRender runs the miniMap render async
	 * 
	 * @param parent
	 */
	MiniMapRender(MiniMap parent) {
		this.parent = parent;
		colors = new HashMap<Integer, Color>();
		multiColors = new HashMap<Integer, List<Color>>();
		setDefaultColors();
		image = parent.getImage();
		
		// Extra test stuff
		try {
		// Load the image from the jar? :O
		
		// Instead of using a hardcoded .jar file name, get whatever .jar contains this addon's code
		//File jarFile = new File(Spoutcraft.getAddonFolder(), "bMiniMap.jar");	
		File jarFile = new File(MiniMap.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
		JarFile jar = new JarFile(jarFile);
		ZipEntry ze = jar.getEntry("roundmap.png");
		InputStream is = jar.getInputStream(ze);
		BufferedImage bmg = ImageIO.read(is);
		// Don't forget cleanup!
		is.close();
		jar.close();
		
		overlay = bmg;
		} catch (Exception e) {
			e.printStackTrace();
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
			image = parent.getImage();

			try {
				image = parent.getImage();
				ActivePlayer player = parent.getParent().getClient()
						.getActivePlayer();

				World world = player.getWorld();

				int i = player.getLocation().getBlockX();
				int j = player.getLocation().getBlockY();
				int k = player.getLocation().getBlockZ();
				double zoom = MiniMap.zoom;
				if(getHighestStoneY(world, i, k) > j && j < 70) {
					this.caveMap(world, player, zoom, i, k);
				} else {
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
				// Apply the overlay
				image.getGraphics().drawImage(overlay, 0, 0, null);
				
				// Then finally send it to the buffer!
				buffer = TextureUtils.convertImageData(image);
				// This is debug code for my test environment but shouldn't affect most people, and too bad if it does ;)
				File test = new File("test.png");
				if (test.exists())
					ImageIO.write(image, "png", test);

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				// A decent wait
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	public int getDiff(World world, int x, int i, int z) {
		int shade = 0;
		
		for(int y=0; y<-10; y--)
		if((world.getBlockAt(x, i, z).getRelative(0,y,0).getTypeId() == 0))
		shade++;
		
		for(int y=0; y<10; y++)
		if((world.getBlockAt(x, i, z).getRelative(0,y,0).getTypeId() == 0))
		shade++;
		
		return shade*10;
	}
	
	public void caveMap(World world, ActivePlayer player, double zoom, int i, int k) { 
		
		for (int x = -MiniMap.radius; x < MiniMap.radius; x++)
			for (int z = -MiniMap.radius; z < MiniMap.radius; z++) {
				int shade = getDiff(world, (int) (i + x * zoom), player.getLocation().getBlockY(),
						(int) (k + z * zoom));
				
				Color color = new Color(0, shade, 0);
				image.setRGB(x + MiniMap.radius, z + MiniMap.radius,
						color.getRGB());
			}
	}
	
	public void heightMap(World world, ActivePlayer player, double zoom, int i, int k) {
		/*
		 * Generate the image and apply shading 
		 */
		for (int x = -MiniMap.radius; x < MiniMap.radius; x++) {
			for (int z = -MiniMap.radius; z < MiniMap.radius; z++) {
				int y = getHighestBlockY(world, (int) (i + x * zoom),
						(int) (k + z * zoom));

				int py = player.getLocation().getBlockY();

				int id = world.getBlockTypeIdAt((int) (x * zoom + i),
						(int) (y), (int) (z * zoom + k));

				int dy = ((y-py)*2 + (y-64)*2);

				Color color = colors.get(id);
				if (color == null)
					color = new Color(255, 255, 255);
				// Height shading?
				if (id != 0 && id != 8 && id != 9 && id != 10
						&& id != 11) {

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
				}

				image.setRGB(x + MiniMap.radius, z + MiniMap.radius,
						color.getRGB());
			}
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

	/**
	 * Yay HashMaps!
	 */
	private void setDefaultColors() {
		Color c;
		colors.put(0, new Color(255, 255, 255));
		colors.put(1, new Color(139, 137, 137));
		c = new Color(15, 188, 0);
		colors.put(2, c);
		colors.put(31, c);
		colors.put(37, c);
		colors.put(38, c);
		colors.put(39, c);
		colors.put(40, c);
		colors.put(59, c);
		colors.put(103, c);
		colors.put(104, c);
		colors.put(105, c);
		colors.put(106, c);
		c = new Color(139, 69, 19);
		colors.put(3, c);
		colors.put(60, c);
		colors.put(88, c);
		colors.put(4, new Color(205, 197, 191));
		c = new Color(148, 124, 80);
		colors.put(5, c);
		colors.put(32, c);
		colors.put(53, c);
		colors.put(54, c);
		colors.put(58, c);
		colors.put(85, c);
		colors.put(86, c);
		colors.put(90, c);
		colors.put(96, c);
		colors.put(99, c);
		colors.put(100, c);
		colors.put(107, c);
		colors.put(6, new Color(139, 69, 19));
		colors.put(7, new Color(52, 52, 52));
		c = new Color(20, 20, 200);
		colors.put(8, c);
		colors.put(9, c);
		c = new Color(252, 87, 0);
		colors.put(10, c);
		colors.put(11, c);
		colors.put(51, c);
		colors.put(12, new Color(134, 114, 94));
		c = new Color(144, 144, 144);
		colors.put(13, c);
		colors.put(14, c);
		colors.put(15, c);
		colors.put(16, c);
		colors.put(21, c);
		colors.put(56, c);
		colors.put(61, c);
		colors.put(62, c);
		colors.put(67, c);
		colors.put(73, c);
		colors.put(74, c);
		c = new Color(160, 82, 45);
		colors.put(17, c);
		colors.put(81, c);
		colors.put(83, c);
		colors.put(18, new Color(35, 100, 40));
		c = new Color(255, 255, 255);
		colors.put(19, c);
		colors.put(20, c);
		colors.put(22, new Color(26, 70, 161));
		colors.put(24, new Color(214, 207, 154));
		colors.put(31, new Color(20, 140, 0));
		colors.put(41, new Color(255, 251, 86));
		colors.put(42, new Color(240, 240, 240));
		c = new Color(164, 164, 164);
		colors.put(43, c);
		colors.put(44, c);
		c = new Color(157, 77, 55);
		colors.put(45, c);
		colors.put(46, c);
		colors.put(47, c);
		colors.put(48, new Color(33, 76, 33));
		colors.put(49, new Color(15, 15, 24));
		c = new Color(255, 255, 255);
		colors.put(50, c);
		colors.put(51, c);
		colors.put(52, c);
		colors.put(55, new Color(252, 87, 0));
		colors.put(57, new Color(156, 234, 231));
		colors.put(79, new Color(90, 134, 191));
		colors.put(83, new Color(20, 140, 0));
		colors.put(86, new Color(255, 140, 0));
		colors.put(87, new Color(128, 8, 8));
		colors.put(89, new Color(150, 110, 48));
		colors.put(91, new Color(255, 140, 0));
		List<Color> variants;
		variants = new ArrayList<Color>();
		variants.add(new Color(34, 100, 34));
		variants.add(new Color(40, 72, 0));
		variants.add(new Color(20, 105, 36));
		multiColors.put(18, variants);
		variants = new ArrayList<Color>();
		variants.add(new Color(241, 241, 241));
		variants.add(new Color(235, 129, 56));
		variants.add(new Color(185, 57, 197));
		variants.add(new Color(126, 156, 219));
		variants.add(new Color(212, 187, 32));
		variants.add(new Color(62, 198, 49));
		variants.add(new Color(221, 141, 163));
		variants.add(new Color(63, 63, 63));
		variants.add(new Color(173, 180, 180));
		variants.add(new Color(31, 96, 123));
		variants.add(new Color(135, 56, 205));
		variants.add(new Color(35, 46, 141));
		variants.add(new Color(82, 49, 27));
		variants.add(new Color(54, 74, 24));
		variants.add(new Color(167, 45, 41));
		variants.add(new Color(10, 10, 10));
		multiColors.put(35, variants);
	}

}