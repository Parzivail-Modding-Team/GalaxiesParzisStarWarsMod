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

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

public abstract class AbstractModel implements BakedModel, FabricBakedModel
{
	protected static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();

	protected final RenderMaterial MAT_DIFFUSE_OPAQUE = RENDERER.materialFinder().find();
	protected final RenderMaterial MAT_DIFFUSE_CUTOUT = RENDERER.materialFinder().blendMode(0, BlendMode.CUTOUT_MIPPED).find();
	protected final RenderMaterial MAT_DIFFUSE_TRANSLUCENT = RENDERER.materialFinder().blendMode(0, BlendMode.TRANSLUCENT).find();
	protected final RenderMaterial MAT_EMISSIVE = RENDERER.materialFinder().emissive(0, true).disableAo(0, true).disableDiffuse(0, true).find();

	protected final Sprite modelSprite;
	protected final ModelTransformation transformation;

	protected AbstractModel(Sprite sprite, ModelTransformation transformation)
	{
		modelSprite = sprite;
		this.transformation = transformation;
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return true;
	}

	@Override
	public boolean isBuiltin()
	{
		return false;
	}

	@Override
	public Sprite getParticleSprite()
	{
		return modelSprite;
	}

	@Override
	public ModelTransformation getTransformation()
	{
		return transformation;
	}
}
