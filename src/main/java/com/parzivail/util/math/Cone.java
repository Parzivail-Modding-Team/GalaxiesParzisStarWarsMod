package com.parzivail.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Cone
{
	/**
	 * @param entities  List of nearby entities
	 * @param startPos  starting position
	 * @param radius    distance cone travels
	 * @param degrees   angle of cone
	 * @param direction direction of the cone
	 *
	 * @return All entities inside the cone
	 */
	public static List<Entity> getEntitiesInCone(List<Entity> entities, Vector3f startPos, float radius, float degrees, Vector3f direction)
	{

		List<Entity> newEntities = new ArrayList<>();
		float squaredRadius = radius * radius;

		for (Entity e : entities)
		{
			Vector3f relativePosition = new Vector3f((float)e.posX, (float)e.posY + e.getEyeHeight(), (float)e.posZ);
			relativePosition = Vector3f.sub(relativePosition, startPos, null);
			if (relativePosition.lengthSquared() > squaredRadius)
				continue;
			if (getAngleBetweenVectors(direction, relativePosition) > degrees)
				continue;
			newEntities.add(e);
		}
		return newEntities;
	}

	public static float getAngleBetween(Entity view, Entity entity)
	{
		Vector3f relativePosition = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
		relativePosition = Vector3f.sub(relativePosition, new Vector3f((float)view.posX, (float)view.posY + view.getEyeHeight(), (float)view.posZ), null);
		Vec3 look = view.getLookVec();
		return getAngleBetweenVectors(new Vector3f((float)look.xCoord, (float)look.yCoord, (float)look.zCoord), relativePosition);
	}

	private static float getAngleBetweenVectors(Vector3f v1, Vector3f v2)
	{
		return Math.abs((float)Math.toDegrees(Vector3f.angle(v1, v2)));
	}
}
