package com.bpermissions.minimap.gui;

import java.util.ArrayList;
import java.util.List;

import org.spoutcraft.spoutcraftapi.gui.GenericComboBox;

import com.bpermissions.minimap.MiniMap;
import com.bpermissions.minimap.renderer.Renderer;
import com.bpermissions.minimap.renderer.Renderers;

public class MiniMapModeButton extends GenericComboBox {

	private final MiniMap miniMap;
	private final Renderers renderers;

	public MiniMapModeButton(final MiniMap miniMap) {
		setTooltip("Changes the mode of the minimap.");
		this.miniMap = miniMap;
		this.renderers = miniMap.getRenderers();
		List<Renderer> renderersList = this.renderers.getRenderers();
		List<String> names = new ArrayList<String>(renderersList.size());
		int idx = 0;
		final String selectedName = this.miniMap.getRender().getRenderer().getName();
		for (int i = 0; i < renderersList.size(); i++) {
			final String name = renderersList.get(i).getName();
			if (name.equals(selectedName)) {
				idx = i;
			}
			names.add(name);
		}
		setItems(names);
		setSelection(idx);
	}

	@Override
	public void onSelectionChanged(int i, String text) {
		System.out.println("SelectionChanged: "+i+" - "+text);
		this.miniMap.getRender().setRenderer(this.renderers.getRenderer(text));
	}

}
