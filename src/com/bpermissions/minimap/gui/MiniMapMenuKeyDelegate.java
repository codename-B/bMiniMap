package com.bpermissions.minimap.gui;

import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.keyboard.BindingExecutionDelegate;
import org.spoutcraft.spoutcraftapi.keyboard.KeyBinding;

import com.bpermissions.minimap.MiniMapAddon;

/**
 *
 * @author Sean
 */
public class MiniMapMenuKeyDelegate extends BindingExecutionDelegate {

	private final MiniMapAddon parent;

	public MiniMapMenuKeyDelegate(MiniMapAddon parent) {
		this.parent = parent;
	}

	@Override
	public void onKeyPress(int i, KeyBinding kb) {
		Spoutcraft.getActivePlayer().getMainScreen().attachPopupScreen(new GUISelector(parent));
	}

	@Override
	public void onKeyRelease(int i, KeyBinding kb) {
		//Nothing
	}
	
	public JavaAddon getParent() {
		return parent;
	}
	
}
