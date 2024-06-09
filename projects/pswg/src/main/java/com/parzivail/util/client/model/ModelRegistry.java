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

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.EnumSet;
import java.util.HashMap;

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
		var id = Registries.BLOCK.getId(block);
		registerConnected(
				block, hConnect, vConnect, lConnect, capTexture,
				id.withPrefixedPath("block/"),
				id.withPath(path -> "block/" + path + "_border")
		);
	}

	public static void registerConnectedLamp(ConnectingBlock block, boolean hConnect, boolean vConnect, boolean lConnect, Identifier capTexture)
	{
		var id = Registries.BLOCK.getId(block);
		var centerTexture = id.withPrefixedPath("block/");
		var borderTexture = id.withPath(path -> "block/" + path + "_border");
		var borderLitTexture = id.withPath(path -> "block/" + path + "_border_on");
		register(block, true, new ConnectedTextureModel.Unbaked(
				hConnect, vConnect, lConnect, EnumSet.of(Direction.UP, Direction.DOWN),
				new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, centerTexture),
				new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, borderTexture),
				capTexture == null ? null : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, capTexture),
				new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, borderLitTexture)
		));
	}

	public static void registerConnected(ConnectingBlock block, boolean hConnect, boolean vConnect, boolean lConnect, Identifier capTexture, Identifier centerTexture)
	{
		var id = Registries.BLOCK.getId(block);
		registerConnected(
				block, hConnect, vConnect, lConnect, capTexture,
				centerTexture,
				id.withPrefixedPath("block/")
		);
	}

	public static void registerConnected(ConnectingBlock block, boolean hConnect, boolean vConnect, boolean lConnect, Identifier capTexture, Identifier centerTexture, Identifier borderTexture)
	{
		registerConnected(block, hConnect, vConnect, lConnect, capTexture, centerTexture, borderTexture, EnumSet.of(Direction.UP, Direction.DOWN));
	}

	public static void registerConnected(ConnectingBlock block, boolean hConnect, boolean vConnect, boolean lConnect, Identifier capTexture, Identifier centerTexture, Identifier borderTexture, EnumSet<Direction> capDirections)
	{
		register(block, true, new ConnectedTextureModel.Unbaked(
				hConnect, vConnect, lConnect, capDirections,
				new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, centerTexture),
				new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, borderTexture),
				capTexture == null ? null : new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, capTexture)
		));
	}

	public static void register(Block block, boolean registerInventoryModel, ClonableUnbakedModel unbakedModel)
	{
		if (registerInventoryModel)
			registerInventory(block, unbakedModel);
		for (var state : block.getStateManager().getStates())
		{
			var id = new ModelIdentifier(Registries.BLOCK.getId(block), BlockModels.propertyMapToString(state.getEntries()));
			models.put(id, unbakedModel.copy());
		}
	}

	public static void registerInventory(Block block, ClonableUnbakedModel unbakedModel)
	{
		models.put(new ModelIdentifier(Registries.BLOCK.getId(block), "inventory"), unbakedModel.copy());
	}

	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context)
	{
		return models.get(modelId);
	}
}
