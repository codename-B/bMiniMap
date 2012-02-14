package com.bpermissions.minimap;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.gui.GenericWidget;
import org.spoutcraft.spoutcraftapi.gui.WidgetType;

public class MiniMapWidget extends GenericWidget {

	private final JavaAddon parent;
	public final MiniMap miniMap;
	private ByteBuffer buff = null;
	
	public static double tx = 0;
	public static double ty = 0;
	
	public static int scale = 0;

	public static boolean rotate = true;
	
	public MiniMapWidget(MiniMapAddon parent) {
		this.parent = parent;
		miniMap = new MiniMap(parent);
	}
	
	public double getWidthX() {
		return Spoutcraft.getRenderDelegate().getScreenWidth();
	}
	
	public double getHeightY() {
		return Spoutcraft.getRenderDelegate().getScreenHeight();
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
		tx = getWidthX()-getWidthX()/5;
		// Global translation
		GL11.glTranslated(tx, ty, 0);
		
		// Code to draw the face of the player
		//drawPlayer();
		// Code moved to textureUtils
		buff = TextureUtils.render(miniMap, buff, (int) (getWidthX()/5));
		
		// Global untranslation
		GL11.glTranslated(-tx, -ty, 0);
	}
		
	@Override
	public void onTick() {
		// Nothing
	}

	public JavaAddon getParent() {
		return parent;
	}

}
