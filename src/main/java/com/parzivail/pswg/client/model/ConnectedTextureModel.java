package com.parzivail.pswg.client.model;

import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.client.ConnectedTextureHelper;
import com.parzivail.util.client.SubSprite;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.math.Point;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConnectedTextureModel extends DynamicBakedModel
{
	public ConnectedTextureModel(Sprite sprite)
	{
		super(sprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
	}

	@Override
	protected Discriminator getDiscriminator()
	{
		// TODO: change from debug value
		return Discriminator.BLOCKSTATE;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		MeshBuilder meshBuilder = RENDERER.meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(MAT_DIFFUSE_CUTOUT);

		final Random random = randomSupplier == null ? new Random(42) : randomSupplier.get();

		BlockModels blockModels = Client.minecraft.getBlockRenderManager().getModels();
		BakedModel model = blockModels.getModel(SwgBlocks.Panel.LabWall.getDefaultState());

		for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++)
		{
			final Direction cullFace = ModelHelper.faceFromIndex(i);
			final BooleanProperty facingProp = ConnectingBlock.FACING_PROPERTIES.get(cullFace);

			if (state != null && state.getBlock() instanceof ConnectingBlock && state.contains(facingProp) && state.get(facingProp))
				continue;

			final List<BakedQuad> quads = model.getQuads(state, cullFace, random);

			if (quads.isEmpty())
				continue;

			for (final BakedQuad q : quads)
			{
				quadEmitter.fromVanilla(q.getVertexData(), 0, false);
				quadEmitter.cullFace(cullFace);
				quadEmitter.nominalFace(q.getFace());
				quadEmitter.colorIndex(q.getColorIndex());

				Point subSpritePoint = ConnectedTextureHelper.getConnectedBlockTexture(blockView, state, pos, cullFace, (self, other) -> other.isOf(self.getBlock()));

				SubSprite subSprite = getSubSprite(modelSprite, 5, 5, subSpritePoint.x, subSpritePoint.y);

				quadEmitter.sprite(0, 0, subSprite.minU, subSprite.minV);
				quadEmitter.sprite(1, 0, subSprite.minU, subSprite.maxV);
				quadEmitter.sprite(2, 0, subSprite.maxU, subSprite.maxV);
				quadEmitter.sprite(3, 0, subSprite.maxU, subSprite.minV);
				quadEmitter.emit();
			}
		}

		return meshBuilder.build();
	}

	private static SubSprite getSubSprite(Sprite sprite, int columns, int rows, int x, int y)
	{
		float spriteWidth = sprite.getMaxU() - sprite.getMinU();
		float spriteHeight = sprite.getMaxV() - sprite.getMinV();

		float columnSpan = spriteWidth / columns;
		float rowSpan = spriteHeight / rows;

		return new SubSprite(sprite.getMinU() + x * columnSpan, sprite.getMinV() + y * rowSpan, sprite.getMinU() + (x + 1) * columnSpan, sprite.getMinV() + (y + 1) * rowSpan);
	}

	@Override
	protected Mesh createItemMesh(Matrix4f transformation)
	{
		return createBlockMesh(null, null, null, null, null, transformation);
	}

	@Override
	protected Matrix4f createTransformation(BlockState state)
	{
		Matrix4f m = new Matrix4f();
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
		private final SpriteIdentifier sprite;

		public Unbaked(SpriteIdentifier sprite)
		{
			this.sprite = sprite;
			this.baker = spriteIdentifierSpriteFunction -> new ConnectedTextureModel(spriteIdentifierSpriteFunction.apply(sprite));
		}

		@Override
		public Collection<Identifier> getModelDependencies()
		{
			return Collections.emptyList();
		}

		@Override
		public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
		{
			ArrayList<SpriteIdentifier> ids = new ArrayList<>();

			ids.add(sprite);

			return ids;
		}

		@Override
		public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
		{
			if (cachedBakedModel != null)
				return cachedBakedModel;

			ConnectedTextureModel result = baker.apply(textureGetter);
			cachedBakedModel = result;
			return result;
		}

		@Override
		public ClonableUnbakedModel copy()
		{
			return new Unbaked(sprite);
		}
	}
}
