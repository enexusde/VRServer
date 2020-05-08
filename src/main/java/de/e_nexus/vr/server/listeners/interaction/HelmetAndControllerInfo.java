/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.listeners.interaction;

import java.io.Serializable;

/**
 * State of the helmet and controller of a vr-set at one specific time.
 * 
 * <p>
 * To identify moving of helmet and/or controllers or even changing button
 * states requires two instances of this class at different times. If you have
 * two instances of the class and the values for an property differs then you
 * can be sure that there is an modificiation (moving, rotating or button) to
 * that representing physical action.
 * <p>
 * The bitmasks are per controller, usually there are two controllers.
 * <p>
 * All positions and rotations are relative to the playground center. This means
 * the rotation and location of the helmet does not influence the position and
 * rotation of the controllers.
 */
public final class HelmetAndControllerInfo implements Serializable {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -7544581628925460376L;

	/**
	 * The bitmask for the menu button on top of the controller. The smallest button
	 * so far, usually marked with a burger-symbol (three horizontal lines).
	 */
	public static final byte BTN_MENU = 0b00000001;

	/**
	 * The bitmask for the round button and the biggest one on top of the
	 * controller. You may call it the trackpad. It is not activated by a tender
	 * touch but by pressing down the trackpad at any position on the trackpad.
	 * 
	 * <p>
	 * Usually the pressing of the trackpad makes an gentle acoustic signal (like a
	 * clap with very, very small hands).
	 */
	public static final byte BTN_TRACK = 0b00000010;

	/**
	 * The bitmask for the default and most primitive button called "trigger",
	 * placed on the position similar to main button on drills.
	 * 
	 * <p>
	 * Even if there are shades between the not-pressing the trigger and clearly
	 * deep-pressing the trigger, this API does only differ between a clear press of
	 * the trigger as a "press" or an not clear pressing of the trigger as a
	 * "not-press".
	 */
	public static final byte BTN_TRIGGER = 0b00000100;
	/**
	 * The bitmask for the most hidden button. placed on downunder usually in touch
	 * with the the ring-finger.
	 * 
	 * <p>
	 * Usually there are two grip-buttons. One on the left side downunder the
	 * controller and one on the right side downunder the controller. Even if it
	 * might be technically possible to differ between those two buttons, this API
	 * is unable to transport this information.
	 * <p>
	 * Even if you only need to press one grip-button to notify the API about the
	 * pressing-down you also can press both buttons. Only pressing non of the
	 * grip-buttons reports the api about not pressing the grip-button at all.
	 */
	public static final byte BTN_GRIP = 0b00001000;

	private final float helmetX;
	private final float helmetY;
	private final float helmetZ;
	private final float helmetAngleX;
	private final float helmetAngleY;
	private final float helmetAngleZ;
	private final float leftHandX;
	private final float leftHandY;
	private final float leftHandZ;
	private final float leftHandRotX;
	private final float leftHandRotY;
	private final float leftHandRotZ;
	private final float rightHandX;
	private final float rightHandY;
	private final float rightHandZ;
	private final float rightHandRotX;
	private final float rightHandRotY;
	private final float rightHandRotZ;
	private final byte leftControllerStateOrd;
	private final byte rightControllerStateOrd;
	private final float leftTouchX;
	private final float leftTouchY;
	private final float rightTouchX;
	private final float rightTouchY;

	public HelmetAndControllerInfo(float helmetX, float helmetY, float helmetZ, float helmetAngleX, float helmetAngleY,
			float helmetAngleZ, float leftHandX, float leftHandY, float leftHandZ, float leftHandRotX,
			float leftHandRotY, float leftHandRotZ, float rightHandX, float rightHandY, float rightHandZ,
			float rightHandRotX, float rightHandRotY, float rightHandRotZ, byte leftControllerStateOrd,
			byte rightControllerStateOrd, float leftTouchX, float leftTouchY, float rightTouchX, float rightTouchY) {
		this.helmetX = helmetX;
		this.helmetY = helmetY;
		this.helmetZ = helmetZ;
		this.helmetAngleX = helmetAngleX;
		this.helmetAngleY = helmetAngleY;
		this.helmetAngleZ = helmetAngleZ;
		this.leftHandX = leftHandX;
		this.leftHandY = leftHandY;
		this.leftHandZ = leftHandZ;
		this.leftHandRotX = leftHandRotX;
		this.leftHandRotY = leftHandRotY;
		this.leftHandRotZ = leftHandRotZ;
		this.rightHandX = rightHandX;
		this.rightHandY = rightHandY;
		this.rightHandZ = rightHandZ;
		this.rightHandRotX = rightHandRotX;
		this.rightHandRotY = rightHandRotY;
		this.rightHandRotZ = rightHandRotZ;
		this.leftControllerStateOrd = leftControllerStateOrd;
		this.rightControllerStateOrd = rightControllerStateOrd;
		this.leftTouchX = leftTouchX;
		this.leftTouchY = leftTouchY;
		this.rightTouchX = rightTouchX;
		this.rightTouchY = rightTouchY;

	}

	public float getHelmetX() {
		return helmetX;
	}

	public float getHelmetY() {
		return helmetY;
	}

	public float getHelmetZ() {
		return helmetZ;
	}

	public float getHelmetAngleX() {
		return helmetAngleX;
	}

	public float getHelmetAngleY() {
		return helmetAngleY;
	}

	public float getHelmetAngleZ() {
		return helmetAngleZ;
	}

	/**
	 * The x-position of the left hand, increasing values means the controller is
	 * moving to the right (if the user looks straight forward). If the controller
	 * is not active the value <code>0.0f</code> is returned.
	 * <table>
	 * <caption>Interpretation of values</caption> 
	 * <tr>
	 * <th>Value</th>
	 * <th>Interpretation</th>
	 * </tr>
	 * <tr>
	 * <td>-1.00</td>
	 * <td>Left Controller is sprawled to left side</td>
	 * </tr>
	 * <tr>
	 * <td>1.00</td>
	 * <td>Left Controller is sprawled to right side</td>
	 * </tr>
	 * </table>
	 * 
	 * @return Value from -1.0 to 1.0. 
	 */
	public float getLeftHandX() {
		return leftHandX;
	}

	public float getLeftHandY() {
		return leftHandY;
	}

	public float getLeftHandZ() {
		return leftHandZ;
	}

	public float getLeftHandRotX() {
		return leftHandRotX;
	}

	public float getLeftHandRotY() {
		return leftHandRotY;
	}

	public float getLeftHandRotZ() {
		return leftHandRotZ;
	}

	public float getRightHandX() {
		return rightHandX;
	}

	public float getRightHandY() {
		return rightHandY;
	}

	public float getRightHandZ() {
		return rightHandZ;
	}

	public float getRightHandRotX() {
		return rightHandRotX;
	}

	public float getRightHandRotY() {
		return rightHandRotY;
	}

	public float getRightHandRotZ() {
		return rightHandRotZ;
	}

	public boolean isLeftTrigger() {
		return isPressedLeft(BTN_TRIGGER);
	}

	public boolean isRightTrigger() {
		return isPressedRight(BTN_TRIGGER);
	}

	public boolean isLeftMenu() {
		return isPressedLeft(BTN_MENU);
	}

	public boolean isRightMenu() {
		return isPressedRight(BTN_MENU);
	}

	public boolean isLeftControllerGrip() {
		return isPressedLeft(BTN_GRIP);
	}

	public boolean isRightControllerGrip() {
		return isPressedRight(BTN_GRIP);
	}

	public boolean isLeftTrack() {
		return isPressedLeft(BTN_TRACK);
	}

	public boolean isRightTrack() {
		return isPressedRight(BTN_TRACK);
	}

	public boolean isPressedLeft(byte bitmask) {
		return isPressed(bitmask, getLeftControllerStateOrd());
	}

	public boolean isPressedRight(byte bitmask) {
		return isPressed(bitmask, getRightControllerStateOrd());
	}

	public boolean isPressed(byte bitmask, byte ord) {
		return (bitmask & (1 << ord)) == (1 << ord);
	}

	public byte getLeftControllerStateOrd() {
		return leftControllerStateOrd;
	}

	public byte getRightControllerStateOrd() {
		return rightControllerStateOrd;
	}

	public float getLeftTouchX() {
		return leftTouchX;
	}

	public float getLeftTouchY() {
		return leftTouchY;
	}

	public float getRightTouchX() {
		return rightTouchX;
	}

	public float getRightTouchY() {
		return rightTouchY;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("HMD[pos=");
		stringBuilder.append((helmetX));
		stringBuilder.append("x");
		stringBuilder.append((helmetY));
		stringBuilder.append("x");
		stringBuilder.append((helmetZ));
		stringBuilder.append(",rot=");
		stringBuilder.append((helmetAngleX));
		stringBuilder.append("x");
		stringBuilder.append((helmetAngleY));
		stringBuilder.append("x");
		stringBuilder.append((helmetAngleZ));
		stringBuilder.append(" \tleft=");
		stringBuilder.append((leftHandX));
		stringBuilder.append("x");
		stringBuilder.append((leftHandY));
		stringBuilder.append("x");
		stringBuilder.append((leftHandZ));
		stringBuilder.append(",rot=");
		stringBuilder.append((leftHandRotX));
		stringBuilder.append("x");
		stringBuilder.append((leftHandRotY));
		stringBuilder.append("x");
		stringBuilder.append((leftHandRotZ));
		stringBuilder.append(" \tright=");
		stringBuilder.append((rightHandX));
		stringBuilder.append("x");
		stringBuilder.append((rightHandY));
		stringBuilder.append("x");
		stringBuilder.append((rightHandZ));
		stringBuilder.append(",rot=");
		stringBuilder.append((rightHandRotX));
		stringBuilder.append("x");
		stringBuilder.append((rightHandRotY));
		stringBuilder.append("x");
		stringBuilder.append((rightHandRotZ));
		stringBuilder.append(", \tbtnLeft=");
		stringBuilder.append(leftControllerStateOrd);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
