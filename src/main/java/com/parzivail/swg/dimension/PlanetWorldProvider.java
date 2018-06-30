package com.parzivail.swg.dimension;

import com.parzivail.swg.registry.WorldRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

public abstract class PlanetWorldProvider extends WorldProvider
{
	private final PlanetDescriptor planetDescriptor;
	private final int dimId;
	private final SWGChunkManager chunkManager;

	@SideOnly(Side.CLIENT)
	protected IRenderHandler skyRenderer;

	public PlanetWorldProvider(int dimId, SWGChunkManager chunkManager)
	{
		planetDescriptor = WorldRegister.planetDescriptorHashMap.get(dimId);
		this.dimId = dimId;
		this.chunkManager = chunkManager;
	}

	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
	{
		int j = (int)(p_76563_1_ % (long)(planetDescriptor.rotationPeriod * 1000));
		float f1 = ((float)j + p_76563_3_) / (planetDescriptor.rotationPeriod * 1000) - 0.25F;

		if (f1 < 0.0F)
		{
			++f1;
		}

		if (f1 > 1.0F)
		{
			--f1;
		}

		float f2 = f1;
		f1 = 1.0F - (float)((Math.cos((double)f1 * Math.PI) + 1.0D) / 2.0D);
		f1 = f2 + (f1 - f2) / 3.0F;
		return f1;
	}

	public int getMoonPhase(long p_76559_1_)
	{
		return (int)(p_76559_1_ / (long)(planetDescriptor.rotationPeriod * 1000) % 8L + 8L) % 8;
	}

	/**
	 * Returns a new chunk provider which generates chunks for this world
	 */
	@Override
	public IChunkProvider createChunkGenerator()
	{
		return createChunkProvider();
	}

	public abstract IChunkProvider createChunkProvider();

	/**
	 * Returns the dimension's id, e.g. "The End", "Nether", or "Overworld".
	 */
	@Override
	public String getDimensionName()
	{
		return planetDescriptor.name;
	}

	@Override
	public ChunkCoordinates getEntrancePortalLocation()
	{
		return null;
	}

	@Override
	public ChunkCoordinates getSpawnPoint()
	{
		return new ChunkCoordinates(0, worldObj.getHeightValue(0, 0), 0);
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
		worldChunkMgr = chunkManager;
		dimensionId = dimId;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return false;
	}
}
