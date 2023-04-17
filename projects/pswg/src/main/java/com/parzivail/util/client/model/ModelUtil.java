package com.parzivail.util.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Optional;

public class ModelUtil
{
	public static Optional<ModelPart> getChild(ModelPart root, String name)
	{
		if (root.hasChild(name))
			return Optional.of(root.getChild(name));
		return Optional.empty();
	}

	public static Box getBounds(Collection<Vector3f> verts)
	{
		var min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		var max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

		for (var v : verts)
		{
			if (v.x < min.x)
				min.set(v.x, min.y, min.z);
			if (v.y < min.y)
				min.set(min.x, v.y, min.z);
			if (v.z < min.z)
				min.set(min.x, min.y, v.z);

			if (v.x > max.x)
				max.set(v.x, max.y, max.z);
			if (v.y > max.y)
				max.set(max.x, v.y, max.z);
			if (v.z > max.z)
				max.set(max.x, max.y, v.z);
		}

		return new Box(new Vec3d(min), new Vec3d(max));
	}
}
