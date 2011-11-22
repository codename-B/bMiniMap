package com.bpermissions.minimap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

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
