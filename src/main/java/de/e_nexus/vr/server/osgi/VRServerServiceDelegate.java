package de.e_nexus.vr.server.osgi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Service;

import de.e_nexus.vr.server.VRServer;
import de.e_nexus.vr.server.osgi.inter.VRServerService;

@Service
@ApplicationScoped
public class VRServerServiceDelegate {

	@Inject
	protected BundleContext bc = null;

	public VRServer getVRServer() {
		ServiceReference<VRServerService> ref = bc.getServiceReference(VRServerService.class);
		return (VRServer) bc.getService(ref);
	}
}
