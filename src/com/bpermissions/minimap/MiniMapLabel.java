package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;

public class MiniMapLabel extends GenericLabel {
	private final MiniMapAddon parent;
	
	public MiniMapLabel(MiniMapAddon parent) {
		this.parent = parent;
	}
	
	@Override
	public void onTick() {
		FixedLocation loc = parent.getClient().getActivePlayer().getLocation();
		this.setText("X: " + loc.getBlockX() 
				+ " Y: " + loc.getBlockY() 
				+ " Z: " + loc.getBlockZ());
	}
	
	@Override
	public void render() {
		int x = (int) this.getScreen().getWidth()-100;
		this.setX(x).setY((int) (this.getScreen().getWidth()/5));
		super.render();
	}

}
