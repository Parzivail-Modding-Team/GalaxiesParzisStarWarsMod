package dev.pswg.rendering.models;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class GqbBlockModelPart implements BlockStateModelPart
{
	private final QuadCollection quads;
	private final Material.Baked particleMaterial;
	private final boolean ambientOcclusion;

	public GqbBlockModelPart(QuadCollection quads, Material.Baked particleMaterial, boolean ambientOcclusion)
	{
		this.quads = quads;
		this.particleMaterial = particleMaterial;
		this.ambientOcclusion = ambientOcclusion;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable Direction direction)
	{
		return quads.getAll();
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return ambientOcclusion;
	}

	@Override
	public Material.Baked particleMaterial()
	{
		return particleMaterial;
	}

	@Override
	public int materialFlags()
	{
		return quads.materialFlags();
	}
}