package com.bpermissions.minimap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import org.lwjgl.input.Keyboard;

import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.keyboard.KeyBinding;

import com.bpermissions.minimap.gui.MiniMapMenuKeyDelegate;

public class MiniMapAddon extends JavaAddon {

	File texture = new File("addons/bMiniMap/texture.png");
	MiniMapWidget widget;
	MiniMapLabel label;
	KeyBinding zoomKeyBind;

	public static BufferedImage defaultTexture;
	
	public void loadOverlay() {
		try {
			// Optionally load the image from outside the jar
			File dataDir = new File(Spoutcraft.getAddonFolder() + File.separator + "bMiniMap");
			File roundmapImg = new File(dataDir + File.separator + "roundmap.png");
			
			// Create dir always, does not matter if already exists
			dataDir.mkdir();
			
			// Boolean for successful image upload so we can skip loading from jar
			boolean externalFileUsed = false;
			
			// Check for/use extranal file
			if(roundmapImg.exists()) {
				externalFileUsed = true;
				try {
					BufferedImage bmg = ImageIO.read(roundmapImg);
					
					ByteBuffer buff = TextureUtils.convertImageData(bmg, bmg.getWidth());
					TextureUtils.getInstance("overlay").initialUpload(buff, bmg.getWidth());
					buff.clear();
				} catch (Exception e) {
					e.printStackTrace();
					externalFileUsed = false;
				}
			}
			
			// Load the image from the jar? :O
			if(externalFileUsed) return;
			
			// Instead of using a hardcoded .jar file name, get whatever .jar contains this addon's code
			//File jarFile = new File(Spoutcraft.getAddonFolder(), "bMiniMap.jar");	
			File jarFile = new File(MiniMap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			
			JarFile jar = new JarFile(jarFile);
			ZipEntry ze = jar.getEntry("roundmap.png");
			InputStream is = jar.getInputStream(ze);
			BufferedImage bmg = ImageIO.read(is);
			// Don't forget cleanup!
			is.close();
			// Now we read the default terrain.png
			ze = jar.getEntry("terrain.png");
			is = jar.getInputStream(ze);
			defaultTexture = ImageIO.read(is);
			// And close
			is.close();
			jar.close();
			
			ByteBuffer buff = TextureUtils.convertImageData(bmg, 512);
			TextureUtils.getInstance("overlay").initialUpload(buff, 512);
			buff.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void onDisable() {
		System.out.println("MiniMap disabled!");
		this.getClient().getKeyBindingManager().registerControl(zoomKeyBind);
	}

	@Override
	public void onEnable() {
		try {
		TextureUtils.getInstance("minimap").initialUpload(256);
		loadOverlay();
		System.out.println("MiniMap enabled!");
		// This is to help me track down bugs
		// onEnable() doesn't print a full stacktrace on it's own
			widget = new MiniMapWidget(this);
			label = new MiniMapLabel(this);
			this.getClient().getActivePlayer().getMainScreen()
					.attachWidget(this, widget);
			this.getClient().getActivePlayer().getMainScreen()
					.attachWidget(this, label);
			zoomKeyBind = new KeyBinding(Keyboard.KEY_M, this, "Zoom level", "Changes through the available zoom levels for the minimap.");
			zoomKeyBind.setDelegate(new MiniMapMenuKeyDelegate(this));
			this.getClient().getKeyBindingManager().registerControl(zoomKeyBind);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MiniMapWidget getWidget() {
		return widget;
	}

	public MiniMapLabel getLabel() {
		return label;
	}
}
