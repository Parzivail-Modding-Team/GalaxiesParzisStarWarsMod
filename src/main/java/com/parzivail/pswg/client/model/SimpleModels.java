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

import com.parzivail.pswg.client.pm3d.PM3DUnbakedBlockModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public enum SimpleModels implements ModelVariantProvider
{
	INSTANCE;

	private static final HashMap<ModelIdentifier, UnbakedModel> models = new HashMap<>();

	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context)
	{
		return models.get(modelId);
	}

	public static void register(Block block, PM3DUnbakedBlockModel unbakedModel)
	{
		Identifier blockId = Registry.BLOCK.getId(block);
		models.put(new ModelIdentifier(blockId, "inventory"), new PM3DUnbakedBlockModel(unbakedModel));
		for (BlockState state : block.getStateManager().getStates())
		{
			ModelIdentifier id = new ModelIdentifier(blockId, BlockModels.propertyMapToString(state.getEntries()));
			models.put(id, new PM3DUnbakedBlockModel(unbakedModel));
		}
	}

	/**
	 * Prevents pinholes or similar artifacts along texture seams by nudging all
	 * texture coordinates slightly towards the vertex centroid of the UV
	 * coordinates.
	 */
	public static void contractUVs(int spriteIndex, Sprite sprite, MutableQuadView poly)
	{
		final float uPixels = sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
		final float vPixels = sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
		final float nudge = 4.0f / Math.max(vPixels, uPixels);

		final float u0 = poly.spriteU(0, spriteIndex);
		final float u1 = poly.spriteU(1, spriteIndex);
		final float u2 = poly.spriteU(2, spriteIndex);
		final float u3 = poly.spriteU(3, spriteIndex);

		final float v0 = poly.spriteV(0, spriteIndex);
		final float v1 = poly.spriteV(1, spriteIndex);
		final float v2 = poly.spriteV(2, spriteIndex);
		final float v3 = poly.spriteV(3, spriteIndex);

		final float uCenter = (u0 + u1 + u2 + u3) * 0.25F;
		final float vCenter = (v0 + v1 + v2 + v3) * 0.25F;

		poly.sprite(0, spriteIndex, MathHelper.lerp(nudge, u0, uCenter), MathHelper.lerp(nudge, v0, vCenter));
		poly.sprite(1, spriteIndex, MathHelper.lerp(nudge, u1, uCenter), MathHelper.lerp(nudge, v1, vCenter));
		poly.sprite(2, spriteIndex, MathHelper.lerp(nudge, u2, uCenter), MathHelper.lerp(nudge, v2, vCenter));
		poly.sprite(3, spriteIndex, MathHelper.lerp(nudge, u3, uCenter), MathHelper.lerp(nudge, v3, vCenter));
	}

	public static void emitBakedModelToMesh(BlockState blockState, BakedModel model, QuadEmitter qe)
	{
		final Random random = new Random();

		for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++)
		{
			final Direction cullFace = ModelHelper.faceFromIndex(i);
			random.setSeed(42);
			final List<BakedQuad> quads = model.getQuads(blockState, cullFace, random);

			if (quads.isEmpty())
			{
				continue;
			}

			for (final BakedQuad q : quads)
			{
				qe.fromVanilla(q.getVertexData(), 0, false);
				qe.cullFace(cullFace);
				qe.nominalFace(q.getFace());
				qe.colorIndex(q.getColorIndex());
				qe.emit();
			}
		}
	}
}
