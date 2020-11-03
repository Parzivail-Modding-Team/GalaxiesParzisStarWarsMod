package com.parzivail.pswg.dimension.tatooine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.dimension.tatooine.terrain.TerrainTatooineCanyons;
import com.parzivail.pswg.structure.ScarifChunk;
import com.parzivail.pswg.structure.ScarifSection;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.util.world.CompositeTerrain;
import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.MultiCompositeTerrain;
import com.parzivail.util.world.TerrainLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class TatooineChunkGenerator extends ChunkGenerator
{
	public static final Codec<TatooineChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.populationSource)).apply(instance, instance.stable(TatooineChunkGenerator::new)));

	public static final ITerrainHeightmap terrain = new MultiCompositeTerrain(0, 60, 3000,
	                                                                          new TerrainTatooineCanyons(0),
	                                                                          new CompositeTerrain(
			                                                                          new TerrainLayer(0, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 25),
			                                                                          new TerrainLayer(1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 2),
			                                                                          new TerrainLayer(2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 12),
			                                                                          new TerrainLayer(3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 15),
			                                                                          new TerrainLayer(4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 100, 0.15)),
	                                                                          new CompositeTerrain(
			                                                                          new TerrainLayer(0, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 150, 5),
			                                                                          new TerrainLayer(1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 1500, 2),
			                                                                          new TerrainLayer(2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 100, 10),
			                                                                          new TerrainLayer(3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 100, 10),
			                                                                          new TerrainLayer(4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 40, 0.5)),
	                                                                          new CompositeTerrain(
			                                                                          new TerrainLayer(0, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 5),
			                                                                          new TerrainLayer(1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 2),
			                                                                          new TerrainLayer(2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 12),
			                                                                          new TerrainLayer(3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 12),
			                                                                          new TerrainLayer(4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 70, 0.8)),
	                                                                          new CompositeTerrain(
			                                                                          new TerrainLayer(0, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Add, 300, 25),
			                                                                          new TerrainLayer(1, TerrainLayer.Function.NCTurbulent, TerrainLayer.Method.Multiply, 300, 2),
			                                                                          new TerrainLayer(2, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 400, 12),
			                                                                          new TerrainLayer(3, TerrainLayer.Function.Simplex, TerrainLayer.Method.Add, 50, 12),
			                                                                          new TerrainLayer(4, TerrainLayer.Function.InvNCTurbulent, TerrainLayer.Method.Multiply, 100, 0.8)));

	public TatooineChunkGenerator(BiomeSource biomeSource)
	{
		super(biomeSource, new StructuresConfig(Optional.empty(), Collections.emptyMap()));
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec()
	{
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed)
	{
		return this;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk)
	{
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk)
	{
		final ChunkPos chunkPos = chunk.getPos();
		final int chunkWorldX = chunkPos.getStartX();
		final int chunkWorldZ = chunkPos.getStartZ();
		final BlockState tatooineSand = SwgBlocks.Sand.Tatooine.getDefaultState();
		final BlockState sandstone = Blocks.SANDSTONE.getDefaultState();
		final BlockState stone = Blocks.STONE.getDefaultState();
		final BlockState bedrock = Blocks.BEDROCK.getDefaultState();

		ProtoChunk pc = (ProtoChunk)chunk;

		int sectionY = 0;
		ChunkSection chunkSection = pc.getSection(0);

		chunkSection.lock();

		for (int cX = 0; cX < 16; cX++)
		{
			for (int cZ = 0; cZ < 16; cZ++)
			{
				int worldX = chunkWorldX + cX;
				int worldZ = chunkWorldZ + cZ;
				int height = (int)terrain.getHeightAt(worldX, worldZ);
				for (int y = 0; y < height; y++)
				{
					if (y >> 4 != sectionY)
					{
						sectionY = y >> 4;

						chunkSection.unlock();
						chunkSection = pc.getSection(sectionY);
						chunkSection.lock();
					}

					if (y == 0)
						chunkSection.setBlockState(cX, 0, cZ, bedrock);
					else if (y > height * 0.98f)
						chunkSection.setBlockState(cX, y & 15, cZ, tatooineSand, false);
					else if (y > height * 0.8f)
						chunkSection.setBlockState(cX, y & 15, cZ, sandstone, false);
					else
						chunkSection.setBlockState(cX, y & 15, cZ, stone, false);
				}
			}
		}

		ScarifChunk strChunk = null; //SwgStructures.General.Region.get().openChunk(chunkPos);
		if (strChunk != null)
		{
			strChunk.init();

			for (Map.Entry<BlockPos, CompoundTag> tile : strChunk.tiles.entrySet())
				pc.addPendingBlockEntityTag(tile.getValue());

			for (int i = 0; i < strChunk.numSections; i++)
			{
				ScarifSection section = strChunk.readSection();

				chunkSection.unlock();
				chunkSection = pc.getSection(section.y);
				chunkSection.lock();

				for (int y = 0; y < 16; y++)
				{
					for (int z = 0; z < 16; z++)
					{
						for (int x = 0; x < 16; x++)
						{
							int stateIdx = section.blockStates[y * 256 + z * 16 + x];
							if (stateIdx >= section.palette.length)
							{
								Lumberjack.warn("Invalid SCARIF palette index for chunk %s,%s, block %s,%s,%s", chunkPos.x, chunkPos.z, x, y, z);
								continue;
							}

							BlockState blockState = section.palette[stateIdx];
							chunkSection.setBlockState(x, y, z, blockState, false);
						}
					}
				}
			}
		}

		chunkSection.unlock();
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType)
	{
		return (int)terrain.getHeightAt(x, z) + 1;
	}

	@Override
	public BlockView getColumnSample(int x, int z)
	{
		return null;
	}
}
