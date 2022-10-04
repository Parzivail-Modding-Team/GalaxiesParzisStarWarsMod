package com.parzivail.pswg.client.render.p3d;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.pswg.util.PIO;
import com.parzivail.util.client.math.ClientMathUtil;
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
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	private static final String MODEL_MAGIC = "P3D";
	private static final String RIG_MAGIC = "P3DR";
	private static final int[] ACCEPTED_VERSIONS = { 0x02 };

	public void renderBlock(MatrixStack matrix, QuadEmitter quadEmitter, P3DBlockRenderTarget target, PartTransformer<P3DBlockRenderTarget> transformer, Supplier<Random> randomSupplier, RenderContext context, Sprite sprite)
	{
		for (var mesh : rootObjects)
			renderMesh(matrix, quadEmitter, mesh, target, transformer, randomSupplier, context, sprite);
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

	private <T> void renderMesh(MatrixStack matrix, QuadEmitter quadEmitter, P3dObject o, P3DBlockRenderTarget target, PartTransformer<P3DBlockRenderTarget> transformer, Supplier<Random> randomSupplier, RenderContext context, Sprite sprite)
	{
		matrix.push();

		var entry = transform(matrix, target, o, 0, transformer);
		if (entry == null)
		{
			matrix.pop();
			return;
		}

		emitFaces(o, entry, quadEmitter, sprite);

		for (var mesh : o.children)
			renderMesh(matrix, quadEmitter, mesh, target, transformer, randomSupplier, context, sprite);

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

			var vA = ClientMathUtil.transform(face.positions[0], modelMat);
			var vB = ClientMathUtil.transform(face.positions[1], modelMat);
			var vC = ClientMathUtil.transform(face.positions[2], modelMat);
			var vD = ClientMathUtil.transform(face.positions[3], modelMat);

			var n = new Vec3f(face.normal.getX(), face.normal.getY(), face.normal.getZ());
			n.transform(normalMat);
			n.normalize();

			quadEmitter.pos(0, vA).normal(0, n).sprite(0, 0, face.texture[0].getX(), 1 - face.texture[0].getY());
			quadEmitter.pos(1, vB).normal(1, n).sprite(1, 0, face.texture[1].getX(), 1 - face.texture[1].getY());
			quadEmitter.pos(2, vC).normal(2, n).sprite(2, 0, face.texture[2].getX(), 1 - face.texture[2].getY());
			quadEmitter.pos(3, vD).normal(3, n).sprite(3, 0, face.texture[3].getX(), 1 - face.texture[3].getY());

			quadEmitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED);

			quadEmitter.emit();
		}
	}

	private RenderMaterial getBlockMaterial(byte material)
	{
		switch (material)
		{
			case 0:
				return AbstractModel.MAT_DIFFUSE_OPAQUE;
			case 1:
				return AbstractModel.MAT_DIFFUSE_CUTOUT;
			case 2:
				return AbstractModel.MAT_DIFFUSE_TRANSLUCENT;
			case 3:
				return AbstractModel.MAT_EMISSIVE;
			default:
			{
				var crashReport = CrashReport.create(null, String.format("Unknown material ID: %s", material));
				throw new CrashException(crashReport);
			}
		}
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord, int r, int g, int b, int a)
	{
		vertexConsumer
				.vertex(modelMatrix, vertex.getX(), vertex.getY(), vertex.getZ())
				.color(r, g, b, a)
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
