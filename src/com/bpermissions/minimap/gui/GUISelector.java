package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;
import org.spoutcraft.spoutcraftapi.gui.GenericPopup;
import org.spoutcraft.spoutcraftapi.gui.RenderPriority;

import com.bpermissions.minimap.MiniMapAddon;

public class GUISelector extends GenericPopup {

	private GenericButton scale;
	private GenericButton mode;
	private GenericButton texture;
	private GenericButton rotate;
	private GenericCheckBox showCoordinates;

	public GUISelector(MiniMapAddon addon) {
		// Label

		// Scale button
		scale = new MiniMapScaleButton();
		scale.setX(95).setY(30);
		scale.setWidth(150).setHeight(20);

//		GenericLabel modeText = new GenericLabel("Mode:");
//		modeText.setX(95).setY(60);
//		modeText.setWidth(30).setHeight(20);
//		modeText.setPriority(RenderPriority.Lowest);

		// Mode button
		mode = new MiniMapModeButton(addon.getWidget().miniMap);
		mode.setX(95).setY(60);
		mode.setWidth(150).setHeight(20);
		mode.setPriority(RenderPriority.Lowest);

		// Texture button
		texture = new MiniMapTextureButton();
		texture.setX(95).setY(90);
		texture.setWidth(150).setHeight(20);

		// Rotate button
		rotate = new MiniMapRotateButton();
		rotate.setX(95).setY(120);
		rotate.setWidth(150).setHeight(20);

		MiniMapShowSlimeChunkInCoordinates showSlimeChunkInCoordinates = new MiniMapShowSlimeChunkInCoordinates(addon.getLabel());

		// Show coordinates
		showCoordinates = new MiniMapShowCoordinates(addon.getLabel(), showSlimeChunkInCoordinates);
		showCoordinates.setX(95).setY(150);
		showCoordinates.setWidth(150).setHeight(20);

		showSlimeChunkInCoordinates.setX(95).setY(180);
		showSlimeChunkInCoordinates.setWidth(150).setHeight(20);

		MiniMapShowSlimeChunkInMap showSlimeChunkInMap = new MiniMapShowSlimeChunkInMap(addon.getWidget().miniMap.getRender());
		showSlimeChunkInMap.setX(95).setY(210);
		showSlimeChunkInMap.setWidth(150).setHeight(20);

		this.attachWidget(addon, scale);
//		this.attachWidget(addon, modeText);
		this.attachWidget(addon, mode);
		this.attachWidget(addon, texture);
		this.attachWidget(addon, rotate);
		this.attachWidget(addon, showCoordinates);
		this.attachWidget(addon, showSlimeChunkInCoordinates);
		this.attachWidget(addon, showSlimeChunkInMap);
	}
	

	@Override
	public boolean close() {
		Spoutcraft.getActivePlayer().getMainScreen().removeWidget(this);
		return true;
	}

	@Override
	public boolean isPausingGame() {
		return true;
	}

}
