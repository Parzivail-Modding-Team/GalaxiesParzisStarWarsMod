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
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Simple baked model supporting the Fabric Render API features.<p>
 */
public abstract class SimpleModel extends AbstractModel
{
	protected Mesh mesh = null;
	protected WeakReference<List<BakedQuad>[]> quadLists = null;
	protected final ItemProxy itemProxy = new ItemProxy();

	public SimpleModel(Sprite sprite, ModelTransformation transformation)
	{
		super(sprite, transformation);
	}

	@Override
	public boolean isVanillaAdapter()
	{
		return false;
	}

	protected abstract Mesh createMesh(Matrix4f transformation);

	protected Mesh mesh(Matrix4f transformation)
	{
		Mesh result = mesh;

		if (result == null)
		{
			result = createMesh(transformation);
			mesh = result;
		}

		return result;
	}

	protected abstract Matrix4f createTransformation(BlockState state);

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random rand)
	{
		List<BakedQuad>[] lists = quadLists == null ? null : quadLists.get();
		if (lists == null)
		{
			lists = ModelHelper.toQuadLists(mesh(createTransformation(state)));
			quadLists = new WeakReference<>(lists);
		}
		final List<BakedQuad> result = lists[face == null ? 6 : face.getId()];
		return result == null ? ImmutableList.of() : result;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(mesh(createTransformation(state)));
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides()
	{
		return itemProxy;
	}

	protected class ItemProxy extends ModelItemPropertyOverrideList
	{
		public ItemProxy()
		{
			super(null, null, null, Collections.emptyList());
		}

		@Override
		public BakedModel apply(BakedModel bakedModel_1, ItemStack itemStack_1, World world_1, LivingEntity livingEntity_1)
		{
			return SimpleModel.this;
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(mesh(createTransformation(null)));
	}
}
