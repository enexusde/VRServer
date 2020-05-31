/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.codes;

public enum Client2ServerCode {
	/**
	 * The client requests from the server informations about the window-title and
	 * protocol version. Also an unique identifier is requested from the server to
	 * the client in order to store in the server what meshes and textures are
	 * already present in the client.
	 */
	CREATE_SESSION,

	/**
	 * The client requests from the server if there are meshes on the server that
	 * are required to display on the client.
	 */
	GET_INCOMING_MESH,
	/**
	 * The client send to the server the helmet and controller position info and
	 * buttons.
	 */
	SEND_HELMET_AND_CONTROLLER_INFO,

	/**
	 * The client requests from the server if there are meshes on the client that
	 * must be removed because they are removed on the server.
	 */
	GET_REMOVE_MESH,
	/**
	 * The client-pc recieved keyboard modifications. Either a release of a button
	 * or a press of a button or both. Not recieving this signal does not mean that
	 * no button is pressed but that the pressing or releasing has not changed
	 * meanwhile.
	 */
	SEND_KEYBOARD_CHANGES
}
