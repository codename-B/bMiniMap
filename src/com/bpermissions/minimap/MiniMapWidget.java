package com.bpermissions.minimap;

import java.nio.ByteBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.java.JavaAddon;
import org.spoutcraft.spoutcraftapi.gui.GenericWidget;
import org.spoutcraft.spoutcraftapi.gui.WidgetType;

public class MiniMapWidget extends GenericWidget {

	private final JavaAddon parent;
	public final MiniMap miniMap;
	private ByteBuffer buff = null;
	
	public static int tx = 0;
	public static int ty = 0;
	public static int scale = 0;
	
	public MiniMapWidget(MiniMapAddon parent) {
		this.parent = parent;
		miniMap = new MiniMap(parent);
		try {
		String playerName = parent.getClient().getActivePlayer().getName();
		ByteBuffer buff = TextureUtils.convertImageData(PlayerIconCache.getInstance().get(playerName));
		TextureUtils.getInstance("icon."+playerName).initialUpload(buff);
		buff.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		Keyboard.poll();
		
		/*
		Move the minimap about with the arrow keys
		works but not being used
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			ty--;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			ty++;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			tx--;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			tx++;
		}
		*/
		// SCALE?
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if(scale > 0)
			scale--;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if(scale < 10)
			scale++;
		}
		
		// Global translation
		GL11.glTranslated(tx, ty, 0);
		
		// Code to draw the face of the player
		//drawPlayer();
		// Code moved to textureUtils
		buff = TextureUtils.render(miniMap, buff);

		// Global untranslation
		GL11.glTranslated(-tx, -ty, 0);
	}
	
	public void drawPlayer() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtils.getInstance("icon."+Spoutcraft.getActivePlayer().getName())
				.getId());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		// Attempt to translate
		GL11.glTranslated(10, 100, 0);
		
		// ChrizC told me to
		GL11.glBegin(GL11.GL_QUADS);
		// a, a
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(0, 0);
		// a, A
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 20);
		// A, A
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(20, 20);
		// A, a
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(20, 0);
		
		GL11.glEnd();
		
		GL11.glTranslated(-10, -100, 0);
	}
	
	@Override
	public void onTick() {
		// Nothing
	}

	public JavaAddon getParent() {
		return parent;
	}

}
