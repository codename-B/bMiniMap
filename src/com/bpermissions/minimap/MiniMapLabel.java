package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;

public class MiniMapLabel extends GenericLabel {
	private final MiniMapAddon parent;
	
	public MiniMapLabel(MiniMapAddon parent) {
		this.parent = parent;
		//this.setTextColor(new Color(255, 255, 255, 0));
		//You had alpha at max, also this currently is an odd
		//black, so I left it white.
	}
	
	@Override
	public void onTick() {
		FixedLocation loc = parent.getClient().getActivePlayer().getLocation();
		this.setText("X: " + Math.round(loc.getX()) 
				+ " Y: " + Math.round(loc.getY()) 
				+ " Z: " + Math.round(loc.getZ()));
	}
	
	@Override
	public void render() {
		int x = (int) this.getScreen().getWidth()-100;
		this.setX(x).setY(MiniMap.width-20);
		super.render();
	}

}
