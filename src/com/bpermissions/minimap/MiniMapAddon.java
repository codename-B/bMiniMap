package com.bpermissions.minimap;

import java.io.File;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;

public class MiniMapAddon extends JavaAddon {

	File texture = new File("addons/texture.png");
	MiniMapWidget widget;
	public boolean isEnabled = false;

	@Override
	public void onDisable() {
		System.out.println("MiniMap disabled!");
		isEnabled = false;
	}

	@Override
	public void onEnable() {
		TextureUtils.getInstance("minimap").initialUpload();
		
		isEnabled = true;
		System.out.println("MiniMap enabled!");
		// This is to help me track down bugs
		// onEnable() doesn't print a full stacktrace on it's own
		try {
			widget = new MiniMapWidget(this);
			
			this.getClient().getActivePlayer().getMainScreen()
					.attachWidget(this, widget);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MiniMapWidget getWidget() {
		return widget;
	}

}
