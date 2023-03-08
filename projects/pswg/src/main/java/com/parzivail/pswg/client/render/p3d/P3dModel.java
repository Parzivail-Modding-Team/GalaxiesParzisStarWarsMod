package com.parzivail.pswg.client.render.p3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.client.model.AbstractModel;
import com.parzivail.util.client.model.ModelUtil;
import com.parzivail.util.data.DataReader;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record P3dModel(int version, HashMap<String, P3dSocket> transformables, P3dObject[] rootObjects, Box bounds)
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

	@FunctionalInterface
	public interface SpriteSupplier<T>
	{
		Sprite provideLayer(T target, String objectName);
	}

	public static final int MAT_ID_DIFFUSE_OPAQUE = 0;
	public static final int MAT_ID_DIFFUSE_CUTOUT = 1;
	public static final int MAT_ID_DIFFUSE_TRANSLUCENT = 2;
	public static final int MAT_ID_EMISSIVE = 3;

	private static final String MODEL_MAGIC = "P3D";
	private static final String RIG_MAGIC = "P3DR";
	private static final int[] ACCEPTED_VERSIONS = { 0x02 };

	public static <T> Matrix4f identityTransformer(T instance, String socket, float tickDelta)
	{
		return new Matrix4f();
	}

	public void renderBlock(MatrixStack matrix, QuadEmitter quadEmitter, P3DBlockRenderTarget target, PartTransformer<P3DBlockRenderTarget> transformer, SpriteSupplier<P3DBlockRenderTarget> spriteSupplier, Supplier<Random> randomSupplier, RenderContext context)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, quadEmitter, mesh, target, transformer, spriteSupplier, randomSupplier, context);
	}

	public <T> void render(MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, T target, PartTransformer<T> transformer, VertexConsumerSupplier<T> vertexConsumerSupplier, int light, float tickDelta, int r, int g, int b, int a)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, target, light, vertexConsumerProvider, mesh, tickDelta, transformer, vertexConsumerSupplier, r, g, b, a);
	}

	private <T> void renderMesh(MatrixStack matrix, T target, int light, VertexConsumerProvider vertexConsumerProvider, P3dObject o, float tickDelta, PartTransformer<T> transformer, VertexConsumerSupplier<T> vertexConsumerSupplier, int r, int g, int b, int a)
	{
		matrix.push();

		var entry = transform(matrix, target, o, tickDelta, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		var vertexConsumer = vertexConsumerSupplier.provideLayer(vertexConsumerProvider, target, o.name);

		emitFaces(light, o, entry, vertexConsumer, r, g, b, a);

		for (var mesh : o.children)
			renderMesh(matrix, target, light, vertexConsumerProvider, mesh, tickDelta, transformer, vertexConsumerSupplier, r, g, b, a);

		matrix.pop();
	}

	private void renderMesh(MatrixStack matrix, QuadEmitter quadEmitter, P3dObject o, P3DBlockRenderTarget target, PartTransformer<P3DBlockRenderTarget> transformer, SpriteSupplier<P3DBlockRenderTarget> spriteSupplier, Supplier<Random> randomSupplier, RenderContext context)
	{
		matrix.push();

		var entry = transform(matrix, target, o, 0, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		var sprite = spriteSupplier.provideLayer(target, o.name);
		emitFaces(o, entry, quadEmitter, sprite);

		for (var mesh : o.children)
			renderMesh(matrix, quadEmitter, mesh, target, transformer, spriteSupplier, randomSupplier, context);

		matrix.pop();
	}

	public <T> void render(MatrixStack matrix, VertexConsumer vertexConsumer, T target, PartTransformer<T> transformer, int light, float tickDelta, int r, int g, int b, int a)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, target, light, vertexConsumer, mesh, tickDelta, transformer, r, g, b, a);
	}

	private <T> void renderMesh(MatrixStack matrix, T target, int light, VertexConsumer vertexConsumer, P3dObject o, float tickDelta, PartTransformer<T> transformer, int r, int g, int b, int a)
	{
		matrix.push();

		var entry = transform(matrix, target, o, tickDelta, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		emitFaces(light, o, entry, vertexConsumer, r, g, b, a);

		for (var mesh : o.children)
			renderMesh(matrix, target, light, vertexConsumer, mesh, tickDelta, transformer, r, g, b, a);

		matrix.pop();
	}

	public <T> void transformToSocket(Matrix4f mat, String socketName, T target, float tickDelta, PartTransformer<T> transformer)
	{
		var socket = transformables().get(socketName);
		for (var part : socket.ancestry)
		{
			mat.mul(part.transform);
			mat.mul(transformer.transform(target, part.name, tickDelta));
		}
		mat.mul(socket.transform);
	}

	@Nullable
	private <T> MatrixStack.Entry transform(MatrixStack matrix, T target, P3dObject o, float tickDelta, PartTransformer<T> transformer)
	{
		var entry = matrix.peek();
		entry.getPositionMatrix().mul(o.transform);
		entry.getNormalMatrix().mul(new Matrix3f(o.transform));

		if (transformer != null)
		{
			var transform = transformer.transform(target, o.name, tickDelta);

			if (transform == null)
			{
				return null;
			}

			entry.getPositionMatrix().mul(transform);
			entry.getNormalMatrix().mul(new Matrix3f(transform));
		}
		return entry;
	}

	private void emitFaces(int light, P3dObject o, MatrixStack.Entry entry, VertexConsumer vertexConsumer, int r, int g, int b, int a)
	{
		var modelMat = entry.getPositionMatrix();
		var normalMat = entry.getNormalMatrix();

		for (var face : o.faces)
		{
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[0], face.normal, face.texture[0], r, g, b, a);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[1], face.normal, face.texture[1], r, g, b, a);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[2], face.normal, face.texture[2], r, g, b, a);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[3], face.normal, face.texture[3], r, g, b, a);
		}
	}

	private void emitFaces(P3dObject o, MatrixStack.Entry entry, QuadEmitter quadEmitter, Sprite sprite)
	{
		var modelMat = entry.getPositionMatrix();
		var normalMat = entry.getNormalMatrix();

		for (var face : o.faces)
		{
			quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(getBlockMaterial(o.material));

			var vA = modelMat.transformPosition(face.positions[0]);
			var vB = modelMat.transformPosition(face.positions[1]);
			var vC = modelMat.transformPosition(face.positions[2]);
			var vD = modelMat.transformPosition(face.positions[3]);

			var n = new Vector3f(face.normal.x, face.normal.y, face.normal.z);
			n.mul(normalMat);
			n.normalize();

			quadEmitter.pos(0, vA).normal(0, n).sprite(0, 0, face.texture[0].x, 1 - face.texture[0].y);
			quadEmitter.pos(1, vB).normal(1, n).sprite(1, 0, face.texture[1].x, 1 - face.texture[1].y);
			quadEmitter.pos(2, vC).normal(2, n).sprite(2, 0, face.texture[2].x, 1 - face.texture[2].y);
			quadEmitter.pos(3, vD).normal(3, n).sprite(3, 0, face.texture[3].x, 1 - face.texture[3].y);

			quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);

			quadEmitter.emit();
		}
	}

	private RenderMaterial getBlockMaterial(byte material)
	{
		switch (material)
		{
			case MAT_ID_DIFFUSE_OPAQUE:
				return AbstractModel.MAT_DIFFUSE_OPAQUE;
			case MAT_ID_DIFFUSE_CUTOUT:
				return AbstractModel.MAT_DIFFUSE_CUTOUT;
			case MAT_ID_DIFFUSE_TRANSLUCENT:
				return AbstractModel.MAT_DIFFUSE_TRANSLUCENT;
			case MAT_ID_EMISSIVE:
				return AbstractModel.MAT_EMISSIVE;
			default:
			{
				var crashReport = CrashReport.create(null, String.format("Unknown material ID: %s", material));
				throw new CrashException(crashReport);
			}
		}
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vector3f vertex, Vector3f normal, Vector3f texCoord, int r, int g, int b, int a)
	{
		vertexConsumer
				.vertex(modelMatrix, vertex.x, vertex.y, vertex.z)
				.color(r, g, b, a)
				.texture(texCoord.x, 1 - texCoord.y)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(normalMatrix, normal.x, normal.y, normal.z)
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

		var rawVertices = new ArrayList<Vector3f>();

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
	private static P3dObject readObject(HashMap<String, P3dSocket> objects, String parent, LittleEndianDataInputStream objStream, boolean hasVertexData, ArrayList<Vector3f> rawVertices) throws IOException
	{
		var name = DataReader.readNullTerminatedString(objStream);

		var transform = DataReader.readMatrix4f(objStream);

		var material = hasVertexData ? objStream.readByte() : (byte)0;

		var numFaces = hasVertexData ? objStream.readInt() : 0;
		var faces = new P3dFace[numFaces];

		for (var faceIdx = 0; faceIdx < numFaces; faceIdx++)
		{
			var positions = new Vector3f[4];
			var texture = new Vector3f[4];

			var normal = new Vector3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());

			for (var vertIdx = 0; vertIdx < 4; vertIdx++)
			{
				positions[vertIdx] = new Vector3f(objStream.readFloat(), objStream.readFloat(), objStream.readFloat());
				texture[vertIdx] = new Vector3f(objStream.readFloat(), objStream.readFloat(), 0);

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
