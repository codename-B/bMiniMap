package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.gui.Color;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;

public class MiniMapLabel extends GenericLabel {
	
	MiniMapAddon parent;
	FixedLocation loc;
	public MiniMapLabel(MiniMapAddon parent) {
		super("0, 0, 0");
		this.setVisible(true);
		this.parent = parent;
		this.setTextColor(new Color(255, 255, 255, 255));
	}

	@Override
	public void render() {
		this.setX((int) (this.getScreen().getWidth()/2));
		this.setY((int) (this.getScreen().getHeight()/2));
		loc = parent.getClient().getActivePlayer().getLocation();

		this.setText(loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ());
		super.render();
	}

}
