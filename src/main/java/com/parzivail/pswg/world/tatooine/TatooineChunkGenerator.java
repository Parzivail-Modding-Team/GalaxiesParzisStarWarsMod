package com.parzivail.pswg.world.tatooine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgDimensions;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.scarif.ScarifChunk;
import com.parzivail.util.world.NoiseGenerator;
import com.parzivail.util.world.ProcNoise;
import com.parzivail.util.world.SimplexChunkGenerator;
import com.parzivail.util.world.biome.BiomeSurface;
import com.parzivail.util.world.biome.BiomeSurfaceHint;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executor;

public class TatooineChunkGenerator extends SimplexChunkGenerator
{
	public static final Codec<TatooineChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource)
			)
			.apply(instance, instance.stable(TatooineChunkGenerator::new))
	);

	private static final int MIN_HEIGHT = 60;

	private final HashMap<RegistryKey<Biome>, NoiseGenerator> noiseGenerators;

	private final ProcNoise noiseSrc;

	public TatooineChunkGenerator(BiomeSource biomeSource)
	{
		super(biomeSource, true);

		noiseGenerators = new HashMap<>();
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_DUNES_KEY, this::genDunes);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_CANYONS_KEY, this::genCanyons);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_PLATEAU_KEY, this::genPlateau);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_MUSHMESA_KEY, this::genMushroomMesa);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_WASTES_KEY, this::genWastes);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_MOUNTAINS_KEY, this::genMountains);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_SALTFLATS_KEY, this::genSaltFlats);
		noiseGenerators.put(SwgDimensions.Tatooine.BIOME_OASIS_KEY, this::genOasis);

		noiseSrc = new ProcNoise(0);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec()
	{
		return CODEC;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk)
	{
	}

	@Override
	protected Optional<BlockState> getStrataBlock(int x, int y, int z, BiomeSurface surface)
	{
		if (y == 0)
			return Optional.of(Blocks.BEDROCK.getDefaultState());

		if (y > 0.9 * surface.height())
			return Optional.empty();

		/*
		 *TODO:
		 * move these into a registry like the biome noise generators, each biome will have different surface
		 * thicknesses and thickness should even vary based on the surface hint. For example, salt flats should
		 * have a surface thickness of 1 and the crag part of wastes and the tops of plateaus should have a high
		 * surface thickness
		 */
		return Optional.of(Blocks.STONE.getDefaultState());
	}

	@Override
	protected void populateExtra(Executor executor, StructureAccessor accessor, Chunk chunk)
	{
		ScarifChunk strChunk = null; //SwgStructures.General.Region.get().openChunk(chunkPos);
		if (strChunk != null)
		{
			var chunkPos = chunk.getPos();
			strChunk.init();
			var pc = (ProtoChunk)chunk;

			var pos = new BlockPos.Mutable();

			for (var tile : strChunk.tiles.entrySet())
				pc.addPendingBlockEntityNbt(tile.getValue());

			for (var i = 0; i < strChunk.numSections; i++)
			{
				var section = strChunk.readSection();
				var states = section.blockStates();
				var palette = section.palette();
				var sY = section.y();

				for (var y = 0; y < 16; y++)
				{
					for (var z = 0; z < 16; z++)
					{
						for (var x = 0; x < 16; x++)
						{
							var stateIdx = states[y * 256 + z * 16 + x];
							if (stateIdx >= palette.length)
							{
								Lumberjack.warn("Invalid SCARIF palette index for chunk %s,%s, block %s,%s,%s", chunkPos.x, chunkPos.z, x, y, z);
								continue;
							}

							var blockState = section.palette()[stateIdx];
							chunk.setBlockState(pos.set(x, sY + y, z), blockState, false);
						}
					}
				}
			}
		}
	}

	@Override
	protected BiomeSurfaceHint getNoise(RegistryKey<Biome> biomeKey, int x, int z)
	{
		return noiseGenerators.get(biomeKey).generate(x, z);
	}

	private BiomeSurfaceHint genDunes(double x, double z)
	{
		var dX = noiseSrc.rawNoise(x / 100 + 1000, z / 100) * 20;
		var dZ = noiseSrc.rawNoise(x / 100, z / 100 + 1000) * 20;

		var noise = noiseSrc.noise(x / 400 - 3000, z / 400) * 18;

		var duneShape = Math.pow((1 - Math.abs(noiseSrc.rawNoise((x + dX) / 150, (z + dZ) / 150 + 3000))), 0.75);
		noise += noiseSrc.noise(x / 80, z / 80 - 3000) * duneShape * 25;

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}

	private BiomeSurfaceHint genCanyons(double x, double z)
	{
		var nx = noiseSrc.octaveNoise(x / 1500 + 1000, z / 1500, 8);
		var nz = noiseSrc.octaveNoise(x / 1500, z / 1500 + 1000, 8);
		var noise = noiseSrc.worley(x / 500 + nx, z / 500 + nz);

		var d = 0.7;
		var winding = Math.sqrt(noise);
		var basin = (1 - (winding - d) / (1 - d));

		double height = 0;
		if (winding < d)
			// Canyon tops
			height = (basin - 1) * 10 * noiseSrc.octaveNoise(x / 500, z / 500, 6) + 60;
		else
			// Carved area
			height = 60 * Math.pow(basin, 10);

		return new BiomeSurfaceHint(MIN_HEIGHT + height, SwgBlocks.Stone.DesertSediment.getDefaultState());
	}

	private BiomeSurfaceHint genPlateau(double x, double z)
	{
		var dX = noiseSrc.rawNoise(x / 50, z / 50 + 1000) * 10;
		var dZ = noiseSrc.rawNoise(x / 50 + 1000, z / 50) * 10;
		var h = noiseSrc.octaveNoise((x + dX) / 500, (z + dZ) / 500, 5);

		h = Math.pow(h, 2) - 0.2;

		var surfaceNoise = noiseSrc.octaveNoise(x / 100, z / 100, 2) * 0.5;

		if (h > 0.15)
			return new BiomeSurfaceHint(MIN_HEIGHT + (0.5 + 0.15 * surfaceNoise) * 60, SwgBlocks.Stone.DesertSediment.getDefaultState());

		var floorNoise = noiseSrc.rawNoise(x / 150, z / 150);

		return new BiomeSurfaceHint(MIN_HEIGHT + Math.max(h * 180, floorNoise * 2), SwgBlocks.Sand.DesertCanyon.getDefaultState());
	}

	private BiomeSurfaceHint genMushroomMesa(double x, double z)
	{
		var noise = noiseSrc.octaveNoise(x / 600, z / 600, 7) * 3;
		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}

	private BiomeSurfaceHint genWastes(double x, double z)
	{
		var flatness = noiseSrc.noise(x / 200, z / 200);
		var noise = noiseSrc.octaveNoise(x / 100, z / 100, 4) * 15 * flatness;

		var dx = noiseSrc.rawNoise(x / 20, z / 20 + 1000) * 5;
		var dz = noiseSrc.rawNoise(x / 20 + 1000, z / 20) * 5;

		dx += noiseSrc.rawNoise(x / 100, z / 100 + 1000) * 18;
		dz += noiseSrc.rawNoise(x / 100 + 1000, z / 100) * 18;

		var peaks = Math.pow(1 - noiseSrc.worley((x + dx) / 300 - 2000, (z + dz) / 300), 2);

		var peakHeight = Math.min(peaks * 90 - 25, 40);

		if (peakHeight > 0)
			return new BiomeSurfaceHint(MIN_HEIGHT + peakHeight + noise, SwgBlocks.Sand.DesertCanyon.getDefaultState());

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}

	private BiomeSurfaceHint genMountains(double x, double z)
	{
		var domainWarpScale = noiseSrc.rawNoise(x / 50 - 1000, z / 50 - 1000);
		var dx = noiseSrc.rawNoise(x / 40 + 1000, z / 40) * domainWarpScale * 20;
		var dz = noiseSrc.rawNoise(x / 40, z / 40 + 1000) * domainWarpScale * 20;
		var h = noiseSrc.octaveNoise((x + dx) / 200, (z + dz) / 200, 6) * 2;

		h *= Math.pow(1.2 - noiseSrc.worley(x / 300, z / 300), 2);

		return new BiomeSurfaceHint(MIN_HEIGHT + h * 50, SwgBlocks.Dirt.DesertLoam.getDefaultState());
	}

	private BiomeSurfaceHint genSaltFlats(double x, double z)
	{
		return new BiomeSurfaceHint(MIN_HEIGHT + 1, SwgBlocks.Salt.Caked.getDefaultState());
	}

	private BiomeSurfaceHint genOasis(double x, double z)
	{
		// TODO: better terrain
		var noise = noiseSrc.octaveNoise(x / 100, z / 100 + 3000, 4) * 8;

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}
}
