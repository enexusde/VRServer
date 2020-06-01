package de.e_nexus.vr.server;

/**
 * Listener for changes made by the user to the keyboard of a connected
 * VR-Session.
 */
public interface VRClientKeyboardListener {

	/**
	 * Notifies about changed button states of the client's system-keyboard. What
	 * key or what character is exposed by the scancode can be read here: <a href=
	 * "http://www.appgamekit.com/documentation/guides/scancodes.htm">Scancodes.</a>
	 * 
	 * <p>
	 * Be aware that no changes will not produce a change-event. This means you will
	 * never have both arrays empty.
	 * <p>
	 * For a better performance all notifications are in one extra thread.
	 * 
	 * <p>
	 * <b>You are not allowed to modify the array! It will lead to unexpected
	 * behaviour!</b>
	 * 
	 * @param down          The {@link ClientKeyboardScancode keys} that are hit on
	 *                      the keyboard, never <code>null</code>.
	 * @param up            The {@link ClientKeyboardScancode keys} that are
	 *                      released on the keyboard, never <code>null</code>.
	 * @param incommingTime The time the modification has reached the server. The
	 *                      order is guaraneed to be ordered correctly. However
	 *                      there might be the problem that having FTL systems two
	 *                      event may have the same incomming time, in this
	 *                      situation please open a bug-report.
	 */
	public void notifyKeyboardEvent(ClientKeyboardScancode[] down, ClientKeyboardScancode[] up, long incommingTime);
}
