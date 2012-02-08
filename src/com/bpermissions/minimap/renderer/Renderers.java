package com.bpermissions.minimap.renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bpermissions.minimap.MiniMapCache;
import com.bpermissions.minimap.TextureMapper;

public class Renderers {

	public static final Color TRANSPARENT = new Color(255, 255, 255 ,0);
	public static final int TRANSPARENT_RGB = TRANSPARENT.getRGB();

	private final Map<String, Renderer> renderers;
	private final MiniMapCache cache = MiniMapCache.getInstance();
	private final TextureMapper mapper;

	public Renderers(final int width, final int height) {
		this.mapper = new TextureMapper();

		this.renderers = new HashMap<String, Renderer>(3);
		putRenderer(new HeightRenderer(width, height, this.cache, this.mapper));
		putRenderer(new CaveRenderer(width, height, this.mapper));
		putRenderer(new DensityRenderer(width, height));
	}

	private void putRenderer(final Renderer renderer) {
		this.renderers.put(renderer.getName(), renderer);
	}

	public List<Renderer> getRenderers() {
		return new ArrayList<Renderer>(this.renderers.values());
	}

	public Renderer getRenderer(final String name) {
		return this.renderers.get(name);
	}
}
