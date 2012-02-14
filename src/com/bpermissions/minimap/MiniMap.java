package com.bpermissions.minimap;

import java.awt.image.BufferedImage;

import com.bpermissions.minimap.renderer.HeightRenderer;
import com.bpermissions.minimap.renderer.Renderers;

public class MiniMap {

	public static final int WIDTH = 256;
	public static final int RADIUS = WIDTH / 2;
	public static double zoom = 1;

	private final MiniMapRender render;
	private BufferedImage image;
	private MiniMapAddon parent;
	private Renderers renderers;

	/**
	 * We initialise and reuse the BufferedImage here for the whole
	 * shizzledizzle
	 * 
	 * @param parent
	 * @param handler
	 */
	public MiniMap(MiniMapAddon parent) {
		this.parent = parent;
		this.renderers = new Renderers(WIDTH, WIDTH);
		image = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_INT_ARGB);
		render = new MiniMapRender(this);
		render.setRenderer(this.renderers.getRenderer(HeightRenderer.NAME));
		render.start();
	}

	public Renderers getRenderers() {
		return this.renderers;
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