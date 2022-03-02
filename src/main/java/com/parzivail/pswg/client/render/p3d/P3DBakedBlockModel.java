package com.parzivail.pswg.client.render.p3d;

import com.parzivail.util.block.DisplacingBlock;
import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.math.ClientMathUtil;
import com.parzivail.util.math.MatrixStackUtil;
import com.parzivail.util.math.QuatUtil;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class P3DBakedBlockModel extends DynamicBakedModel
{
	private final CacheMethod cacheMethod;
	private final Sprite baseSprite;
	private final Identifier[] modelIds;

	private P3DBakedBlockModel(CacheMethod cacheMethod, Sprite baseSprite, Sprite particleSprite, Identifier[] modelIds)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.cacheMethod = cacheMethod;
		this.baseSprite = baseSprite;
		this.modelIds = modelIds;
	}

	@Override
	protected CacheMethod getDiscriminator()
	{
		return cacheMethod;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		if (state != null)
		{
			// TODO: find better way to bake connecting models
			//			if (state.getBlock() instanceof ConnectingNodeBlock)
			//			{
			//				var meshBuilder = createMeshBuilder();
			//				var quadEmitter = meshBuilder.getEmitter();
			//
			//				for (var o : container.objects())
			//				{
			//					if (!"NODE_CENTER".equals(o.objName()) && !state.get(ConnectingNodeBlock.FACING_PROPERTIES.get(FACING_SUBMODELS.get(o.objName()))))
			//						continue;
			//
			//					for (var face : o.faces())
			//						emitFace(transformation, quadEmitter, face);
			//				}
			//
			//				return meshBuilder.build();
			//			}

			if (state.getBlock() instanceof DisplacingBlock)
			{
				var shape = state.getBlock().getOutlineShape(state, blockView, pos, ShapeContext.absent());
				var center = VoxelShapeUtil.getCenter(shape);
				transformation.multiply(Matrix4f.translate((float)center.x - 0.5f, 0, (float)center.z - 0.5f));
			}
		}

		return createMesh(new P3DBlockRenderTarget.Block(blockView, state, pos), randomSupplier, context, transformation);
	}

	@Override
	protected Mesh createItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		return createMesh(new P3DBlockRenderTarget.Item(stack), randomSupplier, context, transformation);
	}

	private Mesh createMesh(P3DBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var meshBuilder = createMeshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		var modelId = modelIds[0];

		if (target instanceof P3DBlockRenderTarget.Block blockRenderTarget)
		{
			var state = blockRenderTarget.getState();
			modelId = getPickleModel(modelId, state);
		}

		var model = P3dManager.INSTANCE.get(modelId);

		var ms = new MatrixStack();
		var e = ms.peek();
		e.getPositionMatrix().multiply(transformation);
		e.getNormalMatrix().multiply(new Matrix3f(transformation));

		// blocks are centered in P3D models
		ms.translate(0.5f, 0, 0.5f);

		// one pixel in P3D models is 1.6 game pixels
		MatrixStackUtil.scalePos(ms, 0.625f, 0.625f, 0.625f);

		// P3D models are +Z forward
		ms.multiply(new Quaternion(0, -90, 0, true));

		// TODO: allow transformers
		model.renderBlock(ms, quadEmitter, target, null, randomSupplier, context, baseSprite);

		return meshBuilder.build();
	}

	private Identifier getPickleModel(Identifier defaultModel, BlockState state)
	{
		if (state != null && state.getBlock() instanceof IPicklingBlock pb)
		{
			var pickleProp = pb.getPickleProperty();
			var pickle = state.get(pickleProp) - 1;
			if (pickle >= 0 && pickle < modelIds.length)
				defaultModel = modelIds[pickle];
		}
		return defaultModel;
	}

	@Override
	protected Matrix4f createTransformation(BlockState state)
	{

		var modelId = getPickleModel(modelIds[0], state);

		var model = P3dManager.INSTANCE.get(modelId);

		if (state == null)
		{
			// Item transformation, scale largest dimension to 1

			return getItemTransformation(model);
		}

		var mat = new Matrix4f();
		mat.loadIdentity();

		if (state.getBlock() instanceof RotatingBlock)
		{
			mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));

			mat.multiply(ClientMathUtil.getRotation(state.get(RotatingBlock.FACING)));

			mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));
		}

		if (state.getBlock() instanceof PillarBlock)
		{
			var axis = state.get(PillarBlock.AXIS);

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

	@NotNull
	public static Matrix4f getItemTransformation(P3dModel model)
	{
		var mat = new Matrix4f();
		mat.loadIdentity();

		var bounds = model.bounds();

		var largestDimension = 0.625f * (float)Math.max(bounds.getXLength(), Math.max(bounds.getYLength(), bounds.getZLength()));
		var scale = 1 / largestDimension;

		var minX = (float)bounds.minX * 0.625f;
		var maxX = (float)bounds.maxX * 0.625f;
		var minY = (float)bounds.minY * 0.625f;
		var minZ = (float)bounds.minZ * 0.625f;
		var maxZ = (float)bounds.maxZ * 0.625f;

		mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));
		mat.multiply(Matrix4f.scale(scale, scale, scale));
		mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));

		mat.multiply(Matrix4f.translate((maxX - minX) / 2 - maxX, -minY, (maxZ - minZ) / 2 - maxZ));

		mat.multiply(Matrix4f.translate(0.5f, 0, 0.5f));
		mat.multiply(QuatUtil.ROT_Y_POS90);
		mat.multiply(Matrix4f.translate(-0.5f, 0, -0.5f));

		return mat;
	}

	public static P3DBakedBlockModel create(Function<SpriteIdentifier, Sprite> spriteMap, CacheMethod cacheMethod, Identifier baseTexture, Identifier particleTexture, Identifier... models)
	{
		return new P3DBakedBlockModel(cacheMethod, spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, baseTexture)), spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleTexture)), models);
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
