package com.parzivail.pswg.client.render.model;

import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.client.ConnectedTextureHelper;
import com.parzivail.util.client.SubSprite;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.math.SpriteSheetPoint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConnectedTextureModel extends DynamicBakedModel
{
	private static final Vec3f[] ORIGINS = new Vec3f[6];
	private static final Vec3f[] DELTAU = new Vec3f[6];
	private static final Vec3f[] DELTAV = new Vec3f[6];

	static
	{
		ORIGINS[Direction.UP.getId()] = new Vec3f(0, 1, 0);
		ORIGINS[Direction.DOWN.getId()] = new Vec3f(1, 0, 0);
		ORIGINS[Direction.WEST.getId()] = new Vec3f(0, 1, 0);
		ORIGINS[Direction.EAST.getId()] = new Vec3f(1, 1, 1);
		ORIGINS[Direction.NORTH.getId()] = new Vec3f(1, 1, 0);
		ORIGINS[Direction.SOUTH.getId()] = new Vec3f(0, 1, 1);

		DELTAU[Direction.UP.getId()] = Vec3f.POSITIVE_X;
		DELTAV[Direction.UP.getId()] = Vec3f.POSITIVE_Z;

		DELTAU[Direction.DOWN.getId()] = Vec3f.NEGATIVE_X;
		DELTAV[Direction.DOWN.getId()] = Vec3f.POSITIVE_Z;

		DELTAU[Direction.SOUTH.getId()] = Vec3f.POSITIVE_X;
		DELTAV[Direction.SOUTH.getId()] = Vec3f.NEGATIVE_Y;

		DELTAU[Direction.NORTH.getId()] = Vec3f.NEGATIVE_X;
		DELTAV[Direction.NORTH.getId()] = Vec3f.NEGATIVE_Y;

		DELTAU[Direction.WEST.getId()] = Vec3f.POSITIVE_Z;
		DELTAV[Direction.WEST.getId()] = Vec3f.NEGATIVE_Y;

		DELTAU[Direction.EAST.getId()] = Vec3f.NEGATIVE_Z;
		DELTAV[Direction.EAST.getId()] = Vec3f.NEGATIVE_Y;
	}

	private final boolean hConnect;
	private final boolean vConnect;
	private final boolean lConnect;
	private final EnumSet<Direction> capDirections;
	private final Sprite borderSprite;
	private final Sprite capSprite;

	public ConnectedTextureModel(boolean hConnect, boolean vConnect, boolean lConnect, EnumSet<Direction> capDirections, Sprite blankSprite, Sprite borderSprite, Sprite capSprite)
	{
		super(blankSprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.hConnect = hConnect;
		this.vConnect = vConnect;
		this.lConnect = lConnect;
		this.capDirections = capDirections;
		this.borderSprite = borderSprite;
		this.capSprite = capSprite;
	}

	@Override
	protected CacheMethod getDiscriminator()
	{
		return CacheMethod.NO_CACHING;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var minecraft = MinecraftClient.getInstance();

		var meshBuilder = createMeshBuilder();//RENDERER.meshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		final var random = randomSupplier == null ? new Random(42) : randomSupplier.get();

		var blockModels = minecraft.getBlockRenderManager().getModels();
		var model = blockModels.getModel(SwgBlocks.Panel.LabWall.getDefaultState());

		// TODO: fix item lighting
		//		if (state == null) // Assume it's an item
		//		{
		//			model = Client.minecraft.getItemRenderer().getHeldItemModel(new ItemStack(SwgBlocks.Panel.LabWall), null, null);
		//		}

		for (var i = 0; i < ModelHelper.NULL_FACE_ID; i++)
		{
			if (state != null && !(state.getBlock() instanceof ConnectingBlock))
				continue;

			final var faceDirection = ModelHelper.faceFromIndex(i);
			if (faceDirection != null)
			{
				final var facingProp = ConnectingBlock.FACING_PROPERTIES.get(faceDirection);

				if (state != null && state.get(facingProp))
				{
					quadEmitter.emit();
					continue;
				}
			}

			var sprite = modelSprite;

			if (capSprite != null && capDirections.contains(faceDirection))
				sprite = capSprite;

			Vec3f min = ORIGINS[faceDirection.getId()];
			Vec3f dU = DELTAU[faceDirection.getId()];
			Vec3f dV = DELTAV[faceDirection.getId()];

			emitTopQuad(quadEmitter, sprite, borderSprite, blockView, state, pos, faceDirection, min, dU, dV);
		}

		return meshBuilder.build();
	}

	private static Vec3f uv(Vec3f min, Vec3f dU, Vec3f dV, float u, float v)
	{
		return new Vec3f(
				min.getX() + dU.getX() * u + dV.getX() * v,
				min.getY() + dU.getY() * u + dV.getY() * v,
				min.getZ() + dU.getZ() * u + dV.getZ() * v
		);
	}

	private void emitTopQuad(QuadEmitter quadEmitter, Sprite blankSprite, Sprite borderSprite, BlockRenderView blockView, BlockState state, BlockPos pos, Direction direction, Vec3f min, Vec3f dU, Vec3f dV)
	{
		var subSpriteEntry = ConnectedTextureHelper.getConnectedBlockTexture(blockView, state, pos, direction, hConnect, vConnect, lConnect);
		var normal = direction.getUnitVector();

		if (subSpriteEntry == null)
		{
			// No corners or edges
			quadEmitter
					.colorIndex(1)
					.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
					.pos(0, min)
					.normal(0, normal)
					.sprite(0, 0, blankSprite.getMinU(), blankSprite.getMinV())
					.pos(1, uv(min, dU, dV, 0, 1))
					.normal(1, normal)
					.sprite(1, 0, blankSprite.getMinU(), blankSprite.getMaxV())
					.pos(2, uv(min, dU, dV, 1, 1))
					.normal(2, normal)
					.sprite(2, 0, blankSprite.getMaxU(), blankSprite.getMaxV())
					.pos(3, uv(min, dU, dV, 1, 0))
					.normal(3, normal)
					.sprite(3, 0, blankSprite.getMaxU(), blankSprite.getMinV())
					.emit();

			return;
		}

		// top left
		var subSprite = getSubSprite(subSpriteEntry.TopLeft(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.pos(0, min)
				.normal(0, normal)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, uv(min, dU, dV, 0, 0.5f))
				.normal(1, normal)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, uv(min, dU, dV, 0.5f, 0.5f))
				.normal(2, normal)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, uv(min, dU, dV, 0.5f, 0))
				.normal(3, normal)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// top right
		subSprite = getSubSprite(subSpriteEntry.TopRight(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.pos(0, uv(min, dU, dV, 0.5f, 0))
				.normal(0, normal)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, uv(min, dU, dV, 0.5f, 0.5f))
				.normal(1, normal)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, uv(min, dU, dV, 1, 0.5f))
				.normal(2, normal)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, uv(min, dU, dV, 1, 0))
				.normal(3, normal)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// bottom left
		subSprite = getSubSprite(subSpriteEntry.BottomLeft(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.pos(0, uv(min, dU, dV, 0, 0.5f))
				.normal(0, normal)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, uv(min, dU, dV, 0, 1))
				.normal(1, normal)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, uv(min, dU, dV, 0.5f, 1))
				.normal(2, normal)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, uv(min, dU, dV, 0.5f, 0.5f))
				.normal(3, normal)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// bottom right
		subSprite = getSubSprite(subSpriteEntry.BottomRight(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.pos(0, uv(min, dU, dV, 0.5f, 0.5f))
				.normal(0, normal)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, uv(min, dU, dV, 0.5f, 1))
				.normal(1, normal)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, uv(min, dU, dV, 1, 1))
				.normal(2, normal)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, uv(min, dU, dV, 1, 0.5f))
				.normal(3, normal)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();
	}

	private static SubSprite getSubSprite(SpriteSheetPoint point, Sprite blankSprite, Sprite borderSprite, int columns, int rows)
	{
		var sprite = borderSprite;

		if (point.sheet() == 1) // "blank" cornerless texture
		{
			sprite = blankSprite;
			rows = 2;
			columns = 2;
		}

		var spriteWidth = sprite.getMaxU() - sprite.getMinU();
		var spriteHeight = sprite.getMaxV() - sprite.getMinV();

		var columnSpan = spriteWidth / columns;
		var rowSpan = spriteHeight / rows;

		return new SubSprite(sprite.getMinU() + point.x() * columnSpan, sprite.getMinV() + point.y() * rowSpan, sprite.getMinU() + (point.x() + 1) * columnSpan, sprite.getMinV() + (point.y() + 1) * rowSpan);
	}

	@Override
	protected Mesh createItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		return createBlockMesh(null, null, null, null, null, transformation);
	}

	@Override
	protected Matrix4f createTransformation(BlockState state)
	{
		var m = new Matrix4f();
		m.loadIdentity();
		return m;
	}

	@Override
	public boolean hasDepth()
	{
		return false;
	}

	@Override
	public boolean isSideLit()
	{
		return false;
	}

	public static class Unbaked extends ClonableUnbakedModel
	{
		private final Function<Function<SpriteIdentifier, Sprite>, ConnectedTextureModel> baker;
		private ConnectedTextureModel cachedBakedModel;
		private final boolean hConnect;
		private final boolean vConnect;
		private final boolean lConnect;
		private final EnumSet<Direction> capDirections;
		private final SpriteIdentifier sprite;
		private final SpriteIdentifier borderSprite;
		private final SpriteIdentifier capSprite;

		public Unbaked(boolean hConnect, boolean vConnect, boolean lConnect, EnumSet<Direction> capDirections, SpriteIdentifier sprite, SpriteIdentifier borderSprite, SpriteIdentifier capSprite)
		{
			this.hConnect = hConnect;
			this.vConnect = vConnect;
			this.lConnect = lConnect;
			this.capDirections = capDirections;
			this.sprite = sprite;
			this.borderSprite = borderSprite;
			this.capSprite = capSprite;
			this.baker = spriteLoader -> new ConnectedTextureModel(hConnect, vConnect, lConnect, capDirections, spriteLoader.apply(sprite), spriteLoader.apply(borderSprite), capSprite == null ? null : spriteLoader.apply(capSprite));
		}

		@Override
		public Collection<Identifier> getModelDependencies()
		{
			return Collections.emptyList();
		}

		@Override
		public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
		{
			var ids = new ArrayList<SpriteIdentifier>();

			ids.add(sprite);
			ids.add(borderSprite);

			if (capSprite != null)
				ids.add(capSprite);

			return ids;
		}

		@Override
		public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
		{
			if (cachedBakedModel != null)
				return cachedBakedModel;

			var result = baker.apply(textureGetter);
			cachedBakedModel = result;
			return result;
		}

		@Override
		public ClonableUnbakedModel copy()
		{
			return new Unbaked(hConnect, vConnect, lConnect, capDirections.clone(), sprite, borderSprite, capSprite);
		}
	}
}
