package com.parzivail.pswg.client.model;

import com.mojang.datafixers.util.Pair;
import com.parzivail.pswg.util.ClientMathUtil;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class DebugModel extends SimpleModel
{
	public DebugModel(Sprite sprite)
	{
		super(sprite, ModelHelper.MODEL_TRANSFORM_BLOCK);
	}

	@Override
	protected Discriminator getDiscriminator()
	{
		return Discriminator.RENDER_SEED;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		MeshBuilder meshBuilder = RENDERER.meshBuilder();
		QuadEmitter quadEmitter = meshBuilder.getEmitter();

		quadEmitter.colorIndex(1).spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF).material(MAT_DIFFUSE_CUTOUT);

		Vector3f vA = new Vector3f(0, 0, 0);
		Vector3f vB = new Vector3f(0, 1, 0);
		Vector3f vC = new Vector3f(1, 1, 1);
		Vector3f vD = new Vector3f(1, 0, 1);

		Vector3f nA = new Vector3f(0, -1, 0);
		Vector3f nB = new Vector3f(0, -1, 0);
		Vector3f nC = new Vector3f(0, -1, 0);
		Vector3f nD = new Vector3f(0, -1, 0);

		Vector3f tA = new Vector3f(0, 0, 0);
		Vector3f tB = new Vector3f(1, 0, 0);
		Vector3f tC = new Vector3f(1, 1, 0);
		Vector3f tD = new Vector3f(0, 1, 0);

		vA = ClientMathUtil.transform(vA, transformation);
		vB = ClientMathUtil.transform(vB, transformation);
		vC = ClientMathUtil.transform(vC, transformation);
		vD = ClientMathUtil.transform(vD, transformation);

		quadEmitter.pos(0, vA).normal(0, nA).sprite(0, 0, tA.getX(), 1 - tA.getY());
		quadEmitter.pos(1, vB).normal(1, nB).sprite(1, 0, tB.getX(), 1 - tB.getY());
		quadEmitter.pos(2, vC).normal(2, nC).sprite(2, 0, tC.getX(), 1 - tC.getY());
		quadEmitter.pos(3, vD).normal(3, nD).sprite(3, 0, tD.getX(), 1 - tD.getY());

		quadEmitter.spriteBake(0, modelSprite, MutableQuadView.BAKE_NORMALIZED);

		quadEmitter.emit();

		return meshBuilder.build();
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
		private final Function<Function<SpriteIdentifier, Sprite>, DebugModel> baker;
		private DebugModel cachedBakedModel;
		private final SpriteIdentifier sprite;

		public Unbaked(SpriteIdentifier sprite)
		{
			this.sprite = sprite;
			this.baker = spriteIdentifierSpriteFunction -> new DebugModel(spriteIdentifierSpriteFunction.apply(sprite));
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

			DebugModel result = baker.apply(textureGetter);
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
