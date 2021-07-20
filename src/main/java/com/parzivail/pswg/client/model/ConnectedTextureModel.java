package com.parzivail.pswg.client.model;

import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.client.ConnectedTextureHelper;
import com.parzivail.util.client.SubSprite;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.math.Point;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
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
	private final boolean hConnect;
	private final boolean vConnect;
	private final boolean lConnect;
	private final Sprite capSprite;

	public ConnectedTextureModel(boolean hConnect, boolean vConnect, boolean lConnect, Sprite sprite, Sprite capSprite)
	{
		super(sprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
		this.hConnect = hConnect;
		this.vConnect = vConnect;
		this.lConnect = lConnect;
		this.capSprite = capSprite;
	}

	@Override
	protected Discriminator getDiscriminator()
	{
		return Discriminator.NONE;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var minecraft = MinecraftClient.getInstance();

		var meshBuilder = RENDERER.meshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		final var random = randomSupplier == null ? new Random(42) : randomSupplier.get();

		var blockModels = minecraft.getBlockRenderManager().getModels();
		var model = blockModels.getModel(SwgBlocks.Panel.LabWall.getDefaultState());

		// TODO: fix item lighting
		//		if (state == null) // Assume it's an item
		//		{
		//			model = Client.minecraft.getItemRenderer().getHeldItemModel(new ItemStack(SwgBlocks.Panel.LabWall), null, null);
		//		}

		//		for (var i = 0; i <= ModelHelper.NULL_FACE_ID; i++)
		//		{
		//			if (state != null && !(state.getBlock() instanceof ConnectingBlock))
		//				continue;
		//
		//			final var faceDirection = ModelHelper.faceFromIndex(i);
		//			if (faceDirection != null)
		//			{
		//				final var facingProp = ConnectingBlock.FACING_PROPERTIES.get(faceDirection);
		//
		//				if (state != null && state.get(facingProp))
		//				{
		//					quadEmitter.emit();
		//					continue;
		//				}
		//			}
		//
		//			for (final var q : quads)
		//			{
		//				quadEmitter.fromVanilla(q, null, faceDirection);
		//
		//				var subSpriteEntry = ConnectedTextureHelper.getConnectedBlockTexture(blockView, state, pos, faceDirection, hConnect, vConnect, lConnect);
		//				var subSpritePoint = subSpriteEntry.Edges();
		//
		//				var sprite = modelSprite;
		//
		//				if (capSprite != null && (faceDirection == Direction.UP || faceDirection == Direction.DOWN))
		//					sprite = capSprite;
		//
		//				var subSprite = getSubSprite(sprite, 4, 4, subSpritePoint.x(), subSpritePoint.y());
		//
		//				quadEmitter.sprite(0, 0, subSprite.minU(), subSprite.minV());
		//				quadEmitter.sprite(1, 0, subSprite.minU(), subSprite.maxV());
		//				quadEmitter.sprite(2, 0, subSprite.maxU(), subSprite.maxV());
		//				quadEmitter.sprite(3, 0, subSprite.maxU(), subSprite.minV());
		//				quadEmitter.emit();
		//			}
		//		}

		var faceDirection = Direction.UP;

		var sprite = modelSprite;

		if (capSprite != null && (faceDirection == Direction.UP || faceDirection == Direction.DOWN))
			sprite = capSprite;

		Sprite blankSprite = modelSprite; // TODO

		emitTopQuad(quadEmitter, blankSprite, sprite, blockView, state, pos);

		return meshBuilder.build();
	}

	private void emitTopQuad(QuadEmitter quadEmitter, Sprite blankSprite, Sprite borderSprite, BlockRenderView blockView, BlockState state, BlockPos pos)
	{
		var subSpriteEntry = ConnectedTextureHelper.getConnectedBlockTexture(blockView, state, pos, Direction.UP, hConnect, vConnect, lConnect);

		if (subSpriteEntry == null)
		{
			// No corners or edges
			quadEmitter
					.colorIndex(1)
					.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
					.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find())
					.pos(0, new Vec3f(0, 1, 0))
					.normal(0, Vec3f.POSITIVE_Y)
					.sprite(0, 0, blankSprite.getMinU(), blankSprite.getMinV())
					.pos(1, new Vec3f(0, 1, 1))
					.normal(1, Vec3f.POSITIVE_Y)
					.sprite(1, 0, blankSprite.getMinU(), blankSprite.getMaxV())
					.pos(2, new Vec3f(1, 1, 1))
					.normal(2, Vec3f.POSITIVE_Y)
					.sprite(2, 0, blankSprite.getMaxU(), blankSprite.getMaxV())
					.pos(3, new Vec3f(1, 1, 0))
					.normal(3, Vec3f.POSITIVE_Y)
					.sprite(3, 0, blankSprite.getMaxU(), blankSprite.getMinV())
					.emit();

			return;
		}

		// top left
		var subSprite = getSubSprite(subSpriteEntry.TopLeft(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find())
				.pos(0, new Vec3f(0, 1, 0))
				.normal(0, Vec3f.POSITIVE_Y)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, new Vec3f(0, 1, 0.5f))
				.normal(1, Vec3f.POSITIVE_Y)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, new Vec3f(0.5f, 1, 0.5f))
				.normal(2, Vec3f.POSITIVE_Y)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, new Vec3f(0.5f, 1, 0))
				.normal(3, Vec3f.POSITIVE_Y)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// top right
		subSprite = getSubSprite(subSpriteEntry.TopRight(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find())
				.pos(0, new Vec3f(0.5f, 1, 0))
				.normal(0, Vec3f.POSITIVE_Y)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, new Vec3f(0.5f, 1, 0.5f))
				.normal(1, Vec3f.POSITIVE_Y)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, new Vec3f(1, 1, 0.5f))
				.normal(2, Vec3f.POSITIVE_Y)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, new Vec3f(1, 1, 0))
				.normal(3, Vec3f.POSITIVE_Y)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// bottom left
		subSprite = getSubSprite(subSpriteEntry.BottomLeft(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find())
				.pos(0, new Vec3f(0, 1, 0.5f))
				.normal(0, Vec3f.POSITIVE_Y)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, new Vec3f(0, 1, 1))
				.normal(1, Vec3f.POSITIVE_Y)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, new Vec3f(0.5f, 1, 1))
				.normal(2, Vec3f.POSITIVE_Y)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, new Vec3f(0.5f, 1, 0.5f))
				.normal(3, Vec3f.POSITIVE_Y)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();

		// bottom right
		subSprite = getSubSprite(subSpriteEntry.BottomRight(), blankSprite, borderSprite, 4, 4);

		quadEmitter
				.colorIndex(1)
				.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
				.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find())
				.pos(0, new Vec3f(0.5f, 1, 0.5f))
				.normal(0, Vec3f.POSITIVE_Y)
				.sprite(0, 0, subSprite.minU(), subSprite.minV())
				.pos(1, new Vec3f(0.5f, 1, 1))
				.normal(1, Vec3f.POSITIVE_Y)
				.sprite(1, 0, subSprite.minU(), subSprite.maxV())
				.pos(2, new Vec3f(1, 1, 1))
				.normal(2, Vec3f.POSITIVE_Y)
				.sprite(2, 0, subSprite.maxU(), subSprite.maxV())
				.pos(3, new Vec3f(1, 1, 0.5f))
				.normal(3, Vec3f.POSITIVE_Y)
				.sprite(3, 0, subSprite.maxU(), subSprite.minV())
				.emit();
	}

	private static SubSprite getSubSprite(Point point, Sprite blankSprite, Sprite borderSprite, int columns, int rows)
	{
		var sprite = borderSprite;

		if (point.x() < 0)
		{
			sprite = blankSprite;
			point = new Point(-point.x(), -point.y());
		}

		var spriteWidth = sprite.getMaxU() - sprite.getMinU();
		var spriteHeight = sprite.getMaxV() - sprite.getMinV();

		var columnSpan = spriteWidth / columns;
		var rowSpan = spriteHeight / rows;

		return new SubSprite(sprite.getMinU() + point.x() * columnSpan, sprite.getMinV() + point.y() * rowSpan, sprite.getMinU() + (point.x() + 1) * columnSpan, sprite.getMinV() + (point.y() + 1) * rowSpan);
	}

	@Override
	protected Mesh createItemMesh(Matrix4f transformation)
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
		private final SpriteIdentifier sprite;
		private final SpriteIdentifier capSprite;

		public Unbaked(boolean hConnect, boolean vConnect, boolean lConnect, SpriteIdentifier sprite, SpriteIdentifier capSprite)
		{
			this.hConnect = hConnect;
			this.vConnect = vConnect;
			this.lConnect = lConnect;
			this.sprite = sprite;
			this.capSprite = capSprite;
			this.baker = spriteLoader -> new ConnectedTextureModel(hConnect, vConnect, lConnect, spriteLoader.apply(sprite), capSprite == null ? null : spriteLoader.apply(capSprite));
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
			return new Unbaked(hConnect, vConnect, lConnect, sprite, capSprite);
		}
	}
}
