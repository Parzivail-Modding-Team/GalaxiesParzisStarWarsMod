package com.parzivail.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;

import java.util.EnumSet;
import java.util.Set;

public class DimensionTeleporter
{
	public static void teleport(Entity entity, ServerWorld world)
	{
		var y = world.getChunk(ChunkSectionPos.getSectionCoord(0), ChunkSectionPos.getSectionCoord(0)).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, 0, 0) + 1;

		Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
		set.add(PositionFlag.X_ROT);
		set.add(PositionFlag.Y_ROT);
		teleport(entity, world, 0, y, 0, 0, 0, set);
	}

	public static void teleport(Entity entity, ServerWorld world, double x, double y, double z, float pitch, float yaw, Set<PositionFlag> movementFlags)
	{
		if (entity instanceof ServerPlayerEntity)
		{
			var chunkPos = new ChunkPos(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
			world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
			entity.stopRiding();
			if (((ServerPlayerEntity)entity).isSleeping())
			{
				((ServerPlayerEntity)entity).wakeUp(true, true);
			}

			if (world == entity.getWorld())
			{
				((ServerPlayerEntity)entity).networkHandler.requestTeleport(x, y, z, yaw, pitch, movementFlags);
			}
			else
			{
				((ServerPlayerEntity)entity).teleport(world, x, y, z, yaw, pitch);
			}

			entity.setHeadYaw(yaw);
		}
		else
		{
			var f = MathHelper.wrapDegrees(yaw);
			var g = MathHelper.wrapDegrees(pitch);
			g = MathHelper.clamp(g, -90.0F, 90.0F);
			if (world == entity.getWorld())
			{
				entity.refreshPositionAndAngles(x, y, z, f, g);
				entity.setHeadYaw(f);
			}
			else
			{
				entity.detach();
				var other = entity;
				entity = entity.getType().create(world);

				if (entity == null)
					return;

				entity.copyFrom(other);
				entity.refreshPositionAndAngles(x, y, z, f, g);
				entity.setHeadYaw(f);
				world.onDimensionChanged(entity);
				other.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
			}
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).isFallFlying())
		{
			entity.setVelocity(entity.getVelocity().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}

		if (entity instanceof PathAwareEntity)
		{
			((PathAwareEntity)entity).getNavigation().stop();
		}
	}
}
