package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.register.WorldRegister;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderTatooine extends PlanetWorldProvider
{
	public WorldProviderTatooine()
	{
		super(WorldRegister.tatooineDimension, new BiomeProviderSingle(WorldRegister.biomeTatooineDunes));
	}

	@Override
	public IChunkGenerator createChunkProvider()
	{
		return new ChunkProviderTatooine(world);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		if (skyRenderer == null)
			skyRenderer = new RenderSkyTatooine();
		return skyRenderer;
	}
}
