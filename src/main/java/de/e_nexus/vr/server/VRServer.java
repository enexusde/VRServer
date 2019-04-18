package de.e_nexus.vr.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import de.e_nexus.vr.server.codes.Client2ServerCode;
import de.e_nexus.vr.server.listeners.VRClientRequestAppInfo;
import de.e_nexus.vr.server.listeners.VRClientStatusListener;
import de.e_nexus.vr.server.listeners.VRExceptionListener;
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

	/**
	 * The status change listeners.
	 */
	private final Set<VRClientStatusListener> statusListeners = new LinkedHashSet<VRClientStatusListener>();
	/**
	 * The exception listeners.
	 */
	private final Set<VRExceptionListener> exceptionListeners = new LinkedHashSet<VRExceptionListener>();

	/**
	 * The info handlers.
	 */
	private final Set<VRClientRequestAppInfo> infoListeners = new LinkedHashSet<VRClientRequestAppInfo>();

	/**
	 * The list of meshes that must be send to the VR client and does not yet exists
	 * on the VR client yet.
	 */
	private final Set<Mesh> toSend = new LinkedHashSet<Mesh>();

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
	 * Adds an VR client status listener.
	 * 
	 * @param vrClientStatusListener The status listener.
	 */
	public void addVRClientStatusListener(VRClientStatusListener vrClientStatusListener) {
		statusListeners.add(vrClientStatusListener);
	}

	/**
	 * Adds a exception listener to the {@link VRServer}.
	 * 
	 * <p>
	 * The same instance of a listener can not be registred twice.
	 * 
	 * @param listener The exception listener.
	 */
	public void addVRExceptionListener(VRExceptionListener listener) {
		exceptionListeners.add(listener);
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
				Client2ServerCode code = Client2ServerCode.read(read);
				switch (code) {
				case GET_APP_INFO:
					StringBuilder sb = new StringBuilder();
					for (VRClientRequestAppInfo vrClientRequestAppInfo : infoListeners) {
						sb.append(vrClientRequestAppInfo.getLatin1Title());
					}
					outLenString(out, sb.toString());
					break;

				case GET_INCOMING_MESH:
					int count = Math.min(toSend.size(), 100);
					out.write(count);
					for (int i = 0; i < count; i++) {
						Iterator<Mesh> iterator = toSend.iterator();
						Mesh mesh = iterator.next();
						iterator.remove();
						ByteArrayOutputStream buff = new ByteArrayOutputStream();
						MeshOutputStream mos = new MeshOutputStream(buff);
						mos.writeMesh(mesh);
						mos.flush();
						//dumpToConsole(buff);
						outLenString(out, buff.size() + "");
						out.write(buff.toByteArray());
						MeshTexturesOutputStream tos = new MeshTexturesOutputStream(out);
						tos.writeTextures(mesh);
						tos.flush();
						out.flush();
						MeshTextureInfoInputStream mtis = new MeshTextureInfoInputStream(in);
						mtis.readTextureIndexes(mesh);
					}
					out.flush();
				default:
					break;
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
		out.write(length);
		out.write(b);
		out.flush();
	}

	private void notifyExceptionInCycle(Exception e) {
		boolean handled = false;
		for (VRExceptionListener vrExceptionListener : exceptionListeners) {
			try {
				vrExceptionListener.handle(e);
				handled = true;
			} catch (Exception ex) {
				ex.addSuppressed(e);
				ex.printStackTrace();
			}
		}

		if (!handled) {
			e.printStackTrace();
		}
	}

	public Thread getThread() {
		return T;
	}

	public void addInfoListener(VRClientRequestAppInfo l) {
		infoListeners.add(l);
	}

	public void addMesh(Mesh m) {
		toSend.add(m);
	}
}
