package com.bpermissions.minimap;

import java.awt.image.BufferedImage;

public class MiniMap {

	public static int width = 128;
	public static int radius = width / 2;
	public static double zoom = 1;

	private final MiniMapRender render;
	private BufferedImage image;
	private MiniMapAddon parent;

	/**
	 * We initialise and reuse the BufferedImage here for the whole
	 * shizzledizzle
	 * 
	 * @param parent
	 * @param handler
	 */
	public MiniMap(MiniMapAddon parent) {
		this.parent = parent;
		image = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
		render = new MiniMapRender(this);
		render.start();

	}

	/**
	 * The JavaAddon (Spout yay)
	 * 
	 * @return JavaAddon(MiniMapAddon)
	 */
	public MiniMapAddon getParent() {
		return parent;
	}

	/**
	 * Convenience method
	 * 
	 * @return Image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Return the thread/render
	 * 
	 * @return Thread(MiniMapRender)
	 */
	public MiniMapRender getRender() {
		return render;
	}

}