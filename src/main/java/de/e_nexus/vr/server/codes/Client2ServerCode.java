package de.e_nexus.vr.server.codes;

public enum Client2ServerCode {
	GET_APP_INFO, GET_INCOMING_MESH;

	public static Client2ServerCode read(int read) {
		for (Client2ServerCode c : values()) {
			if (c.ordinal() == read) {
				return c;
			}
		}
		return null;
	}
}
