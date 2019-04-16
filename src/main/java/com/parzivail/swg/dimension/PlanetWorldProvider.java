package com.parzivail.swg.dimension;

import com.parzivail.swg.register.WorldRegister;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class PlanetWorldProvider extends WorldProvider
{
	private final DimensionType type;
	private final PlanetDescriptor planetDescriptor;

	@SideOnly(Side.CLIENT)
	protected IRenderHandler skyRenderer;

	public PlanetWorldProvider(DimensionType type, SingleBiomeProvider biomeProvider)
	{
		this.type = type;
		planetDescriptor = WorldRegister.planetDescriptorHashMap.get(type.getId());
		this.biomeProvider = biomeProvider;
	}

	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
	{
		if (planetDescriptor.rotationPeriod == 0)
			return 0;

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
		if (planetDescriptor.rotationPeriod == 0)
			return 0;
		return (int)(p_76559_1_ / (long)(planetDescriptor.rotationPeriod * 1000) % 8L + 8L) % 8;
	}

	@Override
	public DimensionType getDimensionType()
	{
		return type;
	}

	/**
	 * Returns a new chunk provider which generates chunks for this world
	 */
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return createChunkProvider();
	}

	public abstract IChunkGenerator createChunkProvider();

	/**
	 * Returns the dimension's id, e.g. "The End", "Nether", or "Overworld".
	 */
	@Override
	public String getSaveFolder()
	{
		return planetDescriptor.name;
	}

	@Nullable
	@Override
	public BlockPos getSpawnCoordinate()
	{
		return new BlockPos(0, world.getHeight(0, 0), 0);
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return false;
	}
}
