package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

import com.bpermissions.minimap.MiniMapWidget;

public class MiniMapRotateButton extends GenericButton {

	public MiniMapRotateButton() {
		setTooltip("What should rotate?");
	}
	
	@Override
	public String getText() {
		if(MiniMapWidget.rotate)
			return "Rotate: Map";
		return "Rotate: Arrow";
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if(MiniMapWidget.rotate) {
			MiniMapWidget.rotate = false;
		} else {
			MiniMapWidget.rotate = true;
		}
	}
	
	
}
