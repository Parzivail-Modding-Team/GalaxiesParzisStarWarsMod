package com.parzivail.swg.dimension.endor;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.dimension.SWGChunkManager;
import com.parzivail.swg.registry.WorldRegister;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderEndor extends PlanetWorldProvider
{
	public WorldProviderEndor()
	{
		super(Resources.dimIdEndor, new SWGChunkManager(WorldRegister.biomeEndor, 0));
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