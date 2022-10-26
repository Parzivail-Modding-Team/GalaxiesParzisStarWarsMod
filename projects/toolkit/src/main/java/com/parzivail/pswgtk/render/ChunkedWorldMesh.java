package com.parzivail.pswgtk.render;

import io.wispforest.worldmesher.WorldMesh;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;

public class ChunkedWorldMesh
{
	private final Long2ObjectMap<WorldMesh> renderMap;
	private final BlockRenderView world;
	private final ChunkPos min;
	private final Vec3i dimensions;

	public ChunkedWorldMesh(BlockRenderView world, ChunkPos min, ChunkPos max, int minY, int maxY)
	{
		this.world = world;
		this.min = min;

		this.dimensions = new Vec3i((max.x - min.x) << 4, maxY - minY, (max.z - min.z) << 4);

		this.renderMap = new Long2ObjectArrayMap<>((max.x - min.x) * (max.z - min.z));
		ChunkPos.stream(min, max).forEach(chunkPos -> {
			this.renderMap.put(chunkPos.toLong(), new WorldMesh.Builder(world, chunkPos.getStartPos().add(0, minY, 0), chunkPos.getStartPos().add(15, maxY, 15)).build());
		});
	}

	public Vec3i getDimensions()
	{
		return dimensions;
	}

	public void scheduleRebuild()
	{
		renderMap.values().forEach(WorldMesh::scheduleRebuild);
	}

	public void scheduleRebuild(ChunkPos pos)
	{
		var mesh = renderMap.get(pos.toLong());
		if (mesh == null)
			return;

		mesh.scheduleRebuild();
	}

	public void render(MatrixStack matrixStack)
	{
		for (var entry : renderMap.long2ObjectEntrySet())
		{
			var mesh = entry.getValue();

			var state = mesh.getState();
			if (!state.canRender)
				continue;

			var chunkPos = entry.getLongKey();
			var chunkX = (int)chunkPos;
			var chunkZ = (int)(chunkPos >> 32);

			var dX = chunkX - min.x;
			var dZ = chunkZ - min.z;

			matrixStack.push();
			matrixStack.translate(dX << 4, 0, dZ << 4);
			if (state.isBuildStage)
			{
				var f = 16 / 17f;
				matrixStack.translate(8, dimensions.getY() / 2f, 8);
				matrixStack.scale(f, f, f);
				matrixStack.translate(-8, -dimensions.getY() / 2f, -8);
			}
			mesh.render(matrixStack);
			matrixStack.pop();
		}
	}
}
