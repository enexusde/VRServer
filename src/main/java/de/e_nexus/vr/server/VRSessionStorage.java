/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server;

import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import de.e_nexus.vr.server.mesh.Mesh;

public class VRSessionStorage extends Vector<VRSession> {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -7839143919320148454L;

	private final Set<UUID> usedUUIDs = new LinkedHashSet<>(1);

	public UUID newUUID() {
		synchronized (this) {
			while (true) {
				UUID candidate = UUID.randomUUID();
				if (!usedUUIDs.contains(candidate)) {
					UUID uniqueUUID = candidate;
					usedUUIDs.add(uniqueUUID);
					return uniqueUUID;
				}
			}
		}
	}

	@Override
	public boolean add(VRSession newSession) {
		for (Mesh mesh : meshesAllExistingMeshesKnow) {
			newSession.markAddMesh(mesh);
		}
		return super.add(newSession);
	}

	public VRSession getByIpAndSession(UUID uid, InetSocketAddress ip) {
		for (VRSession s : this) {
			if (s.isThisSession(ip.getAddress(), uid)) {
				return s;
			}
		}
		return null;
	}

	private final Set<Mesh> meshesAllExistingMeshesKnow = new LinkedHashSet<Mesh>(0);

	public void addPublishMeshToNewSessions(Mesh meshToAdd) {
		meshesAllExistingMeshesKnow.add(meshToAdd);
	}

	public void removePublishMeshToNewSessions(Mesh meshToRemove) {
		meshesAllExistingMeshesKnow.remove(meshToRemove);
	}
}
