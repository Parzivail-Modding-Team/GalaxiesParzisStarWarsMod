package com.parzivail.swg.util;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.ship.EntitySeat;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class SwgEntityUtil
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
	 * Registers an Entity with FML
	 *
	 * @param entityClass The class to register
	 * @param entityName  The name of the entity
	 */
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName)
	{
		while (EntityList.getClassFromID(mobId) != null)
			mobId++;
		EntityRegistry.registerModEntity(entityClass, entityName, mobId, StarWarsGalaxy.instance, 80, 10, false);
		EntityList.idToClassMap.put(Integer.valueOf(mobId), entityClass);
		Lumberjack.debug("Registered entity \"" + entityName + "\" as ID " + mobId);
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
			mobId++;
		EntityRegistry.registerModEntity(mobClass, mobName, mobId, StarWarsGalaxy.instance, 80, 1, true);
		EntityList.idToClassMap.put(Integer.valueOf(mobId), mobClass);
		EntityList.entityEggs.put(Integer.valueOf(mobId), new EntityList.EntityEggInfo(mobId, bgColor, fgColor));
		Lumberjack.debug("Registered entity (and egg) \"" + mobName + "\" as ID " + mobId);
	}

	public static EntityShip getShipRiding(Entity entity)
	{
		if (entity == null)
			return null;

		if (!(entity.ridingEntity instanceof EntitySeat))
			return null;
		return ((EntitySeat)entity.ridingEntity).getParent();
	}
}
