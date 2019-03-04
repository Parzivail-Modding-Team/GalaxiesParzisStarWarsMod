package com.parzivail.swg.dimension.hyperspace;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.PlanetWorldProvider;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.swg.render.sky.RenderSkyHyperspace;
import com.parzivail.util.dimension.SingleBiomeChunkGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderHyperspace extends PlanetWorldProvider
{
	public WorldProviderHyperspace()
	{
		super(StarWarsGalaxy.config.getDimIdHyperspace(), new SingleBiomeChunkGenerator(WorldRegister.biomeHyperspace, 0));
	}

	@Override
	public IChunkProvider createChunkProvider()
	{
		return new ChunkProviderHyperspace(worldObj, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		if (skyRenderer == null)
			skyRenderer = new RenderSkyHyperspace();
		return skyRenderer;
	}
}
