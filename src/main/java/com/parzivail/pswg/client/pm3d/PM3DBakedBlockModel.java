package com.parzivail.pswg.client.pm3d;

import com.parzivail.pswg.block.ConnectingNodeBlock;
import com.parzivail.pswg.block.RotatingBlock;
import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.util.ClientMathUtil;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockRenderView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class PM3DBakedBlockModel extends SimpleModel
{
	private final Discriminator discriminator;
	private final Sprite baseSprite;
	private final PM3DLod container;

	private PM3DBakedBlockModel(Discriminator discriminator, Sprite baseSprite, Sprite particleSprite, PM3DLod container)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.discriminator = discriminator;
		this.baseSprite = baseSprite;
		this.container = container;
	}

	@Override
	protected Discriminator getDiscriminator()
	{
		return discriminator;
	}

	private void emitFace(Matrix4f transformation, QuadEmitter quadEmitter, PM3DFace face)
	{
		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(getMaterial(face));

		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vector3f vA = container.verts[a.vertex];
		Vector3f vB = container.verts[b.vertex];
		Vector3f vC = container.verts[c.vertex];
		Vector3f vD = container.verts[d.vertex];

		Vector3f nA = container.normals[a.normal];
		Vector3f nB = container.normals[b.normal];
		Vector3f nC = container.normals[c.normal];
		Vector3f nD = container.normals[d.normal];

		Vector3f tA = container.uvs[a.texture];
		Vector3f tB = container.uvs[b.texture];
		Vector3f tC = container.uvs[c.texture];
		Vector3f tD = container.uvs[d.texture];

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

	public static final Map<String, Direction> FACING_SUBMODELS;

	static
	{
		FACING_SUBMODELS = new HashMap<>();
		FACING_SUBMODELS.put("NODE_PY", Direction.NORTH);
		FACING_SUBMODELS.put("NODE_NY", Direction.SOUTH);
		FACING_SUBMODELS.put("NODE_PX", Direction.EAST);
		FACING_SUBMODELS.put("NODE_NX", Direction.WEST);
		FACING_SUBMODELS.put("NODE_PZ", Direction.UP);
		FACING_SUBMODELS.put("NODE_NZ", Direction.DOWN);
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		if (state.getBlock() instanceof ConnectingNodeBlock)
		{
			MeshBuilder meshBuilder = RENDERER.meshBuilder();
			QuadEmitter quadEmitter = meshBuilder.getEmitter();

			for (PM3DObject o : container.objects)
			{
				if (!"NODE_CENTER".equals(o.objName) && !state.get(ConnectingNodeBlock.FACING_PROPERTIES.get(FACING_SUBMODELS.get(o.objName))))
					continue;

				for (PM3DFace face : o.faces)
					emitFace(transformation, quadEmitter, face);
			}

			return meshBuilder.build();
		}

		// TODO: make this a block flag
		if (state.getBlock() == SwgBlocks.Barrel.MosEisley)
		{
			Random r = randomSupplier.get();
			r.setSeed(state.getRenderingSeed(pos));

			float s = 0.5f;
			float dx = r.nextFloat() * s;
			float dz = r.nextFloat() * s;
			transformation.multiply(Matrix4f.translate(dx, 0, dz));
		}

		return createMesh(transformation);
	}

	@Override
	protected Mesh createItemMesh(Matrix4f transformation)
	{
		return createMesh(transformation);
	}

	private Mesh createMesh(Matrix4f transformation)
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

		if (state.getBlock() instanceof RotatingBlock)
		{
			mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));
			mat.multiply(new Quaternion(0, ((RotatingBlock)state.getBlock()).getRotationDegrees(state), 0, true));
			mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));
		}

		if (state.getBlock() instanceof PillarBlock)
		{
			Direction.Axis axis = state.get(PillarBlock.AXIS);

			switch (axis)
			{
				case X:
					mat.multiply(new Quaternion(0, 0, -90, true));
					mat.multiply(Matrix4f.translate(-1, 0, 0));
					break;
				case Z:
					mat.multiply(new Quaternion(0, -90, 0, true));
					mat.multiply(new Quaternion(0, 0, -90, true));
					mat.multiply(Matrix4f.translate(-1, 0, -1));
					break;
				case Y:
					break;
			}
		}

		return mat;
	}

	public static PM3DBakedBlockModel create(Discriminator discriminator, PM3DLod container, Identifier baseTexture, Identifier particleTexture, Function<SpriteIdentifier, Sprite> spriteMap)
	{
		return new PM3DBakedBlockModel(discriminator, spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, baseTexture)), spriteMap.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, particleTexture)), container);
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
