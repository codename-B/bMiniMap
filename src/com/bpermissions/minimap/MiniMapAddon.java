package com.bpermissions.minimap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;

public class MiniMapAddon extends JavaAddon {

	File texture = new File("addons/texture.png");
	MiniMapWidget widget;
	public boolean isEnabled = false;
	MiniMapLabel label;

	public void loadOverlay() {
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
		isEnabled = false;
	}

	@Override
	public void onEnable() {
		TextureUtils.getInstance("minimap").initialUpload(128);
		loadOverlay();
		isEnabled = true;
		System.out.println("MiniMap enabled!");
		// This is to help me track down bugs
		// onEnable() doesn't print a full stacktrace on it's own
		try {
			widget = new MiniMapWidget(this);
			label = new MiniMapLabel(this);
			this.getClient().getActivePlayer().getMainScreen()
					.attachWidget(this, widget);
			this.getClient().getActivePlayer().getMainScreen()
					.attachWidget(this, label);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MiniMapWidget getWidget() {
		return widget;
	}

}
