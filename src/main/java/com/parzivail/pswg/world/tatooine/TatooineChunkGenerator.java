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
import com.parzivail.util.world.biome.BiomeSurfaceHint;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
		// "decorate"
	}

	protected Optional<BlockState> getStrataBlock(int x, int y, int z, int height)
	{
		if (y == 0)
			return Optional.of(Blocks.BEDROCK.getDefaultState());

		if (y > 0.9 * height)
			return Optional.empty();

		/*
		 *TODO:
		 * move these into a registry like the biome noise generators, each biome will have different surface
		 * thicknesses and thickness should even vary based on the surface hint. For example, salt flats should
		 * have a surface thickness of 1 and the crag part of wastes and the tops of plateaus should have a high
		 * surface thickness
		 */
		return Optional.of(Blocks.SANDSTONE.getDefaultState());
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
							chunk.setBlockState(pos.set(x, section.y + y, z), blockState, false);
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

	private double getWorleyDomainWarped(double x, double z)
	{
		double offsetX = noiseSrc.noise(x / 10, z / 10 + 1000) / 10;
		double offsetY = noiseSrc.noise(x / 10 + 1000, z / 10) / 10;

		return noiseSrc.worley(x / 50 + offsetX, z / 50 + offsetY);
	}

	private BiomeSurfaceHint genDunes(double x, double z)
	{
		double noise = noiseSrc.noise(x / 400 - 3000, z / 400) * 25;

		noise += noiseSrc.noise(x / 50, z / 50 - 3000) * 25;

		noise *= (1 - Math.abs(noiseSrc.rawNoise(x / 70, z / 70 + 3000)));

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}

	private BiomeSurfaceHint genCanyons(double x, double z)
	{
		double s = 2;
		double h = getWorleyDomainWarped(x / s, z / s);
		double d = 5 * s;

		double blur = 0;
		blur = blur + getWorleyDomainWarped(x / s - d, z / s - d);
		blur = blur + getWorleyDomainWarped(x / s - d, z / s + d);

		blur = blur + getWorleyDomainWarped(x / s + d, z / s - d);
		blur = blur + getWorleyDomainWarped(x / s + d, z / s + d);
		blur = blur / 4;

		h = 1 - (h - blur);
		h = 1 - 1 / (2 * h) - 0.48;
		h = h * 35;

		h = MathHelper.clamp(h, 0, 1);
		double j = noiseSrc.octaveNoise(x / 200f, z / 200f, 6) * 40;

		var noise = (h * 0.8 + noiseSrc.octaveNoise(x / 200f, z / 200f, 3) * 0.4) * (j + 10);

		// TODO: better surface hinting
		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.DesertCanyon.getDefaultState());
	}

	private BiomeSurfaceHint genPlateau(double x, double z)
	{
		var h = noiseSrc.octaveNoise(x / 500, z / 500, 5);

		h = Math.pow(h, 2) - 0.3;

		var surfaceNoise = noiseSrc.octaveWorley(x / 100, z / 100, 5);

		if (h > 0.15)
		{
			return new BiomeSurfaceHint((0.5 + 0.15 * surfaceNoise) * 60, SwgBlocks.Stone.DesertSediment.getDefaultState());
		}

		return new BiomeSurfaceHint(MIN_HEIGHT + h * 180, SwgBlocks.Sand.DesertCanyon.getDefaultState());
	}

	private BiomeSurfaceHint genMushroomMesa(double x, double z)
	{
		// TODO: currently plains

		var noise = noiseSrc.noise(x / 100, z / 100) * 10 + Math.abs(noiseSrc.rawNoise(x / 50, z / 50)) * 5;
		return new BiomeSurfaceHint(noise, SwgBlocks.Sand.Desert.getDefaultState());
	}

	private BiomeSurfaceHint genWastes(double x, double z)
	{
		var dx = noiseSrc.noise(x / 5, z / 5 + 3000) * 10;
		var dz = noiseSrc.noise(x / 5 + 3000, z / 5) * 10;

		var peaks = noiseSrc.octaveInvWorley((x + dx) / 100 - 3000, (z + dz) / 100, 3) * 1.2;

		BlockState surfaceHintBlock;

		double noise;
		var peaks30 = peaks * 30;
		if (peaks30 > 25)
		{
			noise = peaks30 - 20;

			surfaceHintBlock = SwgBlocks.Stone.DesertSediment.getDefaultState();
		}
		else
		{
			noise = 5;
			surfaceHintBlock = SwgBlocks.Sand.Desert.getDefaultState();
		}

		noise *= 5 * (noiseSrc.noise(x / 70, z / 70) + 0.1);

		noise += noiseSrc.octaveNoise(x / 50, z / 50 - 3000, 3) * 30;

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, surfaceHintBlock);
	}

	private BiomeSurfaceHint genMountains(double x, double z)
	{
		var domainWarpScale = noiseSrc.noise(x / 100 - 1000, z / 100 - 1000);
		var dx = noiseSrc.rawNoise(x / 80 + 1000, z / 80) * domainWarpScale * 20;
		var dz = noiseSrc.rawNoise(x / 80, z / 80 + 1000) * domainWarpScale * 20;
		var h = noiseSrc.octaveNoise((x + dx) / 400, (z + dz) / 400, 6) * 2;

		h *= Math.pow(1.2 - noiseSrc.octaveWorley(x / 600, z / 600, 2), 3);

		return new BiomeSurfaceHint(MIN_HEIGHT + h * 75, SwgBlocks.Dirt.DesertLoam.getDefaultState());
	}

	private BiomeSurfaceHint genSaltFlats(double x, double z)
	{
		return new BiomeSurfaceHint(MIN_HEIGHT + 1, SwgBlocks.Salt.Caked.getDefaultState());
	}

	private BiomeSurfaceHint genOasis(double x, double z)
	{
		// TODO: currently hills
		double noise = noiseSrc.noise(x / 100, z / 100 + 3000) * 20;

		noise *= (1 - Math.abs(noiseSrc.rawNoise(x / 30 + 2000, z / 30)));

		return new BiomeSurfaceHint(MIN_HEIGHT + noise, SwgBlocks.Sand.Desert.getDefaultState());
	}
}
