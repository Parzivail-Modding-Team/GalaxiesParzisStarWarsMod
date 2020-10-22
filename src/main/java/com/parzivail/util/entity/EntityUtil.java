package com.parzivail.util.entity;

import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.math.EntityHitResult;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EntityUtil
{
	public static EntityHitResult raycastEntities(Vec3d startPos, Vec3d fromDir, double distance, Entity fromEntity, Entity[] exclude)
	{
		Entity pointedEntity = null;
		Vec3d hitLocation = null;

		List<Entity> blacklist = Arrays.asList(exclude);

		Vec3d endPos = startPos.add(fromDir.multiply(distance));
		List<Entity> list = fromEntity.world.getEntitiesByClass(LivingEntity.class, fromEntity.getBoundingBox().stretch(fromDir.x * distance, fromDir.y * distance, fromDir.z * distance).expand(1, 1, 1), EntityPredicates.EXCEPT_SPECTATOR);

		for (Entity entity : list)
		{
			if (blacklist.contains(entity))
				continue;

			if (entity.collides())
			{
				Box box = entity.getBoundingBox();
				Optional<Vec3d> hitvec = box.raycast(startPos, endPos);

				if (hitvec.isPresent())
				{
					double distanceTo = startPos.distanceTo(hitvec.get());

					if (distanceTo < distance)
					{
						pointedEntity = entity;
						distance = distanceTo;
						hitLocation = hitvec.get();
					}
				}
			}
		}

		if (pointedEntity != null)
			return new EntityHitResult(pointedEntity, hitLocation);

		return null;
	}

	public static BlockHitResult raycastBlocks(Vec3d startPos, Vec3d fromDir, double distance, Entity fromEntity)
	{
		Vec3d end = startPos.add(fromDir.multiply(distance));
		return fromEntity.world.raycast(new RaycastContext(startPos, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, fromEntity));
	}

	public static void updateEulerRotation(Entity entity, Quaternion rotation)
	{
		EulerAngle eulerAngle = QuatUtil.toEulerAngles(rotation);
		entity.yaw = eulerAngle.getYaw();
		entity.pitch = eulerAngle.getPitch();

		while (entity.pitch - entity.prevPitch >= 180.0F)
		{
			entity.prevPitch += 360.0F;
		}

		while (entity.yaw - entity.prevYaw < -180.0F)
		{
			entity.prevYaw -= 360.0F;
		}

		while (entity.yaw - entity.prevYaw >= 180.0F)
		{
			entity.prevYaw += 360.0F;
		}
	}

	public static Vec3d getPosition(Entity e, float tickDelta)
	{
		Vec3d parentPos = e.getPos();
		Vec3d prevParentPos = new Vec3d(e.prevX, e.prevY, e.prevZ);

		return MathUtil.lerp(tickDelta, prevParentPos, parentPos);
	}

	public static void setVelocityFromAngles(Entity entity, float pitch, float yaw, float roll, float scalar)
	{
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float g = -MathHelper.sin((pitch + roll) * 0.017453292F);
		float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		entity.setVelocity(scalar * f, scalar * g, scalar * h);
	}
}
