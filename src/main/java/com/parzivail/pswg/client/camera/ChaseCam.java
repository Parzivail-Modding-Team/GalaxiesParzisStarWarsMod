package com.parzivail.pswg.client.camera;

import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ChaseCam
{
	public Vec3d prevPos;
	public Vec3d pos;

	private boolean firstTick = true;

	public ChaseCam()
	{
		prevPos = new Vec3d(0, 0, 0);
		pos = new Vec3d(0, 0, 0);
	}

	public void tick(ShipEntity parent)
	{
		if (parent == null || parent.isRemoved())
			return;

		if (firstTick)
		{
			pos = parent.getPos();

			firstTick = false;
		}

		prevPos = new Vec3d(pos.x, pos.y, pos.z);

		var world = parent.world;

		var lerpAmount = 0.4f;

		var q = parent.getViewRotation(1);

		var camDistTarget = getCamDistTarget(parent, q);

		var camTargetPosition = parent.getPos().add(QuatUtil.rotate(new Vec3d(0, 0, camDistTarget), q));
		var camDpos = camTargetPosition.subtract(pos);

		var lerpPos = pos.add(camDpos.multiply(lerpAmount));
		var result = world.raycast(new RaycastContext(parent.getPos(), lerpPos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, parent));

		var totalDistance = parent.getPos().distanceTo(result.getPos());
		if (totalDistance == 0)
			pos = result.getPos();
		else
			pos = MathUtil.lerp((float)((totalDistance - 0.05) / totalDistance), parent.getPos(), result.getPos());
	}

	private float getCamDistTarget(ShipEntity parent, Quaternion q)
	{
		var minecraft = MinecraftClient.getInstance();

		var perspective = minecraft.options.getPerspective();
		var scalar = 1;
		if (perspective == Perspective.FIRST_PERSON)
			return 0;
		else if (perspective == Perspective.THIRD_PERSON_FRONT)
			scalar = -1;
		var throttle = parent.getThrottle();
		return scalar * (13 + 3 * throttle);
	}
}
