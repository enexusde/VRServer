package de.e_nexus.vr.server.osgi;

import java.net.BindException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.e_nexus.vr.server.VRServer;
import de.e_nexus.vr.server.listeners.VRExceptionListener;
import de.e_nexus.vr.server.osgi.inter.VRServerService;

public class VRServerActivator implements BundleActivator, VRServerService {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(VRServerActivator.class.getCanonicalName());

	private VRServer server = null;

	private ServiceRegistration<VRServerService> vrServerReplimentor;

	@Override
	public void start(BundleContext ctx) throws Exception {
		System.out.print("Start Osgi spiced VR Server ...");
		if (server != null) {
			throw new Exception("Server already bound. Last shutdown was not clean. This is a bug, please report it.");
		}
		try {
			server = new VRServer(8779, "Osgi VR-Server (Bundle " + ctx.getBundle().getBundleId() + ")");

			server.getListeners().addVRExceptionListener(new VRExceptionListener() {

				@Override
				public void handle(Throwable e) {
					LOG.log(Level.FINE, e.getMessage(), e);
				}
			});
			server.start();

			vrServerReplimentor = ctx.registerService(VRServerService.class, this, new Hashtable<String, String>());
			System.out.println(" [OK]  @" + new SimpleDateFormat("HH:mm:ss").format(new Date())
					+ " (You might get asked by the firewall if you like to allow java to communicate to other systems. In order to connect the local VR-Client you are requested to grant the communication.)");
		} catch (BindException e) {
			System.out.println(" [FAILED]: " + e.getMessage());
		}
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		System.out.print("Stop VR Server ...");
		if (server == null) {
			throw new Exception("Could not stop VR Server. Stop not possible.");
		}
		if (server.isStopping()) {
			throw new Exception("Could not stop VR Server, shutdown already in progress.");
		}
		vrServerReplimentor.unregister();
		vrServerReplimentor = null;
		server.stop();
		server = null;
		System.out.println(" [OK] @" + new SimpleDateFormat("HH:mm").format(new Date()));
	}

	@Override
	public VRServer getVRServer() {
		return server;
	}
}
