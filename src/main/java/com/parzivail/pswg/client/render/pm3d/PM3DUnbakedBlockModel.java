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

package com.parzivail.pswg.client.render.pm3d;

import com.mojang.datafixers.util.Pair;
import com.parzivail.util.client.model.ClonableUnbakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * Can be used for multiple blocks - will return same baked model for each
 */
public class PM3DUnbakedBlockModel extends ClonableUnbakedModel
{
	private final Identifier baseTexture;
	private final Identifier particleTexture;
	private final Function<Function<SpriteIdentifier, Sprite>, PM3DBakedBlockModel> baker;
	private PM3DBakedBlockModel cachedBakedModel = null;

	public PM3DUnbakedBlockModel(Identifier baseTexture, Identifier particleTexture, Function<Function<SpriteIdentifier, Sprite>, PM3DBakedBlockModel> baker)
	{
		this.baseTexture = baseTexture;
		this.particleTexture = particleTexture;
		this.baker = baker;
	}

	public ClonableUnbakedModel copy()
	{
		return new PM3DUnbakedBlockModel(baseTexture, particleTexture, baker);
	}

	@Override
	public Collection<Identifier> getModelDependencies()
	{
		return Collections.emptyList();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> errors)
	{
		var ids = new ArrayList<SpriteIdentifier>();

		ids.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, baseTexture));
		if (!baseTexture.equals(particleTexture))
			ids.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, particleTexture));

		return ids;
	}

	@Override
	@Nullable
	public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> spriteLoader, ModelBakeSettings modelBakeSettings, Identifier identifier)
	{
		if (cachedBakedModel != null)
			return cachedBakedModel;

		var result = baker.apply(spriteLoader);
		cachedBakedModel = result;
		return result;
	}
}
