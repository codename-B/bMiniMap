package com.bpermissions.minimap.gui;

import org.bukkit.ChatColor;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.gui.GenericPopup;
import org.spoutcraft.spoutcraftapi.gui.RenderPriority;

import com.bpermissions.minimap.MiniMapAddon;

public class GUISelector extends GenericPopup {

	private GenericButton scale;
	private GenericButton close;
	private GenericButton mode;
	private GenericButton texture;
	private GenericButton rotate;
	
	public GUISelector(MiniMapAddon addon) {
		// Label
				GenericLabel label = new GenericLabel(ChatColor.YELLOW + "Texture packs");
				label.setX(175).setY(25);
				label.setPriority(RenderPriority.Lowest);
				label.setWidth(-1).setHeight(-1);
				
				// Scale button
				scale = new MiniMapScaleButton();
				scale.setX(95).setY(30);
				scale.setWidth(150).setHeight(20);
				scale.setPriority(RenderPriority.Lowest);
				
				// Mode button
				mode = new MiniMapModeButton();
				mode.setX(95).setY(60);
				mode.setWidth(150).setHeight(20);
				mode.setPriority(RenderPriority.Lowest);
				
				// Texture button
				texture = new MiniMapTextureButton();
				texture.setX(95).setY(90);
				texture.setWidth(150).setHeight(20);
				texture.setPriority(RenderPriority.Lowest);
				
				// Rotate button
				rotate = new MiniMapRotateButton();
				rotate.setX(95).setY(120);
				rotate.setWidth(150).setHeight(20);
				rotate.setPriority(RenderPriority.Lowest);
				
				// Close button
				close = new CloseButton(this);
				close.setX(155).setY(195);
				close.setWidth(60).setHeight(20);
				close.setPriority(RenderPriority.Lowest);
				
				this.attachWidget(addon, scale);
				this.attachWidget(addon, mode);
				this.attachWidget(addon, texture);
				this.attachWidget(addon, rotate);

				//this.attachWidget(addon, close);
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
