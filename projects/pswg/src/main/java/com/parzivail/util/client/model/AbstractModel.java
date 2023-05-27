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

import com.parzivail.pswg.Galaxies;
import com.parzivail.util.client.model.compat.FrapiCompat;
import com.parzivail.util.client.model.compat.IndiumCompat;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

import java.util.Optional;

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
		return Optional
				.ofNullable(RendererAccess.INSTANCE.getRenderer()).map(Renderer::materialFinder)
				.or(IndiumCompat::getMaterialFinder)
				.or(() -> {
					Galaxies.LOG.warn("No MaterialFinder found in Fabric API or Indium!");
					return FrapiCompat.getMaterialFinder82();
				})
				// TODO: remove in 1.20, not present in 1.20.x FAPI builds
				.or(FrapiCompat::getMaterialFinderLegacy)
				.orElseThrow(() -> new RuntimeException("No MaterialFinder found in Fabric API, Indium, or Fabric (impl)!"));
	}

	protected static MeshBuilder createMeshBuilder()
	{
		return Optional
				.ofNullable(RendererAccess.INSTANCE.getRenderer()).map(Renderer::meshBuilder)
				.or(IndiumCompat::getMeshBuilder)
				.or(() -> {
					Galaxies.LOG.warn("No MeshBuilder found in Fabric API or Indium!");
					return FrapiCompat.getMeshBuilder();
				})
				.orElseThrow(() -> new RuntimeException("No MeshBuilder found in Fabric API, Indium, or Fabric (impl)!"));
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
