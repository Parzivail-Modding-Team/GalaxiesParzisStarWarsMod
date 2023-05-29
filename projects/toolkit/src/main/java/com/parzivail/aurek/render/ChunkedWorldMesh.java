package com.parzivail.aurek.render;

import com.parzivail.aurek.util.WorkerUtil;
import com.parzivail.aurek.world.GeneratingBlockRenderView;
import com.parzivail.aurek.world.SliceController;
import io.wispforest.worldmesher.WorldMesh;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkedWorldMesh
{
	private static final ExecutorService WORLDGEN_WORKER = WorkerUtil.createWorker("WorldGenerationWorker", Runtime.getRuntime().availableProcessors() / 2);
	private static final ExecutorService MESH_REBUILD_WORKER = WorkerUtil.createThreadPool(1, "MeshRebuildWorker");

	private final AtomicBoolean isRegenerating = new AtomicBoolean();
	private final Long2ObjectMap<WorldMesh> renderMap;
	private final GeneratingBlockRenderView world;
	private final ChunkPos min;
	private final ChunkPos max;
	private final int minY;
	private final int maxY;
	private final Vec3i dimensions;
	private final SliceController slice;

	public boolean anyChunksBuilding = false;

	public ChunkedWorldMesh(GeneratingBlockRenderView world, ChunkPos min, ChunkPos max, int minY, int maxY)
	{
		this.world = world;
		this.min = min;
		this.max = max;
		this.minY = minY;
		this.maxY = maxY;

		this.dimensions = new Vec3i((max.x - min.x + 1) << 4, maxY - minY, (max.z - min.z + 1) << 4);

		this.renderMap = new Long2ObjectAVLTreeMap<>();
		this.slice = new SliceController(this);

		this.world.setChunkOverrider(this.slice);

		ChunkPos.stream(min, max).forEach(pos -> {
			this.renderMap.put(
					pos.toLong(),
					new WorldMesh.Builder(world, pos.getStartPos().add(0, minY, 0), pos.getStartPos().add(15, maxY, 15))
							.useGlobalNeighbors()
							.build()
			);
		});
	}

	public void setSeed(long seed)
	{
		this.world.setSeed(seed);
		scheduleRegererate();
	}

	public long getSeed()
	{
		return this.world.getSeed();
	}

	public int getMinY()
	{
		return minY;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public ChunkPos getMin()
	{
		return min;
	}

	public ChunkPos getMax()
	{
		return max;
	}

	public Vec3i getDimensions()
	{
		return dimensions;
	}

	public SliceController getSlice()
	{
		return slice;
	}

	public boolean isRegenerating()
	{
		return isRegenerating.get();
	}

	public boolean isBuilding()
	{
		return anyChunksBuilding;
	}

	public boolean isBusy()
	{
		return isBuilding() || isRegenerating();
	}

	public void scheduleRegererate()
	{
		CompletableFuture.runAsync(() -> {
			isRegenerating.set(true);

			// Schedule worldgen
			var regenFutures = new ArrayList<CompletableFuture<Void>>();
			for (var entry : renderMap.keySet())
			{
				var pos = new ChunkPos(entry);
				var regenFuture = CompletableFuture.runAsync(() -> world.regenerate(pos), WORLDGEN_WORKER);
				regenFutures.add(regenFuture);

				regenFuture.whenComplete((unused, throwable) -> {
					scheduleRebuild(pos);
				});
			}

			// Block decoration until worldgen has completed
			for (var f : regenFutures)
				f.join();

			// Schedule decoration
			var decorateFutures = new ArrayList<CompletableFuture<Void>>();
			for (var entry : renderMap.keySet())
				decorateFutures.add(CompletableFuture.runAsync(() -> world.decorate(new ChunkPos(entry)), WORLDGEN_WORKER));

			// Wait until decoration has completed
			for (var f : decorateFutures)
				f.join();

			// Rebuild modified chunk
			for (var dirtyPos : world.getDirtyChunks())
				scheduleRebuild(new ChunkPos(dirtyPos));

			isRegenerating.set(false);
		}, MESH_REBUILD_WORKER);
	}

	public void scheduleRebuild(ChunkPos pos)
	{
		var mesh = renderMap.get(pos.toLong());
		if (mesh == null)
			return;

		mesh.scheduleRebuild(MESH_REBUILD_WORKER);
		world.clearDirty(pos.toLong());
	}

	public void render(MatrixStack matrixStack)
	{
		var anyBuilding = false;

		for (var entry : renderMap.long2ObjectEntrySet())
		{
			var mesh = entry.getValue();
			if (mesh == null)
				return;

			var state = mesh.state();

			if (state.isBuildStage)
				anyBuilding = true;

			if (!state.canRender)
				continue;

			var chunkPos = entry.getLongKey();
			var chunkX = (int)chunkPos;
			var chunkZ = (int)(chunkPos >> 32);

			if (isChunkHidden(chunkX, chunkZ))
				continue;

			var dX = chunkX - min.x;
			var dZ = chunkZ - min.z;

			matrixStack.push();
			matrixStack.translate(dX << 4, 0, dZ << 4);
			if (state.isBuildStage)
			{
				var lateralFactor = 16 / 17f;
				var height = dimensions.getY();
				matrixStack.translate(8, height / 2f, 8);
				matrixStack.scale(lateralFactor, 1, lateralFactor);
				matrixStack.translate(-8, -height / 2f, -8);
			}
			mesh.render(matrixStack);
			matrixStack.pop();
		}

		this.anyChunksBuilding = anyBuilding;
	}

	public void close()
	{
		renderMap.values().forEach(WorldMesh::reset);
	}

	public boolean isChunkHidden(int chunkX, int chunkZ)
	{
		return !slice.shouldChunkRender(chunkX, chunkZ);
	}
}
