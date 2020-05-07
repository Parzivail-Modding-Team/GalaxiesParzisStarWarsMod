package com.parzivail.pswg.dimension;

import com.parzivail.pswg.container.SwgDimensions;
import com.parzivail.pswg.dimension.tatooine.ChunkGeneratorTatooine;
import com.parzivail.pswg.dimension.tatooine.TerrainTatooineCanyons;
import com.parzivail.util.world.MultiCompositeTerrain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import javax.annotation.Nullable;
import java.util.Random;

public class TatooineDimension extends Dimension
{
	public TatooineDimension(World world, DimensionType type)
	{
		super(world, type, 0); // TODO: what's 0 here?
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		FixedBiomeSourceConfig biomeConfig = BiomeSourceType.FIXED.getConfig(world.getLevelProperties()).setBiome(SwgDimensions.Tatooine.BIOME);
		return new ChunkGeneratorTatooine(world, BiomeSourceType.FIXED.applyConfig(biomeConfig), new MultiCompositeTerrain(0, 800, new TerrainTatooineCanyons()));
	}

	@Nullable
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity)
	{
		Random random = new Random(this.world.getSeed());
		BlockPos blockPos = new BlockPos(chunkPos.getStartX() + random.nextInt(15), 0, chunkPos.getEndZ() + random.nextInt(15));
		return this.world.getTopNonAirState(blockPos).getMaterial().blocksMovement() ? blockPos : null;
	}

	@Nullable
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity)
	{
		return this.getSpawningBlockInChunk(new ChunkPos(x >> 4, z >> 4), checkMobSpawnValidity);
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta)
	{
		double d = MathHelper.fractionalPart((double)timeOfDay / 24000.0D - 0.25D);
		double e = 0.5D - Math.cos(d * 3.141592653589793D) / 2.0D;
		return (float)(d * 2.0D + e) / 3.0F;
	}

	@Override
	public boolean hasVisibleSky()
	{
		return true; // TODO
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta)
	{
		float f = MathHelper.cos(skyAngle * 6.2831855F) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float g = 0.7529412F;
		float h = 0.84705883F;
		float i = 1.0F;
		g *= f * 0.94F + 0.06F;
		h *= f * 0.94F + 0.06F;
		i *= f * 0.91F + 0.09F;
		return new Vec3d(g, h, i);
	}

	@Override
	public boolean canPlayersSleep()
	{
		return true; // TODO
	}

	@Override
	public boolean isFogThick(int x, int z)
	{
		return false; // TODO
	}

	@Override
	public DimensionType getType()
	{
		return SwgDimensions.Tatooine.DIMENSION_TYPE;
	}
}
