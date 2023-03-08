package com.parzivail.p3d;

import com.parzivail.util.block.DisplacingBlock;
import com.parzivail.util.block.IPicklingBlock;
import com.parzivail.util.block.VoxelShapeUtil;
import com.parzivail.util.block.rotating.WaterloggableRotating3BlockWithGuiEntity;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class P3dBakedBlockModel extends DynamicBakedModel
{
	private final CacheMethod cacheMethod;
	private final Sprite baseSprite;
	private final HashMap<String, Sprite> additionalSprites;
	private final Identifier[] modelIds;

	private P3dBakedBlockModel(CacheMethod cacheMethod, Sprite baseSprite, Sprite particleSprite, HashMap<String, Sprite> additionalSprites, Identifier[] modelIds)
	{
		super(particleSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.cacheMethod = cacheMethod;
		this.baseSprite = baseSprite;
		this.additionalSprites = additionalSprites;
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

			if (state.getBlock() instanceof DisplacingBlock db)
			{
				var shape = db.getOutlineShape(state, blockView, pos, ShapeContext.absent());
				var center = VoxelShapeUtil.getCenter(shape);
				transformation.translate((float)center.x - 0.5f, 0, (float)center.z - 0.5f);
			}
			else if (state.getBlock() instanceof WaterloggableRotating3BlockWithGuiEntity)
			{
				var side = state.get(WaterloggableRotating3BlockWithGuiEntity.SIDE);
				if (side != WaterloggableRotating3BlockWithGuiEntity.Side.MIDDLE)
					return createMeshBuilder().build();
			}
		}

		return createMesh(new P3dBlockRenderTarget.Block(blockView, state, pos), randomSupplier, context, transformation);
	}

	@Override
	protected Mesh createItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		return createMesh(new P3dBlockRenderTarget.Item(stack), randomSupplier, context, transformation);
	}

	private Mesh createMesh(P3dBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var meshBuilder = createMeshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		var modelId = modelIds[0];

		if (target instanceof P3dBlockRenderTarget.Block blockRenderTarget)
			modelId = getPickleModel(modelId, blockRenderTarget.getState());

		var model = P3dManager.INSTANCE.get(modelId);

		var ms = new MatrixStack();
		var e = ms.peek();
		e.getPositionMatrix().mul(transformation);
		e.getNormalMatrix().mul(new Matrix3f(transformation));

		// blocks are centered in P3D models
		ms.translate(0.5f, 0, 0.5f);

		// one pixel in P3D models is 1.6 game pixels
		MathUtil.scalePos(ms, 0.625f, 0.625f, 0.625f);

		// P3D models are +Z forward
		ms.multiply(new Quaternionf().rotationY((float)(Math.PI / -2)));

		Block block = null;
		if (target instanceof P3dBlockRenderTarget.Block blockRenderTarget && blockRenderTarget.getState() != null)
			block = blockRenderTarget.getState().getBlock();
		else if (target instanceof P3dBlockRenderTarget.Item itemRenderTarget && itemRenderTarget.getStack().getItem() instanceof BlockItem blockItem)
			block = blockItem.getBlock();

		P3dBlockRendererRegistry.get(block).renderBlock(ms, quadEmitter, target, randomSupplier, context, model, baseSprite, additionalSprites);

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

		if (state.getBlock() instanceof WaterloggableRotatingBlock)
		{
			mat.translate(0.5f, 0.5f, 0.5f);

			mat.rotate(MathUtil.getRotation(state.get(WaterloggableRotatingBlock.FACING)));

			mat.translate(-0.5f, -0.5f, -0.5f);
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

	@NotNull
	public static Matrix4f getItemTransformation(P3dModel model)
	{
		var mat = new Matrix4f();

		var bounds = model.bounds();

		var largestDimension = 0.625f * (float)Math.max(bounds.getXLength(), Math.max(bounds.getYLength(), bounds.getZLength()));
		var scale = 1 / largestDimension;

		var minX = (float)bounds.minX * 0.625f;
		var maxX = (float)bounds.maxX * 0.625f;
		var minY = (float)bounds.minY * 0.625f;
		var minZ = (float)bounds.minZ * 0.625f;
		var maxZ = (float)bounds.maxZ * 0.625f;

		mat.translate(0.5f, 0, 0.5f);
		mat.scale(scale, scale, scale);
		mat.translate(-0.5f, 0, -0.5f);

		mat.translate((maxX - minX) / 2 - maxX, -minY, (maxZ - minZ) / 2 - maxZ);

		mat.translate(0.5f, 0, 0.5f);
		mat.rotate(QuatUtil.ROT_Y_POS90);
		mat.translate(-0.5f, 0, -0.5f);

		return mat;
	}

	public static P3dBakedBlockModel create(Function<SpriteIdentifier, Sprite> spriteMap, CacheMethod cacheMethod, Identifier baseTexture, Identifier particleTexture, HashMap<String, Identifier> additionalTextures, Identifier... models)
	{
		HashMap<String, Sprite> additionalSprites = new HashMap<>();

		for (var entry : additionalTextures.entrySet())
			additionalSprites.put(entry.getKey(), spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, entry.getValue())));

		return new P3dBakedBlockModel(
				cacheMethod,
				spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, baseTexture)),
				spriteMap.apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, particleTexture)),
				additionalSprites,
				models
		);
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
