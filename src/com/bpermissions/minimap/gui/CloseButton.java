package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericPopup;

public class CloseButton extends GenericButton {

	private final GenericPopup popup;
	
	public CloseButton(GenericPopup popup) {
		super("Close");
		this.popup = popup;
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		System.out.println("ButtonClickEvent");
		popup.close();
	}

}
