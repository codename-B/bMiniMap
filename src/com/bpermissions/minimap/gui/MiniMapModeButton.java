package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

import com.bpermissions.minimap.MiniMapWidget;

public class MiniMapModeButton extends GenericButton {
	
	public MiniMapModeButton() {
		setTooltip("Changes the mode of the minimap.");
	}
	
	@Override
	public String getText() {
		switch (MiniMapWidget.mode) {
		case 0:
			return "Mode: Normal";
		case 1:
			return "Mode: Cavemap";
		case 2:
			return "Mode: Density";
		}
		return "Mode: Unknown";
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		switch (MiniMapWidget.mode) {
		case 0:
			MiniMapWidget.mode = 1;
			break;
		case 1:
			MiniMapWidget.mode = 2;
			break;
		case 2:
			MiniMapWidget.mode = 0;
			break;
		}
	}

}
