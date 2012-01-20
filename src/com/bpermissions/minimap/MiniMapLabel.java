package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.Spoutcraft;
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
	
	public double getWidthX() {
		return Spoutcraft.getRenderDelegate().getScreenWidth();
	}
	
	public double getHeightY() {
		return Spoutcraft.getRenderDelegate().getScreenHeight();
	}
	
	@Override
	public void render() {
		int x = (int) getWidthX()-10-Spoutcraft.getMinecraftFont().getTextWidth(this.getText());
		this.setX(x).setY((int) (getWidthX()/5));
		super.render();
	}

}
