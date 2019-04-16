package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.dimension.SingleBiomeProvider;
import com.parzivail.swg.register.WorldRegister;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderTatooine extends PlanetWorldProvider
{
	public WorldProviderTatooine()
	{
		super(WorldRegister.tatooineDimension, new SingleBiomeProvider(WorldRegister.biomeTatooineDunes, 0));
	}

	@Override
	public IChunkGenerator createChunkProvider()
	{
		return new ChunkProviderTatooine(world);
	}

	//	@Override
	//	@SideOnly(Side.CLIENT)
	//	public IRenderHandler getSkyRenderer()
	//	{
	//		if (skyRenderer == null)
	//			skyRenderer = new RenderSkyTatooine();
	//		return skyRenderer;
	//	}
}
