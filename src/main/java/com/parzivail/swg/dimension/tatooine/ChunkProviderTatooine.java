package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.util.world.*;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.List;

/**
 * Created by colby on 9/10/2017.
 */
public class ChunkProviderTatooine implements IChunkProvider
{
	private World worldObj;
	private ITerrainHeightmap terrain;

	public ChunkProviderTatooine(World worldObj, long seed)
	{
		this.worldObj = worldObj;
		terrain = new MultiCompositeTerrain(seed, 800, new CompositeTerrain(new TerrainLayer(seed, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 50), new TerrainLayer(seed + 1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 4), new TerrainLayer(seed + 2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 25), new TerrainLayer(seed + 3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 30), new TerrainLayer(seed + 4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 100, 0.15)), new CompositeTerrain(new TerrainLayer(seed, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 150, 10), new TerrainLayer(seed + 1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 150, 5), new TerrainLayer(seed + 2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 100, 20), new TerrainLayer(seed + 3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 100, 20), new TerrainLayer(seed + 4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 40, 0.5)), new CompositeTerrain(new TerrainLayer(seed, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 10), new TerrainLayer(seed + 1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 5), new TerrainLayer(seed + 2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 25), new TerrainLayer(seed + 3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 25), new TerrainLayer(seed + 4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 70, 0.8)), new CompositeTerrain(new TerrainLayer(seed, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 50), new TerrainLayer(seed + 1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 4), new TerrainLayer(seed + 2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 25), new TerrainLayer(seed + 3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 25), new TerrainLayer(seed + 4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 100, 0.8)));
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int x, int z)
	{
		return this.provideChunk(x, z);
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
	 * specified chunk from the map seed and chunk seed
	 */
	public Chunk provideChunk(int cx, int cz)
	{
		Chunk chunk = new Chunk(this.worldObj, cx, cz);
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				double height = terrain.getHeightAt((cx * 16 + x), (cz * 16 + z)) + 60;
				int finalHeight = (int)height;
				chunk.getBlockStorageArray()[0].setExtBlockID(x, 0, z, Blocks.bedrock);

				for (int y = 1; y < 256; y++)
				{
					int l = y >> 4;
					ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

					if (extendedblockstorage == null)
					{
						extendedblockstorage = new ExtendedBlockStorage(y, !this.worldObj.provider.hasNoSky);
						chunk.getBlockStorageArray()[l] = extendedblockstorage;
					}

					boolean hadStructure = StructureRegister.genStructure(this.worldObj.provider.dimensionId, cx, cz, x, z, y, extendedblockstorage);

					if (!hadStructure && y <= finalHeight)
					{
						double sandThreshold = height * 0.9;
						double sandstoneThreshold = height * 0.6;

						if (y >= sandThreshold)
							extendedblockstorage.setExtBlockID(x, y & 15, z, Blocks.sand);
						else if (y >= sandstoneThreshold && y < sandThreshold)
							extendedblockstorage.setExtBlockID(x, y & 15, z, Blocks.sandstone);
						else
							extendedblockstorage.setExtBlockID(x, y & 15, z, Blocks.stone);
					}
				}
			}
		}

		BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, cx * 16, cz * 16, 16, 16);
		byte[] abyte = chunk.getBiomeArray();

		for (int l = 0; l < abyte.length; ++l)
		{
			abyte[l] = (byte)abiomegenbase[l].biomeID;
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int p_73149_1_, int p_73149_2_)
	{
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	public void populate(IChunkProvider chunk, int chunkX, int chunkZ)
	{
		int k = chunkX * 16;
		int l = chunkZ * 16;
		BiomeGenBase b = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
		if (!(b instanceof PBiomeGenBase))
			return;
		PBiomeGenBase biomegenbase = (PBiomeGenBase)b;
		biomegenbase.decorate(this, this.worldObj, StarWarsGalaxy.random, k, l);
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
	 * Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
	{
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
	 * unimplemented.
	 */
	public void saveExtraData()
	{
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave()
	{
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString()
	{
		return "TatooineLevelSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given location.
	 */
	public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
	{
		BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
		return biomegenbase.getSpawnableList(p_73155_1_);
	}

	@Override
	public ChunkPosition findClosestStructure(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
	{
		return null;
	}

	public int getLoadedChunkCount()
	{
		return 0;
	}

	public void recreateStructures(int p_82695_1_, int p_82695_2_)
	{
	}
}