/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh;

/**
 * A triangle is a primitive shape that is having no atomic weight in VR. It
 * represents the smallest informations required to represent anything in VR.
 * 
 * @author Guest
 *
 */
public class Triangle {

	protected final int indiceA;
	protected final int indiceB;
	protected final int indiceC;

	public Triangle(int a, int b, int c) {
		this.indiceA = a;
		this.indiceB = b;
		this.indiceC = c;
		assert a != b && b != c && a != c : "Different vector-indexes required!";
	}

	public int getIndiceA() {
		return indiceA;
	}

	public int getIndiceB() {
		return indiceB;
	}

	public int getIndiceC() {
		return indiceC;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + indiceA + "," + indiceB + "," + indiceC + "]";
	}
}
