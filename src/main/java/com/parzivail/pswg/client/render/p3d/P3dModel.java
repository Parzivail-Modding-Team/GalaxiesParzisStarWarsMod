package com.parzivail.pswg.client.render.p3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.client.model.ModelUtil;
import com.parzivail.util.data.DataReader;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class P3dModel
{
	@FunctionalInterface
	public interface PartTransformer<T>
	{
		Matrix4f transform(T target, String objectName, float tickDelta);
	}

	@FunctionalInterface
	public interface VertexConsumerSupplier<T>
	{
		VertexConsumer provideLayer(VertexConsumerProvider vertexConsumerProvider, T target, String objectName);
	}

	private static final String MODEL_MAGIC = "P3D";
	private static final String RIG_MAGIC = "P3DR";
	private static final int[] ACCEPTED_VERSIONS = { 0x02 };

	public final int version;
	public final HashMap<String, P3dSocket> transformables;
	public final P3dObject[] rootObjects;
	public final Box bounds;

	public P3dModel(int version, HashMap<String, P3dSocket> transformables, P3dObject[] rootObjects, Box bounds)
	{
		this.version = version;
		this.transformables = transformables;
		this.rootObjects = rootObjects;
		this.bounds = bounds;
	}

	public <T> void render(MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, T target, PartTransformer<T> transformer, VertexConsumerSupplier<T> vertexConsumerSupplier, int light, float tickDelta)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, target, light, vertexConsumerProvider, mesh, tickDelta, transformer, vertexConsumerSupplier);
	}

	private <T> void renderMesh(MatrixStack matrix, T target, int light, VertexConsumerProvider vertexConsumerProvider, P3dObject o, float tickDelta, PartTransformer<T> transformer, VertexConsumerSupplier<T> vertexConsumerSupplier)
	{
		matrix.push();

		var entry = transform(matrix, target, o, tickDelta, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		var vertexConsumer = vertexConsumerSupplier.provideLayer(vertexConsumerProvider, target, o.name);

		emitFaces(light, o, entry, vertexConsumer);

		for (var mesh : o.children)
			renderMesh(matrix, target, light, vertexConsumerProvider, mesh, tickDelta, transformer, vertexConsumerSupplier);

		matrix.pop();
	}

	public <T> void render(MatrixStack matrix, VertexConsumer vertexConsumer, T target, PartTransformer<T> transformer, int light, float tickDelta)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, target, light, vertexConsumer, mesh, tickDelta, transformer);
	}

	private <T> void renderMesh(MatrixStack matrix, T target, int light, VertexConsumer vertexConsumer, P3dObject o, float tickDelta, PartTransformer<T> transformer)
	{
		matrix.push();

		var entry = transform(matrix, target, o, tickDelta, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		emitFaces(light, o, entry, vertexConsumer);

		for (var mesh : o.children)
			renderMesh(matrix, target, light, vertexConsumer, mesh, tickDelta, transformer);

		matrix.pop();
	}

	@Nullable
	private <T> MatrixStack.Entry transform(MatrixStack matrix, T target, P3dObject o, float tickDelta, PartTransformer<T> transformer)
	{
		var entry = matrix.peek();
		entry.getPositionMatrix().multiply(o.transform);
		entry.getNormalMatrix().multiply(new Matrix3f(o.transform));

		if (transformer != null)
		{
			var transform = transformer.transform(target, o.name, tickDelta);

			if (transform == null)
			{
				return null;
			}

			entry.getPositionMatrix().multiply(transform);
			entry.getNormalMatrix().multiply(new Matrix3f(transform));
		}
		return entry;
	}

	private void emitFaces(int light, P3dObject o, MatrixStack.Entry entry, VertexConsumer vertexConsumer)
	{
		var modelMat = entry.getPositionMatrix();
		var normalMat = entry.getNormalMatrix();

		for (var face : o.faces)
		{
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[0], face.normal, face.texture[0]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[1], face.normal, face.texture[1]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[2], face.normal, face.texture[2]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[3], face.normal, face.texture[3]);
		}
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord)
	{
		vertexConsumer
				.vertex(modelMatrix, vertex.getX(), vertex.getY(), vertex.getZ())
				.color(255, 255, 255, 255)
				.texture(texCoord.getX(), 1 - texCoord.getY())
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(normalMatrix, normal.getX(), normal.getY(), normal.getZ())
				.next();
	}

	public static P3dModel tryLoad(Identifier modelFile, boolean hasVertexData)
	{
		try
		{
			var reader = PIO.getStream("data", modelFile);
			return read(reader, hasVertexData);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			var crashReport = CrashReport.create(ex, String.format("Loading PR3R file: %s", modelFile));
			throw new CrashException(crashReport);
		}
	}

	public static P3dModel read(InputStream stream, boolean hasVertexData) throws IOException
	{
		var objStream = new LittleEndianDataInputStream(stream);

		var magic = hasVertexData ? MODEL_MAGIC : RIG_MAGIC;

		// read header
		var identBytes = new byte[magic.length()];
		var read = objStream.read(identBytes);
		var ident = new String(identBytes);
		if (!ident.equals(magic) || read != identBytes.length)
			throw new IOException(String.format("Input file not %s model", magic));

		var version = objStream.readInt();

		if (!ArrayUtils.contains(ACCEPTED_VERSIONS, version))
			throw new IOException(String.format("Input file version is 0x%s, expected one of: %s", Integer.toHexString(version), getAcceptedVersionString()));

		// read sockets
		var numSockets = objStream.readInt();
		var transformables = new HashMap<String, P3dSocket>();

		for (var socketIdx = 0; socketIdx < numSockets; socketIdx++)
		{
			var name = DataReader.readNullTerminatedString(objStream);

			var hasParent = objStream.readBoolean();

			String parent = null;
			if (hasParent)
				parent = DataReader.readNullTerminatedString(objStream);

			var transform = DataReader.readMatrix4f(objStream);

			transformables.put(name, new P3dSocket(name, parent, transform));
		}

		// read objects
		var numObjects = objStream.readInt();
		var rootObjects = new P3dObject[numObjects];

		var rawVertices = new ArrayList<Vec3f>();

		for (var objectIdx = 0; objectIdx < numObjects; objectIdx++)
		{
			var object = readObject(transformables, null, objStream, hasVertexData, rawVertices);
			transformables.put(object.name, object);

			rootObjects[objectIdx] = object;
		}

		// build ancestry trees for sockets and parts which enables directly calculating transformation
		buildAncestry(transformables);

		var bounds = ModelUtil.getBounds(rawVertices);

		return new P3dModel(version, transformables, rootObjects, bounds);
	}

	private static void buildAncestry(HashMap<String, P3dSocket> parts)
	{
		for (var part : parts.values())
		{
			var parentName = part.parent;
			while (parentName != null)
			{
				var parent = parts.get(parentName);
				part.ancestry.add(0, parent);
				parentName = parent.parent;
			}
		}
	}

	@NotNull
	private static P3dObject readObject(HashMap<String, P3dSocket> objects, String parent, LittleEndianDataInputStream objStream, boolean hasVertexData, ArrayList<Vec3f> rawVertices) throws IOException
	{
		var name = DataReader.readNullTerminatedString(objStream);

		var transform = DataReader.readMatrix4f(objStream);

		var material = hasVertexData ? objStream.readByte() : (byte)0;

		var numFaces = hasVertexData ? objStream.readInt() : 0;
		var faces = new P3dFace[numFaces];

		for (var faceIdx = 0; faceIdx < numFaces; faceIdx++)
		{
			var positions = new Vec3f[4];
			var texture = new Vec3f[4];

			var normal = new Vec3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());

			for (var vertIdx = 0; vertIdx < 4; vertIdx++)
			{
				positions[vertIdx] = new Vec3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());
				texture[vertIdx] = new Vec3f(objStream.readFloat(), objStream.readFloat(), 0);

				rawVertices.add(positions[vertIdx]);
			}

			faces[faceIdx] = new P3dFace(positions, normal, texture);
		}

		var numChildren = objStream.readInt();
		var children = new P3dObject[numChildren];

		for (var childIdx = 0; childIdx < numChildren; childIdx++)
		{
			var m = readObject(objects, name, objStream, hasVertexData, rawVertices);
			objects.put(m.name, m);
			children[childIdx] = m;
		}

		return new P3dObject(name, parent, transform, material, faces, children);
	}

	private static String getAcceptedVersionString()
	{
		return Arrays.stream(ACCEPTED_VERSIONS).mapToObj(i -> "0x" + Integer.toHexString(i)).collect(Collectors.joining(", "));
	}
}
