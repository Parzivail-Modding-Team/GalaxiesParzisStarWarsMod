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

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshBuilderImpl;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

public abstract class AbstractModel implements BakedModel, FabricBakedModel
{
	public static final RenderMaterial MAT_DIFFUSE_OPAQUE;
	public static final RenderMaterial MAT_DIFFUSE_CUTOUT;
	public static final RenderMaterial MAT_DIFFUSE_TRANSLUCENT;
	public static final RenderMaterial MAT_EMISSIVE;

	protected final Sprite modelSprite;
	protected final ModelTransformation transformation;

	static
	{
		var materialFinder = createMaterialFinder();

		MAT_DIFFUSE_OPAQUE = materialFinder.find();
		MAT_DIFFUSE_CUTOUT = materialFinder.blendMode(0, BlendMode.CUTOUT_MIPPED).find();
		MAT_DIFFUSE_TRANSLUCENT = materialFinder.blendMode(0, BlendMode.TRANSLUCENT).find();
		MAT_EMISSIVE = materialFinder.emissive(0, true).disableAo(0, true).disableDiffuse(0, true).find();
	}

	protected AbstractModel(Sprite sprite, ModelTransformation transformation)
	{
		modelSprite = sprite;
		this.transformation = transformation;
	}

	protected static MaterialFinder createMaterialFinder()
	{
		var renderer = RendererAccess.INSTANCE.getRenderer();
		if (renderer == null)
			return new RenderMaterialImpl.Finder();
		else
			return renderer.materialFinder();
	}

	protected static MeshBuilder createMeshBuilder()
	{
		var renderer = RendererAccess.INSTANCE.getRenderer();
		if (renderer == null)
			return new MeshBuilderImpl();
		else
			return renderer.meshBuilder();
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
