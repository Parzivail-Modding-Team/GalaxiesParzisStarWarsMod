package com.parzivail.util.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
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

	public static <T extends LivingEntity> void lerpLeftArmTo(BipedEntityModel<T> model, float delta, float pitch, float yaw, float roll)
	{
		model.leftArm.pitch = MathHelper.lerp(delta, model.leftArm.pitch, pitch);
		model.leftArm.yaw = MathHelper.lerp(delta, model.leftArm.yaw, yaw);
		model.leftArm.roll = MathHelper.lerp(delta, model.leftArm.roll, roll);
	}

	public static <T extends LivingEntity> void lerpRightArmTo(BipedEntityModel<T> model, float delta, float pitch, float yaw, float roll)
	{
		model.rightArm.pitch = MathHelper.lerp(delta, model.rightArm.pitch, pitch);
		model.rightArm.yaw = MathHelper.lerp(delta, model.rightArm.yaw, yaw);
		model.rightArm.roll = MathHelper.lerp(delta, model.rightArm.roll, roll);
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
