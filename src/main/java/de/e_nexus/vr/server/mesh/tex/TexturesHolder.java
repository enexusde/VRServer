package de.e_nexus.vr.server.mesh.tex;

import java.util.LinkedHashMap;
import java.util.Map;

public class TexturesHolder {
	protected final Map<TextureStage, Texture> textures = new LinkedHashMap<TextureStage, Texture>();

	public void setTexture(TextureStage stage, Texture t) {
		textures.put(stage, t);
	}
}
