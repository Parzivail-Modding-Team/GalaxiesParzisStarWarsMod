package com.parzivail.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;

public class TwoJointIk
{
	public record Result(Vec3d kneePos, Vec3d footPos, double hipYaw, double hipPitch, double kneePitch)
	{
	}

	public static Result forwardEvaluate(Entity entity, Vec3d hip, boolean backwardsKnee, double hipYaw, double upperLegLength, double lowerLegLength)
	{
		var entityPos = entity.getPos();

		var lTotal = upperLegLength + lowerLegLength;

		var world = entity.world;
		var result = world.raycast(new RaycastContext(hip.add(entityPos), hip.add(entityPos).subtract(0, lTotal, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
		if (result.getType() == HitResult.Type.MISS)
			return new Result(Vec3d.ZERO, Vec3d.ZERO, hipYaw, 0, 0);

		var footPos = result.getPos().subtract(entityPos);
		var dGround = hip.y - footPos.y - lowerLegLength;
		var hipPitch = 90 - (Math.asin(dGround / upperLegLength)) / Math.PI * 180;

		var kneePos = footPos.add(0, lowerLegLength, 0);

		return new Result(kneePos, footPos, hipYaw, hipPitch, hipPitch);
	}

	public static Result backEvaluate(Entity entity, Vec3d hip, Vec3d footRequest, boolean backwardsKnee, double upperLegLength, double lowerLegLength)
	{
		var entityPos = entity.getPos();

		var l1 = upperLegLength;
		var l2 = lowerLegLength;
		var lTotal = l1 + l2;

		var hipYaw = hip.x == footRequest.x && hip.z == footRequest.z ? entity.getYaw() : MathHelper.atan2(footRequest.z - hip.z, footRequest.x - hip.x) / Math.PI * 180 - 90;

		if (hip.distanceTo(footRequest) >= lTotal)
			return getFullyExtendedResult(hip, footRequest, hipYaw);

		var world = entity.world;
		var result = world.raycast(new RaycastContext(footRequest.add(entityPos), footRequest.add(entityPos).subtract(0, lTotal, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
		if (result.getType() == HitResult.Type.MISS)
			return getFullyExtendedResult(hip, footRequest, hipYaw);

		footRequest = result.getPos().subtract(entityPos);

		footRequest = footRequest.subtract(hip);

		var endX = footRequest.method_37267(); // method_37267 = horizontalLength
		var endY = footRequest.y;

		var lSquareDiff = l1 * l1 - l2 * l2;
		var lSquareSum = l1 * l1 + l2 * l2;
		var dSquare = footRequest.lengthSquared();
		var coef = lSquareDiff / (2 * dSquare);
		var coef2 = Math.sqrt(2 * (lSquareSum / dSquare) - lSquareDiff * lSquareDiff / (dSquare * dSquare) - 1) / 2f;

		var opposite = backwardsKnee ? -1 : 1;
		var intersectionX = endX / 2 + coef * endX - opposite * coef2 * endY;
		var intersectionY = endY / 2 + coef * endY + opposite * coef2 * endX;

		var hipPitch = MathHelper.atan2(intersectionY, intersectionX) / Math.PI * 180;
		var kneePitch = hipPitch - MathHelper.atan2(endY - intersectionY, endX - intersectionX) / Math.PI * 180;

		var hipYawRad = hipYaw / 180 * Math.PI;

		var kneePos = new Vec3d(intersectionX * Math.cos(hipYawRad), intersectionY, intersectionX * Math.sin(hipYawRad));
		return new Result(kneePos, footRequest, hipYaw, 90 + hipPitch, kneePitch);
	}

	@NotNull
	private static TwoJointIk.Result getFullyExtendedResult(Vec3d hip, Vec3d footRequest, double hipYaw)
	{
		var deltaPos = footRequest.subtract(hip);
		var fullyExtendedHipPitch = MathHelper.atan2(deltaPos.y, deltaPos.method_37267());
		return new Result(Vec3d.ZERO, Vec3d.ZERO, hipYaw, fullyExtendedHipPitch, 0);
	}
}
