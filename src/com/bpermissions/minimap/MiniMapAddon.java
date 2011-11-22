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
		TextureUtils.getInstance().initialUpload();
		isEnabled = true;
		System.out.println("MiniMap enabled!");
		
		try {
			
		widget = new MiniMapWidget(this);
		this.getClient().getActivePlayer().getMainScreen().attachWidget(this, widget);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	
	public MiniMapWidget getWidget() {
		return widget;
	}

}
