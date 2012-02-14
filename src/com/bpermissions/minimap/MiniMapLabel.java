package com.bpermissions.minimap;


import org.spoutcraft.spoutcraftapi.ChatColor;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;

import de.xzise.MinecraftUtil;

public class MiniMapLabel extends GenericLabel {
	private final MiniMapAddon parent;
	private static final String COORDS = ChatColor.YELLOW + "(%d, %d, %d)";
	private static final String COORDS_SLIME = ChatColor.YELLOW + "(%d, %d, %d, S)";
	private boolean showSlimeChunks = false;

	public MiniMapLabel(MiniMapAddon parent) {
		this.parent = parent;
	}

	@Override
	public void onTick() {
		FixedLocation loc = parent.getClient().getActivePlayer().getLocation();
		if (this.showSlimeChunks && MinecraftUtil.canSlimeSpawn(loc)) {
			setText(String.format(COORDS_SLIME, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		} else {
			setText(String.format(COORDS, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		}
	}

	public void setShowSlimeChunks(final boolean showSlimeChunks) {
		this.showSlimeChunks = showSlimeChunks;
	}

	public boolean getShowSlimeChunks() {
		return this.showSlimeChunks;
	}

	@Override
	public void render() {
//		int x = (int) getWidthX()-13-Spoutcraft.getMinecraftFont().getTextWidth(this.getText());
		final int x = (int) Math.round(MiniMapWidget.getStaticWidth() / 2 + MiniMapWidget.getStaticX() - Spoutcraft.getMinecraftFont().getTextWidth(this.getText()) / 2);
		this.setX(x).setY((int) (MiniMapWidget.getStaticWidth()));
		super.render();
	}
}
