/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.Vector;
import de.e_nexus.vr.server.mesh.tex.Texture;

/**
 * Represents a client session in the server. Stores what meshes are already
 * known by the client and must not be transfered to the client.
 */
public final class VRSession implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4661234677575908030L;
	private final InetAddress remoteAddr;
	private final Map<Integer, Mesh> clientMeshIds = new LinkedHashMap<>(0);
	private final Set<Integer> clientMeshIdsToRemove = new LinkedHashSet<>(clientMeshIds.size());
	private final Map<Integer, Texture> clientTextureIds = new LinkedHashMap<>(0);
	private UUID uuid;

	private VRSession(InetAddress remoteAddr, UUID uuid) {
		this.remoteAddr = remoteAddr;
		this.uuid = uuid;
	}

	public void registerMesh(int clientMeshId, Mesh<?> mesh) {
		clientMeshIds.put(clientMeshId, mesh);
	}

	public boolean hasMesh(Mesh mesh) {
		return clientMeshIds.containsValue(mesh);
	}

	public void registerTexture(int clientTextureId, Texture texture) {
		clientTextureIds.put(clientTextureId, texture);
	}

	public boolean hasTexture(Texture texture) {
		return clientTextureIds.containsValue(texture);
	}

	public Integer getMeshId(Mesh mesh) {
		for (Entry<Integer, Mesh> entry : clientMeshIds.entrySet()) {
			if (entry.getValue() == mesh) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Integer getTexureId(Texture texture) {
		for (Entry<Integer, Texture> entry : clientTextureIds.entrySet()) {
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

	public Set<Integer> removeMeshesMarkedForRemoval() {
		synchronized (clientMeshIds) {
			synchronized (clientMeshIdsToRemove) {
				Set<Integer> unmodifiableSet = new LinkedHashSet<Integer>(clientMeshIdsToRemove);
				Iterator<Integer> removeIdsIterator = clientMeshIdsToRemove.iterator();
				while (removeIdsIterator.hasNext()) {
					Integer toRemoveId = (Integer) removeIdsIterator.next();
					clientMeshIds.remove(toRemoveId);
				}
				clientMeshIdsToRemove.clear();
				return Collections.unmodifiableSet(unmodifiableSet);
			}
		}
	}

	public void markRemoveMesh(Mesh<Vector> meshToRemoveFromClient) {
		synchronized (clientMeshIdsToRemove) {
			for (Integer clientMeshId : clientMeshIds.keySet()) {
				if (clientMeshIds.get(clientMeshId) == meshToRemoveFromClient) {
					clientMeshIdsToRemove.add(clientMeshId);
				}
			}
		}
	}
}
