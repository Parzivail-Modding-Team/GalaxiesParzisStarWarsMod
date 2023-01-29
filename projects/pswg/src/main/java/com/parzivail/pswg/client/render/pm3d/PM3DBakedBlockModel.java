package com.parzivail.pswg.client.render.pm3d;

import com.parzivail.util.block.DisplacingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.connecting.ConnectingNodeBlock;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.client.math.ClientMathUtil;
import com.parzivail.util.client.model.DynamicBakedModel;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class PM3DBakedBlockModel extends DynamicBakedModel
{
	private final CacheMethod cacheMethod;
	private final Sprite baseSprite;
	private final PM3DLod container;

	private PM3DBakedBlockModel(CacheMethod cacheMethod, Sprite baseSprite, Sprite particleSprite, PM3DLod container)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.cacheMethod = cacheMethod;
		this.baseSprite = baseSprite;
		this.container = container;
	}

	@Override
	protected CacheMethod getDiscriminator()
	{
		return cacheMethod;
	}

	private void emitFace(Matrix4f transformation, QuadEmitter quadEmitter, PM3DFace face)
	{
		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(getMaterial(face));

		var a = face.verts.get(0);
		var b = face.verts.get(1);
		var c = face.verts.get(2);
		var d = face.verts.size() == 4 ? face.verts.get(3) : c;

		var verts = container.verts();
		var normals = container.normals();
		var uvs = container.uvs();

		var vA = verts[a.vertex()];
		var vB = verts[b.vertex()];
		var vC = verts[c.vertex()];
		var vD = verts[d.vertex()];

		var nA = normals[a.normal()];
		var nB = normals[b.normal()];
		var nC = normals[c.normal()];
		var nD = normals[d.normal()];

		var tA = uvs[a.texture()];
		var tB = uvs[b.texture()];
		var tC = uvs[c.texture()];
		var tD = uvs[d.texture()];

		vA = ClientMathUtil.transform(vA, transformation);
		vB = ClientMathUtil.transform(vB, transformation);
		vC = ClientMathUtil.transform(vC, transformation);
		vD = ClientMathUtil.transform(vD, transformation);

		quadEmitter.pos(0, vA).normal(0, nA).sprite(0, 0, tA.x, 1 - tA.y);
		quadEmitter.pos(1, vB).normal(1, nB).sprite(1, 0, tB.x, 1 - tB.y);
		quadEmitter.pos(2, vC).normal(2, nC).sprite(2, 0, tC.x, 1 - tC.y);
		quadEmitter.pos(3, vD).normal(3, nD).sprite(3, 0, tD.x, 1 - tD.y);

		quadEmitter.spriteBake(0, baseSprite, MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();
	}

	private RenderMaterial getMaterial(PM3DFace face)
	{
		return switch (face.material)
		{
			case 0 -> MAT_DIFFUSE_OPAQUE;
			case 1 -> MAT_DIFFUSE_CUTOUT;
			case 2 -> MAT_DIFFUSE_TRANSLUCENT;
			case 3 -> MAT_EMISSIVE;
			default ->
			{
				var crashReport = CrashReport.create(null, String.format("Unknown material ID: %s", face.material));
				throw new CrashException(crashReport);
			}
		};
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
		if (state != null)
		{
			if (state.getBlock() instanceof ConnectingNodeBlock)
			{
				var meshBuilder = createMeshBuilder();//RENDERER.meshBuilder();
				var quadEmitter = meshBuilder.getEmitter();

				for (var o : container.objects())
				{
					if (!"NODE_CENTER".equals(o.objName()) && !state.get(ConnectingNodeBlock.FACING_PROPERTIES.get(FACING_SUBMODELS.get(o.objName()))))
						continue;

					for (var face : o.faces())
						emitFace(transformation, quadEmitter, face);
				}

				return meshBuilder.build();
			}

			if (state.getBlock() instanceof DisplacingBlock)
			{
				var shape = state.getOutlineShape(blockView, pos, ShapeContext.absent());
				var center = VoxelShapeUtil.getCenter(shape);
				transformation.translate((float)center.x - 0.5f, 0, (float)center.z - 0.5f);
			}
		}

		return createMesh(transformation);
	}

	@Override
	protected Mesh createItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		return createMesh(transformation);
	}

	private Mesh createMesh(Matrix4f transformation)
	{
		var meshBuilder = createMeshBuilder();//RENDERER.meshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		for (var o : container.objects())
			for (PM3DFace face : o.faces())
				emitFace(transformation, quadEmitter, face);

		return meshBuilder.build();
	}

	@Override
	protected Matrix4f createTransformation(BlockState state)
	{
		var mat = new Matrix4f();

		if (state == null)
		{
			// Item transformation, scale largest dimension to 1

			var bounds = container.bounds();

			var largestDimension = (float)Math.max(bounds.getXLength(), Math.max(bounds.getYLength(), bounds.getZLength()));

			mat.translate(0.5f, 0, 0.5f);
			mat.scale(1 / largestDimension, 1 / largestDimension, 1 / largestDimension);
			mat.translate(-0.5f, 0, -0.5f);

			return mat;
		}

		if (state.getBlock() instanceof WaterloggableRotatingBlock)
		{
			mat.translate(0.5f, 0, 0.5f);

			mat.rotate(ClientMathUtil.getRotation(state.get(WaterloggableRotatingBlock.FACING)));

			mat.translate(-0.5f, 0, -0.5f);
		}

		if (state.getBlock() instanceof PillarBlock)
		{
			var axis = state.get(PillarBlock.AXIS);

			switch (axis)
			{
				case X:
					mat.rotateZ((float)(Math.PI / -2));
					mat.translate(-1, 0, 0);
					break;
				case Z:
					mat.rotateY((float)(Math.PI / -2));
					mat.rotateZ((float)(Math.PI / -2));
					mat.translate(-1, 0, -1);
					break;
				case Y:
					break;
			}
		}

		return mat;
	}

	public static PM3DBakedBlockModel create(CacheMethod cacheMethod, PM3DLod container, Identifier baseTexture, Identifier particleTexture, Function<SpriteIdentifier, Sprite> spriteMap)
	{
		return new PM3DBakedBlockModel(cacheMethod, spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, baseTexture)), spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleTexture)), container);
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
