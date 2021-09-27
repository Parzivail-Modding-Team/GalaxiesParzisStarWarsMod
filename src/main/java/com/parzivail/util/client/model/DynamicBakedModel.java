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

package com.parzivail.util.client.model;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Supplier;

/**
 * Simple baked model supporting the Fabric Render API features.
 */
public abstract class DynamicBakedModel extends AbstractModel
{
	public enum Discriminator
	{
		NONE,
		GLOBAL,
		BLOCKSTATE,
		RENDER_SEED
	}

	// These are dirty hacks to at least have some semblance of "working" models when
	// a rendering mod replaces Indigo without implementing FRAPI. I hate them, and
	// they only work because AbstractBlockState::getModelOffset is called before each
	// block is rendered.
	// TODO: figure out a better way to do this
	private static final ThreadLocal<BlockView> lastQueriedWorld = new ThreadLocal<>();
	private static final ThreadLocal<BlockPos> lastQueriedPos = new ThreadLocal<>();

	protected final ItemProxy itemProxy = new ItemProxy();
	protected final HashMap<ModelCacheId, Mesh> meshes = new HashMap<>();
	protected WeakReference<List<BakedQuad>[]> quadLists = null;

	public DynamicBakedModel(Sprite sprite, ModelTransformation transformation)
	{
		super(sprite, transformation);
	}

	public static void cacheBlockPosQuery(BlockView world, BlockPos pos)
	{
		lastQueriedWorld.set(world);
		lastQueriedPos.set(pos);
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
		var cacheDiscriminator = switch (getDiscriminator())
				{
					case GLOBAL -> BlockPos.ORIGIN;
					case BLOCKSTATE -> state;
					case RENDER_SEED -> state == null ? 0 : state.getRenderingSeed(pos);
					default -> null;
				};

		if (cacheDiscriminator == null)
			return createBlockMesh(blockView, state, pos, randomSupplier, context, transformation);

		var cacheId = new ModelCacheId(cacheDiscriminator, transformation);

		if (meshes.containsKey(cacheId))
			return meshes.get(cacheId);

		var m = createBlockMesh(blockView, state, pos, randomSupplier, context, transformation);
		meshes.put(cacheId, m);

		return m;
	}

	protected Mesh createOrCacheItemMesh(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var cacheId = new ModelCacheId(null, transformation);

		if (meshes.containsKey(cacheId))
			return meshes.get(cacheId);

		var m = createItemMesh(transformation);
		meshes.put(cacheId, m);

		return m;
	}

	protected abstract Matrix4f createTransformation(BlockState state);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random rand)
	{
		var lists = quadLists == null ? null : quadLists.get();
		if (lists == null)
		{
			var world = lastQueriedWorld.get() instanceof BlockRenderView brv ? brv : null;
			var pos = lastQueriedPos.get();
			if (pos == null)
				pos = BlockPos.ORIGIN;

			lists = ModelHelper.toQuadLists(createOrCacheBlockMesh(world, state, pos, () -> rand, null, createTransformation(state)));
			quadLists = new WeakReference<>(lists);
		}
		final var result = lists[face == null ? 6 : face.getId()];
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
		public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed)
		{
			return DynamicBakedModel.this;
		}
	}

	private record ModelCacheId(Object discriminator, Matrix4f transformation)
	{
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
			if (!(o instanceof ModelCacheId cacheId))
				return false;

			return Objects.equals(discriminator, cacheId.discriminator) && Objects.equals(transformation, cacheId.transformation);
		}
	}
}
