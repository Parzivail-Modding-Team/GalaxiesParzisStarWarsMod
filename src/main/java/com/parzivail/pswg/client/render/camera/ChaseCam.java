package com.parzivail.pswg.client.render.camera;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ChaseCam
{
	public Vec3d prevPos;
	public Vec3d pos;

	public ChaseCam()
	{
		prevPos = new Vec3d(Double.NaN, Double.NaN, Double.NaN);
		pos = new Vec3d(Double.NaN, Double.NaN, Double.NaN);
	}

	public void tick(ShipEntity parent)
	{
		if (parent == null || parent.isRemoved())
			return;

		if (Double.isNaN(pos.x) || Double.isNaN(pos.y) || Double.isNaN(pos.z))
			pos = parent.getPos();

		prevPos = new Vec3d(pos.x, pos.y, pos.z);

		var world = parent.world;

		var lerpAmount = parent.getCameraLerp();

		var q = parent.getViewRotation(1);

		var camDistTarget = getCamDistTarget(parent, q);

		var camTargetPosition = parent.getPos().add(QuatUtil.rotate(new Vec3d(0, 0, camDistTarget), q));
		var camDpos = camTargetPosition.subtract(pos);

		var lerpPos = pos.add(camDpos.multiply(lerpAmount));

		var parentPos = parent.getPos();
		var dist = lerpPos.distanceTo(parentPos);
		var targetDist = dist;

		for (var i = 0; i < 8; ++i)
		{
			var f = (float)((i & 1) * 2 - 1);
			var g = (float)((i >> 1 & 1) * 2 - 1);
			var h = (float)((i >> 2 & 1) * 2 - 1);

			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;

			var start = parentPos.add(f, g, h);
			var end = lerpPos.add(f, g, h);
			HitResult hitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, parent));

			if (hitResult.getType() != HitResult.Type.MISS)
			{
				var d = hitResult.getPos().distanceTo(parentPos);
				if (d < dist)
					dist = (float)d;
			}
		}

		pos = MathUtil.lerp((float)(dist / targetDist), parentPos, lerpPos);
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

		var baseMult = MathHelper.clamp(Resources.CONFIG.get().view.shipCameraBaseDistance, 0.1f, 10f);
		var speedMult = MathHelper.clamp(Resources.CONFIG.get().view.shipCameraSpeedDistance, 0.1f, 10f);

		return scalar * (13 * baseMult + 3 * speedMult * throttle);
	}
}
