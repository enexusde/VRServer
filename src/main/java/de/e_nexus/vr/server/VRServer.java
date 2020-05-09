/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.rmi.ConnectIOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import de.e_nexus.vr.server.codes.Client2ServerCode;
import de.e_nexus.vr.server.listeners.interaction.HelmetAndControllerInfo;
import de.e_nexus.vr.server.mesh.Mesh;
import de.e_nexus.vr.server.mesh.MeshOutputStream;
import de.e_nexus.vr.server.mesh.tex.MeshTextureInfoInputStream;
import de.e_nexus.vr.server.mesh.tex.MeshTexturesOutputStream;
import de.e_nexus.vr.server.util.NumberTools;

/**
 * The server to accept incomming VR client requests.
 * 
 */
public class VRServer extends ServerSocket {

	/**
	 * The logger for this class.
	 */
	private final static Logger LOG = Logger.getLogger(VRServer.class.getCanonicalName());

	/**
	 * The default transportation charset.
	 */
	private static final Charset LATIN1;
	static {
		LATIN1 = Charset.forName("latin1");
	}
	private VRServerListeners listeners = new VRServerListeners();

	/**
	 * The list of meshes that must be send to the VR client and does not yet exists
	 * on the VR client yet.
	 */
	private final Set<Mesh> toSend = new LinkedHashSet<Mesh>();

	/**
	 * The list of session-storages.
	 */
	private final VRSessionStorage sessionStorage = new VRSessionStorage();

	/**
	 * The worker thread to accept requests.
	 */
	private class Worker extends Thread {
		private boolean running = true;

		public void run() {
			try {
				setSoTimeout(50);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while (running) {
				cycle();
			}
			T = null;
		};

	}

	private Worker T;

	/**
	 * The default constructor.
	 * 
	 * @throws IOException If and only if the port is already in use.
	 */
	public VRServer() throws IOException {
		super(8779);
	}

	/**
	 * Starts the {@link VRServer} from accepting incomming connections from the VR
	 * client.
	 */
	public void start() {
		if (T == null) {
			T = new Worker();
			T.start();
		}
	}

	/**
	 * Stops any worker from accepting command from the VR client.
	 * <p>
	 * The client will not be terminated. If any new request are send from the VR
	 * client and the {@link VRServer} has stoped, the client will terminate with an
	 * message.
	 * <p>
	 * Working connections are not be canceled and are proceesed until the client
	 * terminates the connection.
	 */
	public void stop() {
		T.running = false;
	}

	/**
	 * Returns wherever there is any thread who is accepting new incomming request
	 * from the VR client.
	 * <p>
	 * Notice that any running connection will <b>not</b> be terminated if the
	 * {@link VRServer} is stopping or has been stopped.
	 * 
	 * @return <code>true</code> if new connections are handled, <code>false</code>
	 *         otherwise.
	 */
	public boolean isStopping() {
		return T != null && !T.running;
	}

	/**
	 * Cycles the connection service.
	 */
	protected void cycle() {
		try {
			Socket s = accept();
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();
			int read = in.read();
			if (read == -1) {
				// log("Closed unexpectedly " + in.available());
			} else {
				Client2ServerCode code = Client2ServerCode.values()[read];
				switch (code) {
				case CREATE_SESSION:
					StringBuilder sb = new StringBuilder();
					listeners.getTitle(sb);
					outLenString(out, sb.toString());

					InetSocketAddress remoteSocketAddress = (InetSocketAddress) s.getRemoteSocketAddress();
					VRSession session = VRSession.registerNewSession(remoteSocketAddress.getAddress(), getSessionStorage());
					outLenString(out, session.getUuid().toString());
					listeners.notifyConnected(true);
					break;

				case SEND_HELMET_AND_CONTROLLER_INFO: {
					float helmetX = NumberTools.readByteArrayBigEndianFloat(in);
					float helmetY = NumberTools.readByteArrayBigEndianFloat(in);
					float helmetZ = NumberTools.readByteArrayBigEndianFloat(in);
					float helmetAngleX = NumberTools.readByteArrayBigEndianFloat(in);
					float helmetAngleY = NumberTools.readByteArrayBigEndianFloat(in);
					float helmetAngleZ = NumberTools.readByteArrayBigEndianFloat(in);
					float lhX = NumberTools.readByteArrayBigEndianFloat(in);
					float lhY = NumberTools.readByteArrayBigEndianFloat(in);
					float lhZ = NumberTools.readByteArrayBigEndianFloat(in);
					float lhrX = NumberTools.readByteArrayBigEndianFloat(in);
					float lhrY = NumberTools.readByteArrayBigEndianFloat(in);
					float lhrZ = NumberTools.readByteArrayBigEndianFloat(in);

					float rhX = NumberTools.readByteArrayBigEndianFloat(in);
					float rhY = NumberTools.readByteArrayBigEndianFloat(in);
					float rhZ = NumberTools.readByteArrayBigEndianFloat(in);
					float rhrX = NumberTools.readByteArrayBigEndianFloat(in);
					float rhrY = NumberTools.readByteArrayBigEndianFloat(in);
					float rhrZ = NumberTools.readByteArrayBigEndianFloat(in);
					int lcs = in.read();
					int rcs = in.read();
					float ltx = NumberTools.readByteArrayBigEndianFloat(in);
					float lty = NumberTools.readByteArrayBigEndianFloat(in);
					float rtx = NumberTools.readByteArrayBigEndianFloat(in);
					float rty = NumberTools.readByteArrayBigEndianFloat(in);
					HelmetAndControllerInfo haci = new HelmetAndControllerInfo(helmetX, helmetY, helmetZ, helmetAngleX, helmetAngleY, helmetAngleZ, lhX, lhY,
							lhZ, lhrX, lhrY, lhrZ, rhX, rhY, rhZ, rhrX, rhrY, rhrZ, (byte) lcs, (byte) rcs, ltx, lty, rtx, rty);
					listeners.notifyInteraction(haci);
					break;
				}
				case GET_INCOMING_MESH: {
					int uuidsize = in.read();
					String possibleSessionId = "";
					for (int i = 0; i < uuidsize; i++) {
						int c = in.read();
						if (c == -1) {
							throw new ConnectIOException("Stream closed while reading the length of uuid.");
						}
						char cr = (char) c;
						possibleSessionId += cr;
					}

					UUID designatedUUID = UUID.fromString(possibleSessionId);
					InetSocketAddress remoteSockAddr = (InetSocketAddress) s.getRemoteSocketAddress();
					VRSession vrSession = sessionStorage.getByIpAndSession(designatedUUID, remoteSockAddr);
					int count = Math.min(toSend.size(), 100);
					out.write(count);
					out.flush();
					Iterator<Mesh> iterator = toSend.iterator();
					for (int i = 0; i < count; i++) {
						Mesh mesh = iterator.next();
						iterator.remove();
						ByteArrayOutputStream buff = new ByteArrayOutputStream();
						MeshOutputStream mos = new MeshOutputStream(buff);
						mos.writeMesh(mesh);
						mos.flush();
						// dumpToConsole(buff);
						outLenString(out, buff.size() + "");
						out.write(buff.toByteArray());
						out.flush();
						MeshTexturesOutputStream tos = new MeshTexturesOutputStream(out);
						tos.writeTextures(mesh);
						tos.flush();
						MeshTextureInfoInputStream mtis = new MeshTextureInfoInputStream(in);
						mtis.readTextureIndexes(mesh, vrSession);
					}
					out.flush();
					break;
				}
				}
			}
			s.close();
		} catch (Exception e) {
			notifyExceptionInCycle(e);
		}
	}

	/**
	 * 
	 * @param buff
	 */
	private void dumpToConsole(ByteArrayOutputStream buff) {
		byte[] f = buff.toByteArray();
		NumberTools.printbytes(f);
	}

	private void outLenString(OutputStream out, String string) throws IOException {
		byte[] b = string.getBytes(LATIN1);
		int length = b.length;
		try {
			out.write(length);
		} catch (SocketException e) {
			throw new IOException("Failed to send the length of bytes required to store the string '" + string + "'!", e);
		}
		out.write(b);
		out.flush();
	}

	private void notifyExceptionInCycle(Exception e) {
		boolean handled = listeners.handle(e);
		if (!handled) {
			e.printStackTrace();
		}
	}

	public Thread getThread() {
		return T;
	}

	public VRServerListeners getListeners() {
		return listeners;
	}

	public void addMesh(Mesh m) {
		toSend.add(m);
	}

	/**
	 * Returns the session-storages.
	 * <p>
	 * The session-storage stores VRSessions. A VRSession holds informations about
	 * what VR-client knows what meshes and what textures.
	 * 
	 * @return The session-storage, never <code>null</code>.
	 */
	public VRSessionStorage getSessionStorage() {
		return sessionStorage;
	}
}
