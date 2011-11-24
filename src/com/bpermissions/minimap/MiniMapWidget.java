package com.bpermissions.minimap;

import java.nio.ByteBuffer;

import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.gui.GenericWidget;
import org.spoutcraft.spoutcraftapi.gui.WidgetType;

public class MiniMapWidget extends GenericWidget {

	private final JavaAddon parent;
	public final MiniMap miniMap;
	private ByteBuffer buff = null;

	public MiniMapWidget(MiniMapAddon parent) {
		this.parent = parent;
		miniMap = new MiniMap(parent);
	}

	@Override
	public WidgetType getType() {
		return WidgetType.Texture;
	}

	@Override
	/**
	 * This is where all the fun stuff happens, we check to see
	 * if a new texture is available (and if it is we update it using
	 * TextureUtils.updateTexture(buff);
	 * 
	 * This can only be done in render();
	 * 
	 * We then draw a quad :)
	 */
	public void render() {
		// Code moved to textureUtils
		buff = TextureUtils.render(miniMap, buff);
	}
	
	@Override
	public void onTick() {
		// Nothing
	}

	public JavaAddon getParent() {
		return parent;
	}

}
