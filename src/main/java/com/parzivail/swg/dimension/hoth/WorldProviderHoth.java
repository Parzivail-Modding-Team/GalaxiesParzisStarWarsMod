package com.parzivail.swg.dimension.hoth;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.util.dimension.SingleBiomeChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderHoth extends PlanetWorldProvider
{
	public WorldProviderHoth()
	{
		super(StarWarsGalaxy.config.getDimIdHoth(), new SingleBiomeChunkGenerator(WorldRegister.biomeHothCrags, 0));
	}

	@Override
	public IChunkProvider createChunkProvider()
	{
		return new ChunkProviderHoth(worldObj, 0);
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
