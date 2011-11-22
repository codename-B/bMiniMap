package com.bpermissions.minimap;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
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
		if(buff == null) {
			buff = miniMap.getRender().buffer;
		} else if(buff != null) {
			if(buff != miniMap.getRender().buffer) {
				buff.clear();
				buff = miniMap.getRender().buffer;
				TextureUtils.getInstance().updateTexture(buff);
				buff.clear();
			}
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtils.getInstance().getId());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		// ChrizC told me to
		GL11.glBegin(GL11.GL_QUADS);	
		// A, A
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(10, 10);
		// a, A
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(10, 100);
		// a, a
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(100, 100);
		// A, a
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(100, 10);
		
		GL11.glEnd();
		
	}
	
	@Override
	public void onTick() {
		// Nothing
	}
	
	public JavaAddon getParent() {
		return parent;
	}
	
}
