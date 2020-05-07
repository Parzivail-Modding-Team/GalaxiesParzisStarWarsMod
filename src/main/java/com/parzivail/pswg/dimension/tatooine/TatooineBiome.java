package com.parzivail.pswg.dimension.tatooine;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class TatooineBiome extends Biome
{
	public TatooineBiome()
	{
		super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.NOPE, new TernarySurfaceConfig(SwgBlocks.Sand.Tatooine.getDefaultState(), Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState())).precipitation(Biome.Precipitation.NONE).category(Biome.Category.DESERT).depth(0.125F).scale(0.05F).temperature(2.0F).downfall(0.0F).waterColor(4159204).waterFogColor(329011).parent(null));
	}
}
