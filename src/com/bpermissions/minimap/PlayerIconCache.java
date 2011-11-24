package com.bpermissions.minimap;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class PlayerIconCache {
	private static PlayerIconCache instance = null;
	
	private PlayerIconCache() {
	}
	
	public static PlayerIconCache getInstance() {
		if(instance == null) {
			instance = new PlayerIconCache();
		}
		return instance;
	}
	
	private Map<String, BufferedImage> players = new HashMap<String, BufferedImage>();
	
	public BufferedImage get(String player) {
		// If the image doesn't exist, get it!
		if(!players.contains(player)) {
			// Extra test stuff
			try {
			URL url = new URL("http://direct.minotar.net/avatar/"+player+"/128.png");
			URLConnection con = url.openConnection();
			
			InputStream is = con.getInputStream();
			BufferedImage bmg = ImageIO.read(is);
			// Don't forget cleanup!
			is.close();
			
			players.put(player, bmg);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return players.get(player);
	}
	
	public  void clear() {
		players.clear();
	}

}
