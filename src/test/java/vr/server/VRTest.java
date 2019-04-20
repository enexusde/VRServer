package vr.server;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

import de.e_nexus.vr.server.VRServer;
import de.e_nexus.vr.server.listeners.VRClientRequestAppInfo;
import de.e_nexus.vr.server.listeners.VRClientStatusListener;
import de.e_nexus.vr.server.listeners.VRExceptionListener;
import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.Triangle;
import de.e_nexus.vr.server.mesh.UVVector;
import de.e_nexus.vr.server.mesh.tex.Texture;
import de.e_nexus.vr.server.mesh.tex.TextureStage;
import de.e_nexus.vr.server.mesh.tex.URLTexture;
import de.e_nexus.vr.server.util.NumberTools;

public class VRTest {
	public void testVR() throws IOException, InterruptedException {

		VRServer r = new VRServer();
		r.addVRClientStatusListener(new VRClientStatusListener() {
			public void notifyStatus(boolean connected) {
				if (connected)
					System.out.println("Connected.");
				else
					System.out.println("Disconnected.");
			}
		});
		r.addVRExceptionListener(new VRExceptionListener() {
			public void handle(Exception e) {
				if (e instanceof SocketTimeoutException) {
					// ignore this, if no client, no connection.
				} else
					e.printStackTrace();
			}
		});
		r.addInfoListener(new VRClientRequestAppInfo() {
			public String getLatin1Title() {
				return "Test App";
			}
		});
		URLTexture grid = new URLTexture(new File("src/test/resources/grid.png").toURL());
		Mesh<UVVector> mesh = new Mesh<UVVector>();
		mesh.setTexture(TextureStage.DIFFUSE, grid);
		for (float x = -20; x < 20; x++) {
			for (float y = -20; y < 20; y++) {
				mesh.addCube(x, 1f, y, 0.5f, 0.2f);
			}
		}
		r.addMesh(mesh);
		r.start();
		Thread.sleep(30000);
		r.stop();

	}
}
