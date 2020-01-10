package com.parzivail.swg.render.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DummyBuiltinRenderer implements IModel
{
	private final String variant;

	public DummyBuiltinRenderer(String variant)
	{
		this.variant = variant;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		return new BakedDummyBuiltinRenderer();
	}

	private class BakedDummyBuiltinRenderer implements IBakedModel
	{
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
		{
			return new ArrayList<>();
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return false;
		}

		@Override
		public boolean isGui3d()
		{
			return false;
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return true;
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return null;
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return ItemOverrideList.NONE;
		}
	}
}
