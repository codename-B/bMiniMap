package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.spoutcraft.spoutcraftapi.Spoutcraft;

public class TextureMapper {

	private BufferedImage terrain;
	private final Map<Integer, Integer[]> pairs = new HashMap<Integer, Integer[]>();
	private final Integer[] blank = {0, 0};
	
	
	public static void main(String[] args) {
		TextureMapper map = new TextureMapper();
		try {
		for(int i=0; i<10; i++) {
			File file = new File(i+".png");
			if(!file.exists())
				file.createNewFile();
				ImageIO.write(map.getTexture(i), "png", file);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TextureMapper() {
		terrain = loadTerrain();
		setupPairs();
	}
	
	public TextureMapper(boolean debug) {
		terrain = loadTerrainDebug();
		setupPairs();
	}
	
	public void setupPairs() {
		// TODO flesh out fully
		Integer[] st;
		
		// Stone texture
		st = newArray(1, 0);
		pairs.put(1, st);
		pairs.put(23, st);
		pairs.put(36, st);
		pairs.put(61, st);
		pairs.put(62, st);
		pairs.put(69, st);
		pairs.put(77, st);
		
		pairs.put(2, newArray(8, 2));
		pairs.put(3, newArray(2, 0));
		pairs.put(4, newArray(0, 1));
		
		// Wood texture
		st = newArray(4, 0);
		pairs.put(5, st);
		pairs.put(53, st);
		pairs.put(63, st);
		pairs.put(64, st);
		pairs.put(65, st);
		pairs.put(68, st);
		pairs.put(72, st);
		pairs.put(85, st);
		
		pairs.put(6, newArray(15, 0));
		pairs.put(7, newArray(1, 1));
		pairs.put(8, newArray(15, 12));
		pairs.put(9, newArray(15, 13));
		pairs.put(10, newArray(15, 14));
		pairs.put(11, newArray(15, 15));
		pairs.put(12, newArray(2, 1));
		pairs.put(13, newArray(3, 1));
		pairs.put(14, newArray(2, 0));
		pairs.put(15, newArray(2, 1));
		pairs.put(16, newArray(2, 2));
		pairs.put(17, newArray(5, 1));
		pairs.put(18, newArray(4, 3));
		pairs.put(19, newArray(0, 3));
		pairs.put(20, newArray(1, 3));
		pairs.put(21, newArray(0, 10));
		pairs.put(22, newArray(0, 9));
		pairs.put(24, newArray(0, 11));
		pairs.put(25, newArray(10, 4));
		pairs.put(26, newArray(6, 8));
		// Rails texture
		st = newArray(0, 8);
		pairs.put(27, st);
		pairs.put(28, st);
		pairs.put(66, st);
		
		pairs.put(29, newArray(10, 6));
		pairs.put(30, newArray(11, 0));
		pairs.put(31, newArray(8, 2));
		pairs.put(32, newArray(7, 3));
		pairs.put(33, newArray(11, 6));
		pairs.put(34, newArray(11, 6));
		// TODO WOOL COLORS?
		pairs.put(35, newArray(0, 4));
		
		pairs.put(37, newArray(0, 13));
		pairs.put(38, newArray(0, 12));
		pairs.put(39, newArray(1, 13));
		pairs.put(40, newArray(1, 12));
		pairs.put(41, newArray(7, 1));
		pairs.put(42, newArray(6, 1));
		pairs.put(43, newArray(6, 0));
		pairs.put(44, newArray(6, 0));
		pairs.put(45, newArray(7, 0));
		pairs.put(46, newArray(9, 0));
		pairs.put(47, newArray(3, 2));
		pairs.put(48, newArray(4, 2));
		pairs.put(49, newArray(5, 2));
		pairs.put(50, newArray(0, 5));
		pairs.put(51, newArray(0, 5));
		pairs.put(52, newArray(1, 4));
		
		pairs.put(54, newArray(11, 1));
		pairs.put(54, newArray(11, 1));
		// TODO REDSTONE
		//pairs.put(55, newArray(11, 1));
		pairs.put(56, newArray(2, 3));
		pairs.put(57, newArray(8, 1));
		pairs.put(58, newArray(11, 2));
		pairs.put(59, newArray(15, 5));
		pairs.put(60, newArray(7, 5));
		
		pairs.put(67, newArray(0, 1));
		
		pairs.put(70, newArray(0, 6));
		
		pairs.put(73, newArray(3, 3));
		pairs.put(74, newArray(3, 3));
		pairs.put(75, newArray(3, 3));
		pairs.put(76, newArray(3, 3));
		
		pairs.put(78, newArray(2, 4));
		pairs.put(79, newArray(3, 4));
		pairs.put(80, newArray(2, 4));
		pairs.put(81, newArray(5, 4));
		pairs.put(82, newArray(0, 0));
		pairs.put(83, newArray(9, 4));
		pairs.put(84, newArray(11, 4));

		pairs.put(86, newArray(6, 6));
		pairs.put(87, newArray(7, 6));
		pairs.put(88, newArray(8, 6));
		pairs.put(89, newArray(9, 6));
		pairs.put(90, newArray(14, 4));
		pairs.put(91, newArray(6, 6));
	}
	
	public Integer[] newArray(int x, int y) {
		Integer[] ar = {x, y};
		return ar;
	}
	
	public Integer[] getPair(int id) {
		if(pairs.containsKey(id))
			return pairs.get(id);
		else
			return blank.clone();
	}
	
	Map<Integer, Color> colors = new HashMap<Integer, Color>();
	
	public Color getColor(int id) {
		if(colors.containsKey(id))
			return colors.get(id);
		
		BufferedImage texture = getTexture(id);
		
		int width = texture.getWidth();
		int height = texture.getHeight();
		
		int r = 0;
		int g = 0;
		int b = 0;
		
		for(int x=0; x<width; x++)
			for(int y=0; y<height; y++) {
				Color color = new Color(texture.getRGB(x, y));
				r += color.getRed();
				g += color.getGreen();
				b += color.getBlue();	
			}
		
		r = r/(width*height);
		g = g/(width*height);
		b = b/(width*height);
		
		Color color = new Color(r, g, b);
		
		colors.put(id, color);
		return color;
	}
	
	public int getRGB(int id, int x, int z) {
		x = Math.abs(x) % 16;
		z = Math.abs(z) % 16;
		
		return getTexture(id).getRGB(x, z);
	}
	// Even better than a map, we don't need anything else after all, arrays ftw
	BufferedImage[] images = new BufferedImage[128];
	
	public BufferedImage getTexture(int id) {
		
		if(images[id] != null)
			return images[id];
		
		Integer[] pair = getPair(id);
		int x0 = pair[0]*16;
		int y0 = pair[1]*16;
		int x1 = x0+16;
		int y1 = y0+16;
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		
		if(id == 0 || id == 20)
			return img;
		
		Graphics gr = img.getGraphics();
		gr.setColor(MiniMapRender.transparent);
		gr.drawImage(terrain, 0, 0, 16, 16, x0, y0, x1, y1, null);
		gr.dispose();
		if(id == 2 || id == 18 || id ==31) {
		for(int x=0; x<16; x++)
			for(int y=0; y<16; y++) {
			Color color = new Color(img.getRGB(x, y));
			color = new Color(0, color.getGreen(), 0, color.getAlpha());
			img.setRGB(x, y, color.getRGB());
			color = null;
			}
		}
		
		images[id] = img;
		return img;
	}
	
	public BufferedImage loadTerrainDebug() {
		try {
			File file = new File("terrain.png");
			return ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage loadTerrain() {
		try {
			// Load the terrain image from the current texture pack
			
			File zipLocation = Spoutcraft.getSelectedTexturePackZip();
			ZipFile textureZip;
			
			//If the file isn't there (as in when we are on default, load from the jar)
			if(zipLocation.getName().equalsIgnoreCase("Default")) {
				File jarFile = new File(MiniMap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				textureZip = new ZipFile(jarFile);
			} else {
				 textureZip = new ZipFile(zipLocation);
			}
			
			ZipEntry zipEntry = textureZip.getEntry("terrain.png");
			InputStream is = textureZip.getInputStream(zipEntry);
			BufferedImage bmg = ImageIO.read(is);
			
			// Don't forget cleanup!
			is.close();
			textureZip.close();
			
			// Scale to 128px
			if(bmg.getWidth() > 256) {
				BufferedImage newImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
				newImg.getGraphics().drawImage(bmg, 0, 0, 256, 256, null);
				bmg = newImg;
			}
			
			return bmg;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
	
	
	
}
