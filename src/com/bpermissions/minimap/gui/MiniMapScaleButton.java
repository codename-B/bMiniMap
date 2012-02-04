package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

import com.bpermissions.minimap.MiniMapWidget;

public class MiniMapScaleButton extends GenericButton {

	public MiniMapScaleButton() {
		setTooltip("Changes the zoom level of the minimap.");
	}
	
	@Override
	public String getText() {
		switch (MiniMapWidget.scale) {
		case 0:
			return "Zoom level: High";
		case 1:
			return "Zoom level: Normal";
		case 2:
			return "Zoom level: Low";
		}
		return "Zoom level: Unknown";
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		System.out.println("ButtonClickEvent");
		switch (MiniMapWidget.scale) {
		case 0:
			MiniMapWidget.scale = 1;
			break;
		case 1:
			MiniMapWidget.scale = 2;
			break;
		case 2:
			MiniMapWidget.scale = 0;
			break;
		}
	}

}
