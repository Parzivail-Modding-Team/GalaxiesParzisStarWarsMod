package com.parzivail.pswg.world.tatooine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.world.tatooine.terrain.TerrainTatooineCanyons;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.scarif.ScarifChunk;
import com.parzivail.util.world.CompositeTerrain;
import com.parzivail.util.world.ITerrainHeightmap;
import com.parzivail.util.world.MultiCompositeTerrain;
import com.parzivail.util.world.TerrainLayer;
import net.minecraft.block.Blocks;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk)
	{
		final var chunkPos = chunk.getPos();
		final var chunkWorldX = chunkPos.getStartX();
		final var chunkWorldZ = chunkPos.getStartZ();
		final var tatooineSand = SwgBlocks.Sand.Desert.getDefaultState();
		final var sandstone = Blocks.SANDSTONE.getDefaultState();
		final var stone = Blocks.STONE.getDefaultState();
		final var bedrock = Blocks.BEDROCK.getDefaultState();

		var pc = (ProtoChunk)chunk;

		var sectionY = 0;
		var chunkSection = pc.getSection(0);

		chunkSection.lock();

		for (var cX = 0; cX < 16; cX++)
		{
			for (var cZ = 0; cZ < 16; cZ++)
			{
				var worldX = chunkWorldX + cX;
				var worldZ = chunkWorldZ + cZ;
				var height = (int)terrain.getHeightAt(worldX, worldZ);
				for (var y = 0; y < height; y++)
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

			for (var tile : strChunk.tiles.entrySet())
				pc.addPendingBlockEntityNbt(tile.getValue());

			for (var i = 0; i < strChunk.numSections; i++)
			{
				var section = strChunk.readSection();

				chunkSection.unlock();
				chunkSection = pc.getSection(section.y);
				chunkSection.lock();

				for (var y = 0; y < 16; y++)
				{
					for (var z = 0; z < 16; z++)
					{
						for (var x = 0; x < 16; x++)
						{
							var stateIdx = section.blockStates[y * 256 + z * 16 + x];
							if (stateIdx >= section.palette.length)
							{
								Lumberjack.warn("Invalid SCARIF palette index for chunk %s,%s, block %s,%s,%s", chunkPos.x, chunkPos.z, x, y, z);
								continue;
							}

							var blockState = section.palette[stateIdx];
							chunkSection.setBlockState(x, y, z, blockState, false);
						}
					}
				}
			}
		}

		chunkSection.unlock();

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView world)
	{
		return (int)terrain.getHeightAt(x, z) + 1;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world)
	{
		return null;
	}
}
