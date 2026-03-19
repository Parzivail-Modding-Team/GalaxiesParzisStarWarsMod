package dev.pswg.util.world;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

public class DimensionTeleporter
{
	public static void teleport(Entity entity, ServerLevel world)
	{
		var y = world.getChunk(SectionPos.blockToSectionCoord(0), SectionPos.blockToSectionCoord(0)).getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0) + 1;

		Set<Relative> set = EnumSet.noneOf(Relative.class);
		set.add(Relative.X_ROT);
		set.add(Relative.Y_ROT);
		teleport(entity, world, 0, y, 0, 0, 0, set);
	}

	public static void teleport(Entity entity, ServerLevel world, double x, double y, double z, float pitch, float yaw, Set<Relative> movementFlags)
	{
		if (entity instanceof ServerPlayer)
		{
			var chunkPos = new ChunkPos(new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)));
			world.getChunkSource().addTicketWithRadius(TicketType.PLAYER_LOADING, chunkPos, 1); //, entity.getId()
			entity.stopRiding();
			if (((ServerPlayer)entity).isSleeping())
			{
				((ServerPlayer)entity).stopSleepInBed(true, true);
			}

			if (world == ((ServerPlayer)entity).level())
			{
				((ServerPlayer)entity).connection.teleport(x, y, z, yaw, pitch);
			}
			else
			{
				entity.teleportTo(world, x, y, z, movementFlags, yaw, pitch, true);
			}

			entity.setYHeadRot(yaw);
		}
		else
		{
			var f = Mth.wrapDegrees(yaw);
			var g = Mth.wrapDegrees(pitch);
			g = Mth.clamp(g, -90.0F, 90.0F);
			if (world == entity.level())
			{
				entity.snapTo(x, y, z, f, g);
				entity.setYHeadRot(f);
			}
			else
			{
				entity.unRide();
				var other = entity;
				entity = entity.getType().create(world, EntitySpawnReason.DIMENSION_TRAVEL);

				if (entity == null)
					return;

				entity.restoreFrom(other);
				entity.snapTo(x, y, z, f, g);
				entity.setYHeadRot(f);
				world.addDuringTeleport(entity);
				other.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
			}
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).isDescending())
		{
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}

		if (entity instanceof PathfinderMob)
		{
			((PathfinderMob)entity).getNavigation().stop();
		}
	}
}