package dev.pswg.rendering.models;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

public final class GqbSingleVariantUnbaked implements BlockStateModel.Unbaked
{
	private final Identifier modelId;
	private final Variant.SimpleModelState modelState;
	private final GalaxiesModelBakery.GQuadGeometry geometry;

	public GqbSingleVariantUnbaked(Identifier modelId, Variant.SimpleModelState modelState, GalaxiesModelBakery.GQuadGeometry geometry)
	{
		this.modelId = modelId;
		this.modelState = modelState;
		this.geometry = geometry;
	}

	@Override
	public BlockStateModel bake(ModelBaker baker)
	{
		ResolvedModel model = baker.getModel(modelId);
		var textureSlots = model.getTopTextureSlots();

		QuadCollection quads = geometry.bake(textureSlots, baker, modelState.asModelState(), model);
		Material.Baked particleMaterial = model.resolveParticleMaterial(textureSlots, baker);

		BlockStateModelPart part = new GqbBlockModelPart(
				quads,
				particleMaterial,
				model.getTopAmbientOcclusion()
		);

		return new SingleVariant(part);
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver)
	{
		resolver.markDependency(modelId);
	}
}
