package com.parzivail.util.entity;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitBlock;
import com.parzivail.util.math.RaytraceHitEntity;
import com.parzivail.util.ui.Fx.Util;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EntityUtils
{
	private static int mobId = 300;

	/**
	 * Gets the last used Mob ID
	 *
	 * @return
	 */
	public static int getLastUsedId()
	{
		return mobId;
	}

	/**
	 * Ray-traces from the given entity's POV
	 *
	 * @param distance   The range of the trace
	 * @param fromEntity The POV entity
	 * @param exclude    The entity references to exclude (Note: not classes, but
	 *                   inequality between two entity pointers)
	 *
	 * @return Returns the entity the trace hit, or null if none is hit
	 */
	public static RaytraceHit rayTrace(Vec3 fromDir, double distance, EntityLivingBase fromEntity, Entity[] exclude, boolean includeBlocks)
	{
		if (fromEntity == null || fromEntity.worldObj == null)
			return null;

		Entity pointedEntity = null;
		RaytraceHitBlock rhb = null;
		Vec3 startPos = fromEntity.getPosition(0).addVector(0, fromEntity.getEyeHeight(), 0);
		Vec3 endPos = startPos.addVector(fromDir.xCoord * distance, fromDir.yCoord * distance, fromDir.zCoord * distance);
		List list = fromEntity.worldObj.getEntitiesWithinAABBExcludingEntity(fromEntity, fromEntity.boundingBox.addCoord(fromDir.xCoord * distance, fromDir.yCoord * distance, fromDir.zCoord * distance).expand(1, 1, 1));

		if (includeBlocks)
		{
			Vec3 newEnd = Vec3.createVectorHelper(startPos.xCoord, startPos.yCoord, startPos.zCoord);
			MovingObjectPosition mop = fromEntity.worldObj.rayTraceBlocks(newEnd, endPos);
			if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
			{
				rhb = new RaytraceHitBlock(mop.hitVec, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);
				distance = mop.hitVec.distanceTo(startPos);
				endPos = newEnd;
			}
		}

		if (!fromEntity.worldObj.isRemote)
		{
			StarWarsGalaxy.instance.traceStart = Util.Vector3f(startPos);
			StarWarsGalaxy.instance.traceEnd = Util.Vector3f(endPos);
		}

		for (Object e : list)
		{
			Entity entity = (Entity)e;

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(startPos, endPos);

				if (axisalignedbb.isVecInside(startPos) && (0.0D < distance || distance == 0.0D) && !Arrays.asList(exclude).contains(entity))
				{
					pointedEntity = entity;
					distance = 0.0D;
				}
				else if (movingobjectposition != null)
				{
					double d3 = startPos.distanceTo(movingobjectposition.hitVec);

					if ((d3 < distance || distance == 0.0D) && !Arrays.asList(exclude).contains(entity))
					{
						pointedEntity = entity;
						distance = d3;
					}
				}
			}
		}

		if (pointedEntity != null)
			return new RaytraceHitEntity(pointedEntity);

		return rhb;
	}

	/**
	 * Registers an Entity with FML
	 *
	 * @param entityClass The class to register
	 * @param entityName  The name of the entity
	 */
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName)
	{
		while (EntityList.getClassFromID(mobId) != null)
			mobId += 1;
		EntityRegistry.registerModEntity(entityClass, entityName, mobId, StarWarsGalaxy.instance, 80, 1, true);
		EntityList.idToClassMap.put(mobId, entityClass);
		Lumberjack.debug("Registered entity \"" + entityName + "\" as ID " + String.valueOf(mobId));
	}

	/**
	 * Registers an Entity with FML, and creates a genComposite egg
	 *
	 * @param mobClass The class to register
	 * @param mobName  The name of the entity
	 * @param bgColor  The background color of the egg
	 * @param fgColor  The foreground color of the egg
	 */
	public static void registerWithSpawnEgg(Class<? extends Entity> mobClass, String mobName, int bgColor, int fgColor)
	{
		while (EntityList.getClassFromID(mobId) != null)
			mobId += 1;
		EntityRegistry.registerModEntity(mobClass, mobName, mobId, StarWarsGalaxy.instance, 80, 1, true);
		EntityList.idToClassMap.put(mobId, mobClass);
		EntityList.entityEggs.put(mobId, new EntityEggInfo(mobId, bgColor, fgColor));
		Lumberjack.debug("Registered entity (and egg) \"" + mobName + "\" as ID " + String.valueOf(mobId));
	}

	//	public static boolean isRiding(Entity entity, Class<? extends Entity> clazz)
	//	{
	//		return !(entity == null || entity.ridingEntity == null) && (clazz.isInstance(entity.ridingEntity) || (entity.ridingEntity instanceof EntitySeat && clazz.isInstance(((EntitySeat)entity.ridingEntity).parent)));
	//	}

	public static BasicFlightModel getShipRiding(Entity entity)
	{
		if (!(entity.ridingEntity instanceof BasicFlightModel))
			return null;
		return (BasicFlightModel)entity.ridingEntity;
	}

	public static Entity getEntityByUuid(World world, UUID uuid)
	{
		for (Object e : world.getLoadedEntityList())
		{
			Entity entity = (Entity)e;
			if (entity.getUniqueID().equals(uuid))
				return entity;
		}
		return null;
	}

	public static boolean isClientControlled(BasicFlightModel query)
	{
		BasicFlightModel ship = getShipRiding(Client.mc.thePlayer);
		return ship != null && ship.equals(query);
	}
}
