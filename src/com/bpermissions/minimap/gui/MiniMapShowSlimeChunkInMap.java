package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;

import com.bpermissions.minimap.MiniMapRender;

public class MiniMapShowSlimeChunkInMap extends GenericCheckBox {

	private final MiniMapRender render;

	public MiniMapShowSlimeChunkInMap(final MiniMapRender render) {
		super("Show slime chunks in map");
		this.render = render;
		this.setTooltip("Toogles if the slime chunks are visible in the map.");
		this.setChecked(this.render.isShowingSlimeChunks());
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		this.render.setShowSlimeChunks(this.isChecked());
	}
}
