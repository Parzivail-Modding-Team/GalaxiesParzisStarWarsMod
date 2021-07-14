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

import com.parzivail.pswg.client.model.ConnectedTextureModel;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Random;

public enum ModelRegistry implements ModelVariantProvider
{
	INSTANCE;

	private static final HashMap<ModelIdentifier, UnbakedModel> models = new HashMap<>();

	public static void registerConnected(ConnectingBlock block)
	{
		registerConnected(block, true, true, true, null);
	}

	public static void registerConnected(ConnectingBlock block, boolean hConnect, boolean vConnect, boolean lConnect, Identifier capTexture)
	{
		var id = Registry.BLOCK.getId(block);
		register(block, true, new ConnectedTextureModel.Unbaked(
				hConnect, vConnect, lConnect,
				new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(id.getNamespace(), "block/" + id.getPath())),
				capTexture == null ? null : new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, capTexture)
		));
	}

	public static void register(Block block, boolean registerInventoryModel, ClonableUnbakedModel unbakedModel)
	{
		var blockId = Registry.BLOCK.getId(block);
		if (registerInventoryModel)
			models.put(new ModelIdentifier(blockId, "inventory"), unbakedModel.copy());
		for (var state : block.getStateManager().getStates())
		{
			var id = new ModelIdentifier(blockId, BlockModels.propertyMapToString(state.getEntries()));
			models.put(id, unbakedModel.copy());
		}
	}

	/**
	 * Prevents pinholes or similar artifacts along texture seams by nudging all
	 * texture coordinates slightly towards the vertex centroid of the UV
	 * coordinates.
	 */
	public static void contractUVs(int spriteIndex, Sprite sprite, MutableQuadView poly)
	{
		final var uPixels = sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
		final var vPixels = sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
		final var nudge = 4.0f / Math.max(vPixels, uPixels);

		final var u0 = poly.spriteU(0, spriteIndex);
		final var u1 = poly.spriteU(1, spriteIndex);
		final var u2 = poly.spriteU(2, spriteIndex);
		final var u3 = poly.spriteU(3, spriteIndex);

		final var v0 = poly.spriteV(0, spriteIndex);
		final var v1 = poly.spriteV(1, spriteIndex);
		final var v2 = poly.spriteV(2, spriteIndex);
		final var v3 = poly.spriteV(3, spriteIndex);

		final var uCenter = (u0 + u1 + u2 + u3) * 0.25F;
		final var vCenter = (v0 + v1 + v2 + v3) * 0.25F;

		poly.sprite(0, spriteIndex, MathHelper.lerp(nudge, u0, uCenter), MathHelper.lerp(nudge, v0, vCenter));
		poly.sprite(1, spriteIndex, MathHelper.lerp(nudge, u1, uCenter), MathHelper.lerp(nudge, v1, vCenter));
		poly.sprite(2, spriteIndex, MathHelper.lerp(nudge, u2, uCenter), MathHelper.lerp(nudge, v2, vCenter));
		poly.sprite(3, spriteIndex, MathHelper.lerp(nudge, u3, uCenter), MathHelper.lerp(nudge, v3, vCenter));
	}

	public static void emitBakedModelToMesh(BlockState blockState, BakedModel model, QuadEmitter qe)
	{
		final var random = new Random();

		for (var i = 0; i <= ModelHelper.NULL_FACE_ID; i++)
		{
			final var cullFace = ModelHelper.faceFromIndex(i);
			random.setSeed(42);
			final var quads = model.getQuads(blockState, cullFace, random);

			if (quads.isEmpty())
			{
				continue;
			}

			for (final var q : quads)
			{
				qe.fromVanilla(q.getVertexData(), 0, false);
				qe.cullFace(cullFace);
				qe.nominalFace(q.getFace());
				qe.colorIndex(q.getColorIndex());
				qe.emit();
			}
		}
	}

	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context)
	{
		return models.get(modelId);
	}
}
