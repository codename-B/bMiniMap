package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;

import com.bpermissions.minimap.MiniMapWidget;

public class MiniMapRotateButton extends GenericCheckBox {

	public MiniMapRotateButton() {
		super("Northwards orientated");
		setTooltip("What should rotate?");
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		MiniMapWidget.rotate = !isChecked();
	}
}
