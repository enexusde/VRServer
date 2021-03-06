/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package vr.server;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import de.e_nexus.vr.server.VRServer;
import de.e_nexus.vr.server.listeners.VRClientRequestAppInfo;
import de.e_nexus.vr.server.listeners.VRClientStatusListener;
import de.e_nexus.vr.server.listeners.VRExceptionListener;
import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.UVVector;
import de.e_nexus.vr.server.mesh.tex.BufferedImageTexture;
import de.e_nexus.vr.server.mesh.tex.TextureStage;
import de.e_nexus.vr.server.mesh.tex.URLTexture;
import de.e_nexus.vr.server.util.TextureTools;

public class VRTest {
	public void no_testGrid() throws IOException, InterruptedException {
		System.out.println("Test grid");
		VRServer r = new VRServer();
		r.getListeners().addVRClientStatusListener(new VRClientStatusListener() {
			public void notifyStatus(boolean connected) {
				if (connected)
					System.out.println("Connected.");
				else
					System.out.println("Disconnected.");
			}
		});
		r.getListeners().addVRExceptionListener(new VRExceptionListener() {
			public void handle(Throwable e) {
				if (e instanceof SocketTimeoutException) {
					// ignore this, if no client, no connection.
				} else
					e.printStackTrace();
			}
		});
		r.getListeners().addInfoListener(new VRClientRequestAppInfo() {
			public String getLatin1Title() {
				return "Test App";
			}
		});
		URLTexture grid = new URLTexture(new File("src/test/resources/alphablend10percentopaque.png").toURL());
		Mesh<UVVector> verticalLines = new Mesh<UVVector>();
		verticalLines.setTexture(TextureStage.DIFFUSE, grid);
		int q = 1;
		for (float right = -q; right < q; right++) {
			for (float front = -q; front < q; front++) {
				verticalLines.addCube(right, 0f, front, 0.001f, 20f);
			}
		}
		double deg90rad = Math.toRadians(90);
		r.addMesh(verticalLines);
		// r.addMesh(verticalLines.cloneRotateHorizontal(new Vector(0, 0, 0),
		// deg90rad));
		// r.addMesh(verticalLines.cloneRotateClockwise(new Vector(0, 0, 0), deg90rad));

		r.start();
		Thread.sleep(30000);
		r.stop();
	}

	public void testBoxesNormalsAndTexture() throws IOException, InterruptedException {
		System.out.println("Test Boxes");
		VRServer r = new VRServer();
		r.getListeners().addVRClientStatusListener(new VRClientStatusListener() {
			public void notifyStatus(boolean connected) {
				if (connected)
					System.out.println("Connected.");
				else
					System.out.println("Disconnected.");
			}
		});
		r.getListeners().addVRExceptionListener(new VRExceptionListener() {
			public void handle(Throwable e) {
				if (e instanceof SocketTimeoutException) {
					// ignore this, if no client, no connection.
				} else
					e.printStackTrace();
			}
		});
		r.getListeners().addInfoListener(new VRClientRequestAppInfo() {
			public String getLatin1Title() {
				return "Test App";
			}
		});
		float hf = 100f;
		int i = 0;
		for (File file : new File(".").listFiles()) {
			i++;
			BufferedImageTexture texture = TextureTools.fromText(file.getAbsolutePath(), Color.black, Color.white, null);
			if (texture != null) {
				Mesh<UVVector> mesh = new Mesh<UVVector>();
				mesh.setTexture(TextureStage.DIFFUSE, texture);

				UVVector a = new UVVector(texture.getWidth() / hf, 0, -1, 0, 1, null, null, null);
				UVVector b = new UVVector(texture.getWidth() / hf, texture.getHeight() / hf, -1, 0, 0, null, null, null);
				UVVector c = new UVVector(0, texture.getHeight() / hf, -1, 1, 0, null, null, null);
				UVVector d = new UVVector(0, 0, -1, 1, 1, null, null, null);
				int addVector = mesh.addVector(a.cloneMoved(i, i, i));
				mesh.addSquareClockwise(addVector, mesh.addVector(b.cloneMoved(i, i, i)), mesh.addVector(c.cloneMoved(i, i, i)),
						mesh.addVector(d.cloneMoved(i, i, i)));
				r.addMesh(mesh);
			}

		}

		URLTexture grid = new URLTexture(new File("src/test/resources/grid.png").toURL());
		Mesh<UVVector> mesh = new Mesh<UVVector>();
		mesh.setTexture(TextureStage.DIFFUSE, grid);
		int q = 1;
		for (float x = -q; x < q; x++) {
			for (float y = -q; y < q; y++) {
				mesh.addCube(x, 1, y, 0.5f, 0.2f);
			}
		}
		r.addMesh(mesh);
		r.start();
		Thread.sleep(30000);
		r.stop();

	}
}
