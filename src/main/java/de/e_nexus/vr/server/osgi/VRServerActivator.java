package de.e_nexus.vr.server.osgi;

import java.util.Hashtable;
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

	private ServiceReference<VRServerService> vrServerReference;

	@Override
	public void start(BundleContext ctx) throws Exception {
		if (server != null) {
			throw new Exception("Server already bound. Last shutdown was not clean. This is a bug, please report it.");
		}
		server = new VRServer(8779, "Osgi VR-Server (Bundle " + ctx.getBundle().getBundleId() + ")");
		server.getListeners().addVRExceptionListener(new VRExceptionListener() {

			@Override
			public void handle(Throwable e) {

			}
		});
		server.start();
		LOG.info("Start Osgi spiced VR Server.");
		vrServerReplimentor = ctx.registerService(VRServerService.class, this, new Hashtable<String, String>());
		vrServerReference = vrServerReplimentor.getReference();
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		if (server == null) {
			throw new Exception("Could not stop VR Server. Stop not possible.");
		}
		if (server.isStopping()) {
			throw new Exception("Could not stop VR Server, shutdown already in progress.");
		}
		LOG.info("Unregister service and stop VR Server.");
		vrServerReplimentor.unregister();
		vrServerReplimentor = null;
		server.stop();
		vrServerReference = null;
		server = null;
	}

	@Override
	public VRServer getVRServer() {
		return server;
	}

}