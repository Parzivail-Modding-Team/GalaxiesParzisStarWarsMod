package com.parzivail.pswg.world.tatooine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class TatooineChunkGenerator extends SimplexChunkGenerator
{
	public static final Codec<TatooineChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource)
			)
			.apply(instance, instance.stable(TatooineChunkGenerator::new))
	);

	public TatooineChunkGenerator(BiomeSource biomeSource)
	{
		super(biomeSource, true);
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
}
