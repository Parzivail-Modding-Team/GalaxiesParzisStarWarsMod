package com.parzivail.swg.dimension.tatooine;

import com.google.common.collect.ImmutableList;
import com.parzivail.swg.dimension.tatooine.terrain.TerrainTatooineCanyons;
import com.parzivail.swg.register.StructureRegister;
import com.parzivail.util.world.*;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;

public class ChunkProviderTatooine implements IChunkGenerator
{
	public static final ITerrainHeightmap terrain = new MultiCompositeTerrain(0, 800, new TerrainTatooineCanyons(), new CompositeTerrain(new TerrainLayer(0, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Add, 300, 50), new TerrainLayer(1, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Multiply, 300, 4), new TerrainLayer(2, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 400, 25), new TerrainLayer(3, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 50, 30), new TerrainLayer(4, TerrainFunction.InvNCTurbulent, TerrainCompositeMethod.Multiply, 100, 0.15)), new CompositeTerrain(new TerrainLayer(0, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Add, 150, 10), new TerrainLayer(1, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Multiply, 150, 5), new TerrainLayer(2, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 100, 20), new TerrainLayer(3, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 100, 20), new TerrainLayer(4, TerrainFunction.InvNCTurbulent, TerrainCompositeMethod.Multiply, 40, 0.5)), new CompositeTerrain(new TerrainLayer(0, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Add, 300, 10), new TerrainLayer(1, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Multiply, 300, 5), new TerrainLayer(2, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 400, 25), new TerrainLayer(3, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 50, 25), new TerrainLayer(4, TerrainFunction.InvNCTurbulent, TerrainCompositeMethod.Multiply, 70, 0.8)), new CompositeTerrain(new TerrainLayer(0, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Add, 300, 50), new TerrainLayer(1, TerrainFunction.NCTurbulent, TerrainCompositeMethod.Multiply, 300, 4), new TerrainLayer(2, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 400, 25), new TerrainLayer(3, TerrainFunction.Simplex, TerrainCompositeMethod.Add, 50, 25), new TerrainLayer(4, TerrainFunction.InvNCTurbulent, TerrainCompositeMethod.Multiply, 100, 0.8)));
	private final World worldObj;

	public ChunkProviderTatooine(World worldObj)
	{
		this.worldObj = worldObj;
	}

	@Override
	public Chunk generateChunk(int cx, int cz)
	{
		ChunkPrimer chunk = new ChunkPrimer();
		for (short x = 0; x < 16; x++)
		{
			for (short z = 0; z < 16; z++)
			{
				double height = terrain.getHeightAt((cx * 16 + x), (cz * 16 + z)) + 60;
				int finalHeight = (int)height;

				chunk.setBlockState(x, 0, z, Blocks.BEDROCK.getDefaultState());

				for (short y = 1; y <= finalHeight; y++)
				{
					double sandThreshold = height * 0.95;
					double sandstoneThreshold = height * 0.85;

					if (y >= sandThreshold)
						chunk.setBlockState(x, y, z, Blocks.SAND.getDefaultState());
					else if (y >= sandstoneThreshold && y < sandThreshold)
						chunk.setBlockState(x, y, z, Blocks.SANDSTONE.getDefaultState());
					else
						chunk.setBlockState(x, y, z, Blocks.STONE.getDefaultState());
				}
			}
		}

		StructureRegister.structureEngine.genStructure(worldObj.provider.getDimension(), cx, cz, chunk);

		Chunk realChunk = new Chunk(this.worldObj, chunk, cx, cz);
		Biome[] abiome = this.worldObj.getBiomeProvider().getBiomes(null, cx * 16, cz * 16, 16, 16);
		byte[] abyte = realChunk.getBiomeArray();

		for (int l = 0; l < abyte.length; ++l)
		{
			abyte[l] = (byte)Biome.getIdForBiome(abiome[l]);
		}

		realChunk.generateSkylightMap();
		return realChunk;
	}

	@Override
	public void populate(int x, int z)
	{

	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z)
	{
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return ImmutableList.of();
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z)
	{

	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
	{
		return false;
	}
}
