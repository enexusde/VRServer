/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server;

import java.util.LinkedHashSet;
import java.util.Set;

import de.e_nexus.vr.server.listeners.VRClientRequestAppInfo;
import de.e_nexus.vr.server.listeners.VRClientStatusListener;
import de.e_nexus.vr.server.listeners.VRExceptionListener;
import de.e_nexus.vr.server.listeners.interaction.HelmetAndControllerInfo;

public class VRServerListeners {
	/**
	 * The status change listeners.
	 */
	private final Set<VRClientKeyboardListener> keyboardListeners = new LinkedHashSet<VRClientKeyboardListener>();
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
	 * The helmet and controller info.
	 */
	private final Set<VRClientHelmetAndControllerListener> interactionListeners = new LinkedHashSet<VRClientHelmetAndControllerListener>();

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

	public void addInteractionListener(VRClientHelmetAndControllerListener interactionListener) {
		this.interactionListeners.add(interactionListener);
	}

	public void addInfoListener(VRClientRequestAppInfo l) {
		infoListeners.add(l);
	}

	public void getTitle(StringBuilder sb) {
		for (VRClientRequestAppInfo vrClientRequestAppInfo : infoListeners) {
			sb.append(vrClientRequestAppInfo.getLatin1Title());
		}
	}

	public void notifyConnected(boolean connected) {
		for (VRClientStatusListener vrClientStatusListener : statusListeners) {
			vrClientStatusListener.notifyStatus(connected);
		}
	}

	public void notifyInteraction(HelmetAndControllerInfo haci) {
		for (VRClientHelmetAndControllerListener il : interactionListeners) {
			il.notify(haci);
		}
	}

	public void addKeyboardListener(VRClientKeyboardListener listener) {
		keyboardListeners.add(listener);
	}

	public void notifyKeyboardChange(byte[] down, byte[] up, long incommingTimeMilis) {
		ClientKeyboardScancode[] downCodes = new ClientKeyboardScancode[down.length];
		ClientKeyboardScancode[] upCodes = new ClientKeyboardScancode[up.length];
		for (int i = 0; i < down.length; i++) {
			downCodes[i] = ClientKeyboardScancode.values()[down[i]];
		}
		for (int i = 0; i < up.length; i++) {
			upCodes[i] = ClientKeyboardScancode.values()[up[i]];
		}

		for (VRClientKeyboardListener keyboardListener : keyboardListeners) {
			keyboardListener.notifyKeyboardEvent(downCodes, upCodes, incommingTimeMilis);
		}
	}

	public Set<VRClientKeyboardListener> getKeyboardListeners() {
		return keyboardListeners;
	}

	public boolean handle(Exception e) {
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
		return handled;
	}
}
