package com.bpermissions.minimap;

import org.spoutcraft.spoutcraftapi.ChatColor;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.keyboard.BindingExecutionDelegate;
import org.spoutcraft.spoutcraftapi.keyboard.KeyBinding;

/**
 *
 * @author Sean
 */
public class MiniMapZoomKeyDelegate extends BindingExecutionDelegate {

	private final JavaAddon parent;

	public MiniMapZoomKeyDelegate(JavaAddon parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void onKeyPress(int i, KeyBinding kb) {
		switch (MiniMapWidget.scale) {
			case 0:
				MiniMapWidget.scale = 64;
				zoomMessage(2);
				break;
			case 64:
				MiniMapWidget.scale = 128;
				zoomMessage(3);
				break;
			case 128:
				MiniMapWidget.scale = -24;
				zoomMessage(0);
				break;
			case -24:
				MiniMapWidget.scale = 0;
				zoomMessage(1);
				break;
		}
	}

	@Override
	public void onKeyRelease(int i, KeyBinding kb) {
		//Nothing
	}

	private void zoomMessage(int zoomLevel) {
		parent.getClient().getActivePlayer().sendMessage(ChatColor.BLUE + "** ZOOM LEVEL " + zoomLevel);
	}
}
