package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;
import org.spoutcraft.spoutcraftapi.gui.Widget;

public class MiniMapShowCoordinates extends GenericCheckBox {

	private final Widget label;

	public MiniMapShowCoordinates(final Widget label) {
		super("Show coordinates");
		this.label = label;
		setTooltip("Toggles the coordinate display below the minimap");
		setChecked(label.isVisible());
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		this.label.setVisible(this.isChecked());
	}
}
