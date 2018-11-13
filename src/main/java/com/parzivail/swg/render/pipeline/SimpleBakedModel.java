package com.parzivail.swg.render.pipeline;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SimpleBakedModel
{
	protected final List<BakedQuad> generalQuads;
	protected final Map<EnumFacing, List<BakedQuad>> faceQuads;
	protected final boolean ambientOcclusion;
	protected final boolean gui3d;

	public SimpleBakedModel(List<BakedQuad> generalQuadsIn, Map<EnumFacing, List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn)
	{
		generalQuads = generalQuadsIn;
		faceQuads = faceQuadsIn;
		ambientOcclusion = ambientOcclusionIn;
		gui3d = gui3dIn;
	}

	public List<BakedQuad> getQuads()
	{
		return generalQuads;
	}

	@SideOnly(Side.CLIENT)
	public static class Builder
	{
		private final List<BakedQuad> builderGeneralQuads;
		private final Map<EnumFacing, List<BakedQuad>> builderFaceQuads;
		private final boolean builderAmbientOcclusion;
		private final boolean builderGui3d;

		public Builder(ModelBlock model)
		{
			this(model.isAmbientOcclusion(), model.isGui3d());
		}

		private Builder(boolean ambientOcclusion, boolean gui3d)
		{
			builderGeneralQuads = Lists.newArrayList();
			builderFaceQuads = Maps.newEnumMap(EnumFacing.class);

			for (EnumFacing enumfacing : EnumFacing.values())
			{
				builderFaceQuads.put(enumfacing, Lists.newArrayList());
			}

			builderAmbientOcclusion = ambientOcclusion;
			builderGui3d = gui3d;
		}

		public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad)
		{
			(builderFaceQuads.get(facing)).add(quad);
			return this;
		}

		public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad)
		{
			builderGeneralQuads.add(quad);
			return this;
		}

		public SimpleBakedModel makeBakedModel()
		{
			return new SimpleBakedModel(builderGeneralQuads, builderFaceQuads, builderAmbientOcclusion, builderGui3d);
		}
	}
}
