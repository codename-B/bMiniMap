package com.bpermissions.minimap.gui;

import java.util.Arrays;

import org.spoutcraft.spoutcraftapi.gui.GenericComboBox;

import com.bpermissions.minimap.MiniMapWidget;

public class MiniMapModeButton extends GenericComboBox {
	
	public MiniMapModeButton() {
		setTooltip("Changes the mode of the minimap.");
		setItems(Arrays.asList("Normal", "Cavemap", "Density"));
		setSelection(MiniMapWidget.mode);
	}

	@Override
	public void onSelectionChanged(int i, String text) {
		System.out.println("SelectionChanged: "+i+" - "+text);
		if (i >= 0 && i <= 2) {
			MiniMapWidget.mode = i;
			System.out.println("MiniMapWidget set to mode"+MiniMapWidget.mode);
		}
	}

}
