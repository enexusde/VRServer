/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.tex.Texture;

/**
 * Represents a client session in the server. Stores what meshes are already
 * known by the client and must not be transfered to the client.
 */
public final class VRSession {
	private final InetAddress remoteAddr;
	private final Map<Integer, Mesh> meshIds = new LinkedHashMap<>(0);
	private final Map<Integer, Texture> textureIds = new LinkedHashMap<>(0);
	private UUID uuid;

	private VRSession(InetAddress remoteAddr, UUID uuid) {
		this.remoteAddr = remoteAddr;
		this.uuid = uuid;
	}

	public void registerMesh(int id, Mesh<?> mesh) {
		meshIds.put(id, mesh);
	}

	public boolean hasMesh(Mesh mesh) {
		return meshIds.containsValue(mesh);
	}

	public void registerTexture(int id, Texture texture) {
		textureIds.put(id, texture);
	}

	public boolean hasTexture(Texture texture) {
		return textureIds.containsValue(texture);
	}

	public Integer getMeshId(Mesh mesh) {
		for (Entry<Integer, Mesh> entry : meshIds.entrySet()) {
			if (entry.getValue() == mesh) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Integer getTexureId(Texture texture) {
		for (Entry<Integer, Texture> entry : textureIds.entrySet()) {
			if (entry.getValue() == texture) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static VRSession registerNewSession(InetAddress remoteAddr, VRSessionStorage storage) {
		VRSession newSession = new VRSession(remoteAddr, storage.newUUID());
		storage.add(newSession);
		return newSession;
	}

	public boolean isThisSession(InetAddress remoteAddr, UUID uid) {
		return (uid == this.uuid || uid.equals(this.uuid)) && (remoteAddr == this.remoteAddr || this.remoteAddr.equals(remoteAddr));
	}

	public UUID getUuid() {
		return uuid;
	}
}
