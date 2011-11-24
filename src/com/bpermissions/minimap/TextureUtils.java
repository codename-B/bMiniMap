package com.bpermissions.minimap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spoutcraft.spoutcraftapi.Spoutcraft;

public class TextureUtils {

	private TextureUtils() {
	}

	private static Map<String, TextureUtils> instances = new HashMap<String, TextureUtils>();
	private int textureID;

	/**
	 * Simply because I only need one instance of this, and I'd like to access
	 * it like this because I'm lazy.
	 * 
	 * Did I mention I'm lazy?
	 * 
	 * @return the class
	 */
	public static TextureUtils getInstance(String key) {
		if (instances.get(key) == null) {
			instances.put(key, new TextureUtils());
		}
		return instances.get(key);
	}
	
	/**
	 * Moved from MiniMapWidget
	 * 
	 * Parses the ByteBuffer and updates everything accordingly
	 * Returns the ByteBuffer to be stored as a reference in MiniMapWidget
	 * (or whatever parent class is handling things)
	 * 
	 * @param miniMap
	 * @param buff
	 * @return ByteBuffer (buff)
	 */
	public static ByteBuffer render(MiniMap miniMap, ByteBuffer buff) {
		if (buff == null) {
			// First render() ?
			buff = miniMap.getRender().buffer;
		} else if (buff != null) {
			if (buff != miniMap.getRender().buffer) {
				buff.clear();
				buff = miniMap.getRender().buffer;
				TextureUtils.getInstance("minimap").updateTexture(buff);
				buff.clear();
			}
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtils.getInstance("minimap")
				.getId());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// This is manually translated to get it so that up is where the player is looking, conventiently
		float rot = ((float) Spoutcraft.getActivePlayer().getLocation().getYaw() + 90) % 360;
		// Attempt to rotate?
		GL11.glTranslated(55, 55, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glTranslated(-55, -55, 0);
		
		// ChrizC told me to
		GL11.glBegin(GL11.GL_QUADS);
		// a, a
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(10, 10);
		// a, A
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(10, 100);
		// A, A
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(100, 100);
		// A, a
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(100, 10);

		GL11.glEnd();
		
		return buff;
	}

	/**
	 * Setup the initial Texture environment
	 * but with a ByteBuffer
	 */
	public void initialUpload(ByteBuffer buff) {
		System.out.println("Generating texture ID");
		IntBuffer bInt = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(bInt);
		textureID = bInt.get(0);
		System.out.println("ID:" + textureID + " Binding texture");
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, MiniMap.width,
				MiniMap.width, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				buff);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
	}
	
	/**
	 * Setup the initial Texture environment
	 */
	public void initialUpload() {
		System.out.println("Generating texture ID");
		IntBuffer bInt = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(bInt);
		textureID = bInt.get(0);
		System.out.println("ID:" + textureID + " Binding texture");
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, MiniMap.width,
				MiniMap.width, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
	}

	/**
	 * WARNING! ONLY CALL FROM render(); in the Widget! or baaaaaad things will
	 * happen, trust me.
	 * 
	 * @param texture
	 */
	public void updateTexture(ByteBuffer texture) {
		try {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
					MiniMap.width, MiniMap.width, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, texture);
			// Now that it's cached
			texture.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return textureID;
	}

}
