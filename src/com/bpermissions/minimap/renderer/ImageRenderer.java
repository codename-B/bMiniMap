package com.bpermissions.minimap.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.MiniMapRender;
import com.bpermissions.minimap.MiniMapWidget;

import de.xzise.ColorUtil;

public abstract class ImageRenderer implements Renderer {

	private final BufferedImage image;
	public final int width;
	public final int height;

	public ImageRenderer(final int width, final int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.width = width;
		this.height = height;
	}

	protected final BufferedImage getImage() {
		return this.image;
	}

	protected abstract int getColor(final ActivePlayer player, final int x, final int z);

	protected int applyDy(final int rgb, int dy) {
		return this.applyDy(ColorUtil.getRedFromRGB(rgb), ColorUtil.getGreenFromRGB(rgb), ColorUtil.getBlueFromRGB(rgb), ColorUtil.getAlphaFromRGB(rgb), dy);
	}

	protected int applyDy(final int red, final int green, final int blue, int dy) {
		return this.applyDy(red, green, blue, 0xFF, dy);
	}

	protected int applyDy(final int red, final int green, final int blue, final int alpha, int dy) {
		// do shading (and even out top+bottom a little)
		if(dy > 32)
			dy = dy - dy / 16;
		if(dy > 48)
			dy = dy - dy / 16;
		if(dy < -32)
			dy = dy + dy / 16;
		if(dy < -48)
			dy = dy + dy / 16;
		// apply shading to the rgb
		return ColorUtil.getRGB(MiniMapRender.betweenRGB(red + dy), MiniMapRender.betweenRGB(green + dy), MiniMapRender.betweenRGB(blue + dy), MiniMapRender.betweenRGB(alpha));
	}

	@Override
	public final void render(ActivePlayer player) {
		long start = System.currentTimeMillis();
		int scale = MiniMapWidget.scale;
		/*
		 * Generate the image and apply shading 
		 */
		this.getImage().flush();
		Graphics gr = this.getImage().getGraphics();

		gr.setColor(Renderers.TRANSPARENT);

		final int playerX = player.getLocation().getBlockX();
		final int playerZ = player.getLocation().getBlockZ();
		boolean canceled = false;
		for (int x = -this.width/2; x < this.width/2 && !canceled; x++) {
			for (int z = -this.height/2; z < this.height/2 && !canceled; z++) {
				// If the minimap render takes longer than 1000ms or the scale is changed exit the render pass
				if(System.currentTimeMillis()-start > 1000 || MiniMapWidget.scale != scale) {
					canceled = true;
					break;
				}
				// Use the scale to scale RELATIVELY
				final int tx = (int) (playerX + (x/(3-scale)));
				final int tz = (int) (playerZ + (z/(3-scale)));
				// then color in
				this.getImage().setRGB(x+width/2, z+width/2, this.getColor(player, tx, tz));
			}
		}
		// Clean up after yourself
		gr.dispose();
	}

	@Override
	public void copy(final BufferedImage image) {
		// Apply the image to the minimap image
		Graphics gr = image.getGraphics();
		gr.drawImage(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), null);
		gr.dispose();
		// Can we help stop the memory leak here?
		this.image.flush();
	}
}
