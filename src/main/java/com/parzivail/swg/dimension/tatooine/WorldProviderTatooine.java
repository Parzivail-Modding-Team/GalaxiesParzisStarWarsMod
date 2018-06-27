package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.dimension.SWGChunkManager;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.swg.render.sky.RenderSkyTatooine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderTatooine extends PlanetWorldProvider
{
	public WorldProviderTatooine()
	{
		super(Resources.dimIdTatooine, new SWGChunkManager(WorldRegister.biomeTatooineDunes, 0));
	}

	@Override
	public IChunkProvider createChunkProvider()
	{
		return new ChunkProviderTatooine(worldObj, 0);
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