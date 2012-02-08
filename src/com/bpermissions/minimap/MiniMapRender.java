package com.bpermissions.minimap;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

import com.bpermissions.minimap.renderer.Renderer;
import com.bpermissions.minimap.renderer.Renderers;

/**
 * Runs the render task for the minimap and converts chunk data to a
 * BufferedImage then converts the BufferedImage to a bytebuffer!
 */
public class MiniMapRender extends Thread {

	private final MiniMap parent;

	public ByteBuffer buffer;

	private Renderer renderer;
	private Renderer rendererBuffer;
	private Object lock = new Object();

	/**
	 * MiniMapRender runs the miniMap render async
	 * 
	 * @param parent
	 */
	MiniMapRender(MiniMap parent) {
		this.parent = parent;
	}

	public void setRenderer(final Renderer renderer) {
		synchronized (this.lock) {
			this.rendererBuffer = renderer;
		}
	}

	public Renderer getRenderer() {
		return this.renderer;
	}

	@Override
	/**
	 * Asynchronously updates the minimap
	 */
	public void run() {
		while (parent.getParent().isEnabled()) {
			synchronized (this.lock) {
				if (this.rendererBuffer != null) {
					this.renderer = rendererBuffer;
					this.rendererBuffer = null;
				}
			}
			long start = System.currentTimeMillis();
			int scale = MiniMapWidget.scale;
			if (this.renderer != null) {
				try {
					ActivePlayer player = parent.getParent().getClient().getActivePlayer();
	
					BufferedImage image = this.parent.getImage();
					this.renderer.render(player);
					this.renderer.copy(image);
					// Cut the image into a circle
					for (int x = 0; x < MiniMap.WIDTH; x++)
						for (int z = 0; z < MiniMap.WIDTH; z++) {
							int center = MiniMap.RADIUS;
							int xd = (x - center);
							int zd = (z - center);
							int distance = (xd * xd + zd * zd);
							// distance squared is fast and efficient enough for
							// what we need
							if (distance >= (MiniMap.RADIUS - 2) * (MiniMap.RADIUS - 2))
								image.setRGB(x, z, Renderers.TRANSPARENT_RGB);
						}
					// Clear the buffer just to REALLY clean up
					if (buffer != null)
						buffer.clear();
					// And set it to the new buffer
					buffer = TextureUtils.convertImageData(image, 256);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				long finish = 500 - (System.currentTimeMillis() - start);
				if (finish > 0 && MiniMapWidget.scale == scale)
					sleep(finish);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int betweenRGB(final int value) {
		return between(value, 0, 255);
	}

	public static int between(final int value, final int min, final int max) {
		if (value > max) {
			return max;
		} else if (value < min) {
			return min;
		} else {
			return value;
		}
	}

	/**
	 * Another nice convenience method, I love navigation!
	 * 
	 * @return MiniMap
	 */
	public MiniMap getParent() {
		return parent;
	}

}