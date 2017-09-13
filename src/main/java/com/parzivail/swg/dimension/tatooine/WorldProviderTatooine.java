package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.Resources;
import com.parzivail.swg.dimension.SWGChunkManager;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.swg.render.sky.RenderSkyTatooine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

/**
 * Created by colby on 9/10/2017.
 */
public class WorldProviderTatooine extends WorldProvider
{
	public static String dimName = "Tatooine";

	@SideOnly(Side.CLIENT)
	private IRenderHandler skyRenderer;

	/**
	 * Returns a new chunk provider which generates chunks for this world
	 */
	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderTatooine(this.worldObj, /*this.getSeed()*/0);
	}

	/**
	 * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
	 */
	@Override
	public String getDimensionName()
	{
		return dimName;
	}

	@Override
	public ChunkCoordinates getEntrancePortalLocation()
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		if (this.skyRenderer == null)
			this.skyRenderer = new RenderSkyTatooine();
		return this.skyRenderer;
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{
		return new ChunkCoordinates(0, this.worldObj.getHeightValue(0, 0), 0);
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	/**
	 * creates a new world chunk manager for WorldProvider
	 */
	@Override
	protected void registerWorldChunkManager()
	{
		this.worldChunkMgr = new SWGChunkManager(WorldRegister.biomeTatooineDunes, 0.0F);
		this.dimensionId = Resources.dimIdTatooine;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return false;
	}
}