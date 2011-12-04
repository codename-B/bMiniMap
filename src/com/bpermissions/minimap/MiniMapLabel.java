package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;

public class MiniMapLabel extends GenericLabel {
	private final JavaAddon parent;
	
	public MiniMapLabel(MiniMapAddon parent) {
		this.parent = parent;
		//this.setTextColor(new Color(255, 255, 255, 0));
		//You had alpha at max, also this currently is an odd
		//black, so I left it white.
	}
	
	@Override
	public void onTick() {
		ActivePlayer player = parent.getClient().getActivePlayer();
		this.setText("X: " + Math.round(player.getLocation().getX()) 
				+ " Y: " + Math.round(player.getLocation().getY()) 
				+ " Z: " + Math.round(player.getLocation().getZ()));
	}
	
	@Override
	public void render() {
		int x = (int) this.getScreen().getWidth()-100;
		this.setX(x).setY(MiniMap.width-20);
		super.render();
	}

}
