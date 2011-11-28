package com.bpermissions.minimap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
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
	
	@SuppressWarnings("rawtypes")
	/**
	 * Moved from MiniMapRender
	 * 
	 * Convert the bufferedImage into a byteBuffer
	 * suitable for textures
	 * @param bufferedImage
	 * @return ByteBuffer (from bufferedImage)
	 */
	public static ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		ColorModel glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
				bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
		texImage = new BufferedImage(glAlphaColorModel, raster, true,
				new Hashtable());

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, MiniMap.width, MiniMap.width);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
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
		// Is the render up to date
		if (buff == null) {
			// First render() ?
			buff = miniMap.getRender().buffer;
		} else if (buff != null) {
			// If the byteBuffer is not up to date, update it!
			if (buff != miniMap.getRender().buffer) {
				// Memory management
				buff.clear();
				buff = miniMap.getRender().buffer;
				TextureUtils.getInstance("minimap").updateTexture(buff);
				// More memory management
				buff.clear();
			}
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureUtils.getInstance("minimap")
				.getId());
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		// This is manually translated to get it so that up is where the player is looking, conventiently
		float rot = ((float) Spoutcraft.getActivePlayer().getLocation().getYaw() + 90) % 360;
		// Attempt to rotate?
		
		int width = 100+MiniMapWidget.scale*10;
		int center = (width)/2;
		
		GL11.glTranslated(center, center, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glTranslated(-center, -center, 0);
		
		// ChrizC told me to
		GL11.glBegin(GL11.GL_QUADS);
		
		// a, a
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(0, 0);
		// a, A
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(width, 0);
		// A, A
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(width, width);
		// A, a
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(0, width);

		GL11.glEnd();
		
		GL11.glRotatef(-rot, 0, 0, 1);
		
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
