package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;

import com.bpermissions.minimap.MiniMapLabel;

public class MiniMapShowSlimeChunkInCoordinates extends GenericCheckBox {

	private final MiniMapLabel miniMapLabel;

	public MiniMapShowSlimeChunkInCoordinates(final MiniMapLabel miniMapLabel) {
		super("Show slime chunk in coordinates");
		this.miniMapLabel = miniMapLabel;
		setTooltip("Toggles if the slime chunk will be shown in the coordinates.");
		setChecked(this.miniMapLabel.getShowSlimeChunks());
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		this.miniMapLabel.setShowSlimeChunks(this.isChecked());
	}
}
