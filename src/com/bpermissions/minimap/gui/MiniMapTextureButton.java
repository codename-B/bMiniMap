package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericCheckBox;

import com.bpermissions.minimap.TextureMapper;

public class MiniMapTextureButton extends GenericCheckBox {

	public MiniMapTextureButton() {
		super("Use current texture pack");
		setTooltip("Do you want to use your current texture pack?");
		setChecked(TextureMapper.getUseTexture());
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		TextureMapper.setUseTexture(isChecked());
	}
}
