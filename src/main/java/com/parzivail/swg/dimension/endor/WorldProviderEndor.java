package com.parzivail.swg.dimension.endor;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.util.dimension.SingleBiomeChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderEndor extends PlanetWorldProvider
{
	public WorldProviderEndor()
	{
		super(StarWarsGalaxy.config.getDimIdEndor(), new SingleBiomeChunkGenerator(WorldRegister.biomeEndor, 0));
	}

	@Override
	public IChunkProvider createChunkProvider()
	{
		return new ChunkProviderEndor(worldObj, 0);
	}

	//	@Override
	//	@SideOnly(Side.CLIENT)
	//	public IRenderHandler getSkyRenderer()
	//	{
	//		if (this.skyRenderer == null)
	//			this.skyRenderer = new RenderSkyTatooine();
	//		return this.skyRenderer;
	//	}
}
