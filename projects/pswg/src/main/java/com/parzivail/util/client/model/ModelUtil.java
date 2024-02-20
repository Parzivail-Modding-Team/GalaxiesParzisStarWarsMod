package com.parzivail.util.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
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

	public static <T extends LivingEntity> void smartLerpArmsRadians(T entity, Hand mainHand, BipedEntityModel<?> model, float delta, float leftPitch, float leftYaw, float leftRoll, float rightPitch, float rightYaw, float rightRoll)
	{
		ModelPart leftArm = model.leftArm;
		ModelPart rightArm = model.rightArm;

		if ((entity.getMainArm() == Arm.LEFT) ^ (mainHand == Hand.OFF_HAND))
		{
			rightYaw = -rightYaw;
			rightRoll = -rightRoll;

			leftYaw = -leftYaw;
			leftRoll = -leftRoll;

			leftArm = model.rightArm;
			rightArm = model.leftArm;
		}

		rightArm.pitch = MathHelper.lerp(delta, rightArm.pitch, rightPitch);
		rightArm.yaw = MathHelper.lerp(delta, rightArm.yaw, rightYaw);
		rightArm.roll = MathHelper.lerp(delta, rightArm.roll, rightRoll);

		leftArm.pitch = MathHelper.lerp(delta, leftArm.pitch, leftPitch);
		leftArm.yaw = MathHelper.lerp(delta, leftArm.yaw, leftYaw);
		leftArm.roll = MathHelper.lerp(delta, leftArm.roll, leftRoll);
	}

	public static <T extends LivingEntity> void lerpLeftArmToDegrees(BipedEntityModel<T> model, float delta, float pitch, float yaw, float roll)
	{
		model.leftArm.pitch = MathHelper.lerpAngleDegrees(delta, model.leftArm.pitch * MathHelper.DEGREES_PER_RADIAN, pitch * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
		model.leftArm.yaw = MathHelper.lerpAngleDegrees(delta, model.leftArm.yaw * MathHelper.DEGREES_PER_RADIAN, yaw * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
		model.leftArm.roll = MathHelper.lerpAngleDegrees(delta, model.leftArm.roll * MathHelper.DEGREES_PER_RADIAN, roll * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
	}

	public static <T extends LivingEntity> void lerpRightArmToDegrees(BipedEntityModel<T> model, float delta, float pitch, float yaw, float roll)
	{
		model.rightArm.pitch = MathHelper.lerpAngleDegrees(delta, model.rightArm.pitch * MathHelper.DEGREES_PER_RADIAN, pitch * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
		model.rightArm.yaw = MathHelper.lerpAngleDegrees(delta, model.rightArm.yaw * MathHelper.DEGREES_PER_RADIAN, yaw * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
		model.rightArm.roll = MathHelper.lerpAngleDegrees(delta, model.rightArm.roll * MathHelper.DEGREES_PER_RADIAN, roll * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
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
