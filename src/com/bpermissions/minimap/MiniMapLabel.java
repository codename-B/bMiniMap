package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.ChatColor;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;

public class MiniMapLabel extends GenericLabel {
	private final MiniMapAddon parent;
	public static boolean show = true;
	private final String coords = ChatColor.YELLOW + "(%d, %d, %d)";
	
	public MiniMapLabel(MiniMapAddon parent) {
		this.parent = parent;
	}

	@Override
	public void onTick() {
		FixedLocation loc = parent.getClient().getActivePlayer().getLocation();
		setText(String.format(coords, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	
	public boolean isVisible() {
		return super.isVisible() && show;
	}
	
	public double getWidthX() {
		return Spoutcraft.getRenderDelegate().getScreenWidth();
	}

	public double getHeightY() {
		return Spoutcraft.getRenderDelegate().getScreenHeight();
	}

	@Override
	public void render() {
		int x = (int) getWidthX()-13-Spoutcraft.getMinecraftFont().getTextWidth(this.getText());
		this.setX(x).setY((int) (getWidthX()/5));
		super.render();
	}
}
