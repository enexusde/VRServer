/*  _    ______   _____ Copyright GPL by Peter Rader 2019                          
 * | |  / / __ \ / ___/___  ______   _____  _____
 * | | / / /_/ / \__ \/ _ \/ ___/ | / / _ \/ ___/
 * | |/ / _, _/ ___/ /  __/ /   | |/ /  __/ /    
 * |___/_/ |_| /____/\___/_/    |___/\___/_/     
 */
package de.e_nexus.vr.server.mesh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

public class MeshOutputStream<T extends Vector> extends LittleEndianOutputStream {

	private static final byte MUST_NORMALIZE_FLAG = (byte) 1;
	private static final byte TWO_COMPONENTS = (byte) 2;
	private static final byte THREE_COMPONENTS = (byte) 3;
	private static final byte TYPE_FLOAT = (byte) 0;
	private final static Logger LOG = Logger.getLogger(MeshOutputStream.class.getCanonicalName());

	public MeshOutputStream(OutputStream out) {
		super(out);
	}

	protected static final byte FLOAT = 0;
	protected static final byte UINT = 1;
	protected static final byte UINT_SIZE = 4;

	public void writeMesh(Mesh<T> m) throws IOException {
		boolean isUv = !m.vectors.isEmpty() && m.vectors.iterator().next() instanceof UVVector;
		boolean allHaveNormals = true;
		for (Object v : m.vectors) {
			if (v instanceof NormalVector) {
				NormalVector normalVector = (NormalVector) v;
				if (normalVector.normalX != null && normalVector.normalY != null && normalVector.normalZ != null) {
					continue;
				}
			}
			allHaveNormals = false;
			break;
		}
		write1_CountVertices(m);
		write2_CountIndices(m);
		write3_NumberOfAttributes(m, allHaveNormals);
		write4_sizeOfSingleVertexInBytes(m, isUv, allHaveNormals);
		write5_offsetRawVertexData(m, isUv, allHaveNormals);
		write6_offsetIndexData(m, isUv, allHaveNormals);
		writeVertexAttributeData(m, isUv, allHaveNormals);
		writeRawVertexData(m, isUv, allHaveNormals);
		writeIndexData(m);
	}

	private void writeRawVertexData(Mesh<T> mesh, boolean isUv, boolean allHaveNormals) throws IOException {
		List<T> vectors = mesh.vectors;
		for (T t : vectors) {
			LOG.fine("Write a vector of mesh " + mesh + ".");
			LOG.finest("Writing vector " + t.x + "x" + t.y + "x" + t.z + ".");
			writeLittleEndian(t.x);
			writeLittleEndian(t.y);
			writeLittleEndian(t.z);
			if (allHaveNormals) {
				NormalVector nv = (NormalVector) t;
				writeLittleEndian(nv.normalX);
				writeLittleEndian(nv.normalY);
				writeLittleEndian(nv.normalZ);

			}

			if (isUv) {
				UVVector e = (UVVector) t;
				LOG.finest("Write a texture coordinte " + e.uvX + "x" + e.uvY
						+ " (factor from left border of image 'x' factor from upper border of image) of mesh " + mesh + ".");
				writeLittleEndian(e.uvX);
				writeLittleEndian(e.uvY);
			} else {
				LOG.fine("No texture inforemations available for this vector.");
			}
		}
	}

	private void writeIndexData(Mesh<T> m) throws IOException {
		for (Triangle t : m.polygons) {
			writeLittleEndian(t.indiceA);
			writeLittleEndian(t.indiceB);
			writeLittleEndian(t.indiceC);
		}
	}

	private void writeVertexAttributeData(Mesh m, boolean isUv, boolean allHaveNormals) throws IOException {

		attr(TYPE_FLOAT, THREE_COMPONENTS, MUST_NORMALIZE_FLAG, "posi" + "tion" + (char) 0 + (char) 0 + (char) 0 + (char) 0);
		if (allHaveNormals) {
			attr(TYPE_FLOAT, THREE_COMPONENTS, MUST_NORMALIZE_FLAG, "norm" + "als" + (char) 0);
		}
		if (isUv) {
			attr(TYPE_FLOAT, TWO_COMPONENTS, MUST_NORMALIZE_FLAG, "uv" + (char) 0 + (char) 0);
		}
	}

	private void attr(byte oneByteDataType, byte oneByteComponentCount, byte oneByteNormalizeFlag, String txt) throws IOException {
		write(oneByteDataType);
		write(oneByteComponentCount);
		write(oneByteNormalizeFlag);
		write(txt.length());
		write(txt.getBytes());
	}

	private void write6_offsetIndexData(Mesh<T> m, boolean isUv, boolean allHaveNormals) throws IOException {
		int vectorPayloadSize = m.vectors.size() * calculateSingleVertexSize(isUv, allHaveNormals);
		writeLittleEndian(calculateRawVertexDataOffset(isUv) + vectorPayloadSize);// ??
	}

	private void write5_offsetRawVertexData(Mesh m, boolean isUv, boolean allHaveNormals) throws IOException {
		int calculateRawVertexDataOffset = calculateRawVertexDataOffset(isUv);
		writeLittleEndian(calculateRawVertexDataOffset);
	}

	private int calculateRawVertexDataOffset(boolean isUv) {
		int offsetAttributeData = 24;
		int posAttrSize = 3 + 8 + 1 + 4;
		int uvAttrSize = 3 + 1 + 4;
		int rawVertexDataOffset = offsetAttributeData + posAttrSize;
		if (isUv) {
			rawVertexDataOffset += uvAttrSize;
		}
		LOG.finer("Calculated offset of raw vertex data: " + rawVertexDataOffset);
		return rawVertexDataOffset;
	}

	private void write4_sizeOfSingleVertexInBytes(Mesh m, boolean isUv, boolean allHaveNormals) throws IOException {
		writeLittleEndian(calculateSingleVertexSize(isUv, allHaveNormals));
	}

	private int calculateSingleVertexSize(boolean isUv, boolean allHaveNormals) {
		int positionSize = 3 * 4;
		int uvSize = 2 * 4;
		int normalsSize = 3 * 4;
		return positionSize + (isUv ? uvSize : 0) + (allHaveNormals ? normalsSize : 0);
	}

	private void write3_NumberOfAttributes(Mesh m, boolean allHaveNormals) throws IOException {
		writeLittleEndian(allHaveNormals ? 3 : 2);

	}

	private void write2_CountIndices(Mesh m) throws IOException {
		writeLittleEndian(m.polygons.size() * 3);
	}

	private void write1_CountVertices(Mesh m) throws IOException {
		writeLittleEndian(m.vectors.size());
	}
}
