package com.parzivail.pswgtk.world;

import com.parzivail.util.gen.BiomeGenerator;
import com.parzivail.util.gen.TerrainGenerator;
import com.parzivail.util.gen.world.ChunkView;
import com.parzivail.util.gen.world.WorldGenView;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class GeneratingBlockRenderView extends ProceduralBlockRenderView implements WorldGenView
{
	private final TerrainGenerator terrainGenerator = new TerrainGenerator(0, new BiomeGenerator(0), null);
	private final Long2ObjectMap<ChunkView> chunks = new Long2ObjectAVLTreeMap<>();
	private final Set<Long> dirtyChunks = new ConcurrentSkipListSet<>();

	public GeneratingBlockRenderView(ChunkPos min, ChunkPos max, int minY, int maxY)
	{
		ChunkPos.stream(min, max).forEach(chunkPos -> chunks.put(chunkPos.toLong(), new ArrayChunk(chunkPos, minY, maxY)));
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos)
	{
		var pos = ChunkPos.toLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
		var chunk = chunks.get(pos);
		if (chunk == null)
			return Blocks.VOID_AIR.getDefaultState();

		return chunk.getBlockState(blockPos);
	}

	@Override
	public int getTopY(Heightmap.Type type, int x, int z)
	{
		var pos = ChunkPos.toLong(x >> 4, z >> 4);
		var chunk = chunks.get(pos);
		if (chunk == null)
			return 0;

		return chunk.sampleHeightmap(type, x & 15, z & 15);
	}

	@Override
	public void setBlockState(BlockPos blockPos, BlockState state)
	{
		var pos = ChunkPos.toLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
		var chunk = chunks.get(pos);
		if (chunk == null)
			return;

		dirtyChunks.add(pos);
		chunk.setBlockState(blockPos, state);
	}

	@Override
	public void addEntity(Entity entity)
	{
	}

	public Set<Long> getDirtyChunks()
	{
		return dirtyChunks;
	}

	public void clearDirty(long pos)
	{
		dirtyChunks.remove(pos);
	}

	public void regenerate(ChunkPos pos)
	{
		var c = chunks.get(pos.toLong());
		terrainGenerator.buildNoise(c);
		terrainGenerator.buildSurface(c);
	}

	public void decorate(ChunkPos pos)
	{
		terrainGenerator.generateDecorations(this, chunks.get(pos.toLong()));
	}
}
