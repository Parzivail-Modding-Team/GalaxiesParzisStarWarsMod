/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.parzivail.pswg.client.model;

import com.google.common.collect.ImmutableList;
import com.parzivail.pswg.Resources;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Supplier;

/**
 * Simple baked model supporting the Fabric Render API features.<p>
 */
public abstract class SimpleModel extends AbstractModel
{
	public enum Discriminator
	{
		GLOBAL,
		BLOCKSTATE,
		RENDER_SEED
	}

	protected final ItemProxy itemProxy = new ItemProxy();
	protected HashMap<ModelCacheId, Mesh> meshes = new HashMap<>();
	protected WeakReference<List<BakedQuad>[]> quadLists = null;

	public SimpleModel(Sprite sprite, ModelTransformation transformation)
	{
		super(sprite, transformation);
	}

	@Override
	public boolean isVanillaAdapter()
	{
		return false;
	}

	protected Discriminator getDiscriminator()
	{
		return Discriminator.GLOBAL;
	}

	protected abstract Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation);

	protected abstract Mesh createItemMesh(Matrix4f transformation);

	protected Mesh createOrCacheBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		Object cacheDiscriminator;

		switch (getDiscriminator())
		{
			case GLOBAL:
				cacheDiscriminator = BlockPos.ORIGIN;
				break;
			case BLOCKSTATE:
				cacheDiscriminator = state;
				break;
			case RENDER_SEED:
				cacheDiscriminator = state.getRenderingSeed(pos);
				break;
			default:
				cacheDiscriminator = null;
				break;
		}

		ModelCacheId cacheId = new ModelCacheId(cacheDiscriminator, transformation);

		if (meshes.containsKey(cacheId))
			return meshes.get(cacheId);

		Mesh m = createBlockMesh(blockView, state, pos, randomSupplier, context, transformation);
		meshes.put(cacheId, m);

		return m;
	}

	protected Mesh createOrCacheItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		ModelCacheId cacheId = new ModelCacheId(null, transformation);

		if (meshes.containsKey(cacheId))
			return meshes.get(cacheId);

		Mesh m = createItemMesh(transformation);
		meshes.put(cacheId, m);

		return m;
	}

	protected abstract Matrix4f createTransformation(BlockState state);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random rand)
	{
		List<BakedQuad>[] lists = quadLists == null ? null : quadLists.get();
		if (lists == null)
		{
			lists = ModelHelper.toQuadLists(createOrCacheBlockMesh(null, state, BlockPos.ORIGIN, () -> Resources.RANDOM, null, createTransformation(state)));
			quadLists = new WeakReference<>(lists);
		}
		final List<BakedQuad> result = lists[face == null ? 6 : face.getId()];
		return result == null ? ImmutableList.of() : result;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(createOrCacheBlockMesh(blockView, state, pos, randomSupplier, context, createTransformation(state)));
	}

	@Override
	public ModelOverrideList getOverrides()
	{
		return itemProxy;
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(createOrCacheItemMesh(stack, randomSupplier, context, createTransformation(null)));
	}

	protected class ItemProxy extends ModelOverrideList
	{
		public ItemProxy()
		{
			super(null, null, null, Collections.emptyList());
		}

		@Override
		public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity)
		{
			return SimpleModel.this;
		}
	}

	private static class ModelCacheId
	{
		private final Object discriminator;
		private final Matrix4f transformation;

		public ModelCacheId(Object discriminator, Matrix4f transformation)
		{
			this.discriminator = discriminator;
			this.transformation = transformation;
		}

		@Override
		public int hashCode()
		{
			if (discriminator == null)
				return transformation.hashCode() * 31;

			return (transformation.hashCode() * 31) + discriminator.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (!(o instanceof ModelCacheId))
				return false;

			ModelCacheId cacheId = (ModelCacheId)o;

			return Objects.equals(discriminator, cacheId.discriminator) && Objects.equals(transformation, cacheId.transformation);
		}
	}
}
