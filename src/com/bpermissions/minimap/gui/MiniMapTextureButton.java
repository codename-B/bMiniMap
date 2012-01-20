package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

import com.bpermissions.minimap.TextureMapper;

public class MiniMapTextureButton extends GenericButton {

	public MiniMapTextureButton() {
		setTooltip("Do you want to use your current texture pack?");
	}
	
	@Override
	public String getText() {
		if(TextureMapper.getUseTexture())
			return "Texture pack: Current";
		return "Texture pack: Default";
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		TextureMapper.toggleUseTexture();
	}
	
}
