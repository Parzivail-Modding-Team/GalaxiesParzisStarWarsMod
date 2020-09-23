package com.parzivail.pswg.dimension;

import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DefaultEntityPlacer implements EntityPlacer
{
	public static final DefaultEntityPlacer INSTANCE = new DefaultEntityPlacer();

	private DefaultEntityPlacer()
	{
	}

	@Override
	public BlockPattern.TeleportTarget placeEntity(Entity entity, ServerWorld serverWorld, Direction direction, double v, double v1)
	{
		BlockPos target = serverWorld.dimension.getSpawningBlockInChunk(new ChunkPos(0, 0), false);
		return new BlockPattern.TeleportTarget(new Vec3d(target.getX(), target.getY(), target.getZ()), new Vec3d(0, 0, 0), 0);
	}
}
