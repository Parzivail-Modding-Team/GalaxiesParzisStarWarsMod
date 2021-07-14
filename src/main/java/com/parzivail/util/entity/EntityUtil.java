package com.parzivail.util.entity;

import com.parzivail.util.math.EntityHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Arrays;

public class EntityUtil
{
	public static EntityHitResult raycastEntities(Vec3d startPos, Vec3d fromDir, double distance, Entity fromEntity, Entity[] exclude)
	{
		Entity pointedEntity = null;
		Vec3d hitLocation = null;

		var blacklist = Arrays.asList(exclude);

		fromDir = fromDir.normalize();

		var endPos = startPos.add(fromDir.multiply(distance));
		var list = fromEntity.world.getEntitiesByClass(LivingEntity.class, fromEntity.getBoundingBox().stretch(fromDir.x * distance, fromDir.y * distance, fromDir.z * distance).expand(1, 1, 1), EntityPredicates.EXCEPT_SPECTATOR);

		for (var entity : list)
		{
			if (blacklist.contains(entity))
				continue;

			if (entity.collides())
			{
				var box = entity.getBoundingBox();
				var hitvec = box.raycast(startPos, endPos);

				if (hitvec.isPresent())
				{
					var distanceTo = startPos.distanceTo(hitvec.get());

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
		var end = startPos.add(fromDir.multiply(distance));
		return fromEntity.world.raycast(new RaycastContext(startPos, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, fromEntity));
	}

	public static void setVelocityFromAngles(Entity entity, float pitch, float yaw, float roll, float scalar)
	{
		var f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		var g = -MathHelper.sin((pitch + roll) * 0.017453292F);
		var h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		entity.setVelocity(scalar * f, scalar * g, scalar * h);
	}
}
