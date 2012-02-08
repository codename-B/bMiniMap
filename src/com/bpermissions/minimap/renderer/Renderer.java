package com.bpermissions.minimap.renderer;

import java.awt.image.BufferedImage;

import org.spoutcraft.spoutcraftapi.entity.ActivePlayer;

public interface Renderer {

	String getName();

	void render(final ActivePlayer player);

	void copy(final BufferedImage image);
}
