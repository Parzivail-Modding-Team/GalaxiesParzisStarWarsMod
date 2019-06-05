package com.parzivail.util.jsonpipeline;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

public class BuiltinWrapper implements IBakedModel
{
	private final IBakedModel baked;

	public BuiltinWrapper(IBakedModel baked)
	{
		this.baked = baked;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	{
		return baked.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return baked.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return baked.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return baked.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return baked.getOverrides();
	}
}
