package com.parzivail.pswg.client.pm3d;

import com.parzivail.pswg.block.RotatingBlock;
import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.pswg.util.ClientMathUtil;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

import java.util.function.Function;

public class PM3DBakedBlockModel extends SimpleModel
{
	private final Sprite baseSprite;
	private final PM3DFile container;

	private PM3DBakedBlockModel(Sprite baseSprite, Sprite particleSprite, PM3DFile container)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.baseSprite = baseSprite;
		this.container = container;
	}

	private void emitFace(Matrix4f transformation, QuadEmitter quadEmitter, PM3DFace face)
	{
		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(getMaterial(face));

		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vector3f vA = container.verts.get(a.getVertex());
		Vector3f vB = container.verts.get(b.getVertex());
		Vector3f vC = container.verts.get(c.getVertex());
		Vector3f vD = container.verts.get(d.getVertex());

		Vector3f nA = container.normals.get(a.getNormal());
		Vector3f nB = container.normals.get(b.getNormal());
		Vector3f nC = container.normals.get(c.getNormal());
		Vector3f nD = container.normals.get(d.getNormal());

		Vector3f tA = container.uvs.get(a.getTexture());
		Vector3f tB = container.uvs.get(b.getTexture());
		Vector3f tC = container.uvs.get(c.getTexture());
		Vector3f tD = container.uvs.get(d.getTexture());

		vA = ClientMathUtil.transform(vA, transformation);
		vB = ClientMathUtil.transform(vB, transformation);
		vC = ClientMathUtil.transform(vC, transformation);
		vD = ClientMathUtil.transform(vD, transformation);

		quadEmitter.pos(0, vA).normal(0, nA).sprite(0, 0, tA.getX(), 1 - tA.getY());
		quadEmitter.pos(1, vB).normal(1, nB).sprite(1, 0, tB.getX(), 1 - tB.getY());
		quadEmitter.pos(2, vC).normal(2, nC).sprite(2, 0, tC.getX(), 1 - tC.getY());
		quadEmitter.pos(3, vD).normal(3, nD).sprite(3, 0, tD.getX(), 1 - tD.getY());

		quadEmitter.spriteBake(0, baseSprite, MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();
	}

	private RenderMaterial getMaterial(PM3DFace face)
	{
		switch (face.material)
		{
			case 0:
				return MAT_DIFFUSE_OPAQUE;
			case 1:
				return MAT_DIFFUSE_CUTOUT;
			case 2:
				return MAT_DIFFUSE_TRANSLUCENT;
			case 3:
				return MAT_EMISSIVE;
			default:
			{
				CrashReport crashReport = CrashReport.create(null, String.format("Unknown material ID: %s", face.material));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	protected Mesh createMesh(Matrix4f transformation)
	{
		MeshBuilder meshBuilder = RENDERER.meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		for (PM3DObject o : container.objects)
			for (PM3DFace face : o.faces)
				emitFace(transformation, quadEmitter, face);

		return meshBuilder.build();
	}

	@Override
	protected Matrix4f createTransformation(BlockState state)
	{
		Matrix4f mat = new Matrix4f();
		mat.loadIdentity();

		if (state == null)
		{
			// Item transformation, scale largest dimension to 1

			float largestDimension = (float)Math.max(container.bounds.getXLength(), Math.max(container.bounds.getYLength(), container.bounds.getZLength()));

			mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));
			mat.multiply(Matrix4f.scale(1 / largestDimension, 1 / largestDimension, 1 / largestDimension));
			mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));

			return mat;
		}

		if (state.contains(RotatingBlock.ROTATION))
		{
			mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));

			int rotation = state.get(RotatingBlock.ROTATION);
			mat.multiply(new Quaternion(0, -rotation * 45, 0, true));

			mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));
		}

		return mat;
	}

	public static PM3DBakedBlockModel create(PM3DFile container, Identifier baseTexture, Identifier particleTexture, Function<SpriteIdentifier, Sprite> spriteMap)
	{
		return new PM3DBakedBlockModel(spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, baseTexture)), spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, particleTexture)), container);
	}

	@Override
	public boolean hasDepth()
	{
		return true;
	}

	@Override
	public boolean isSideLit()
	{
		return true;
	}
}
