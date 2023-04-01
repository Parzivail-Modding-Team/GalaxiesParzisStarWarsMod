package com.parzivail.pswg.entity.ship;

import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Quaternionf;

public class SpeederEntity extends ShipEntity
{
	private record PathfindResult(boolean success, double height)
	{
	}

	private final float yawVelocityDecay = 0.6f;
	private float yawVelocity = 0;

	public SpeederEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	protected double getRepulsorSetpoint()
	{
		return 0;
	}

	@Override
	public boolean usePlayerPerspective()
	{
		return true;
	}

	@Override
	public boolean useMouseInput(PlayerEntity player)
	{
		return false;
	}

	@Override
	public void tick()
	{
		super.tick();

		var pilot = getControllingPassenger();
		if (pilot instanceof PlayerEntity pe)
		{
			if (pe.sidewaysSpeed > 0)
				yawVelocity += 1.75f;
			else if (pe.sidewaysSpeed < 0)
				yawVelocity -= 1.75f;
		}

		var rotation = new Quaternionf(getRotation());

		var v = QuatUtil.project(MathUtil.V3D_POS_Y, rotation);
		rotation.rotateAxis(MathUtil.toRadians(yawVelocity), v.toVector3f());

		setRotation(rotation);

		if (world.isClient)
		{
			clientInstRotation = new Quaternionf(rotation);
		}

		for (var p : getPassengerList())
			p.setYaw(p.getYaw() - yawVelocity);

		yawVelocity *= yawVelocityDecay;

		var result = getMaxHeightInPatch(getPos(), 1 + getThrottle(), 2, 3);
		if (result.success)
		{
			var d = result.height;

			var setpoint = getRepulsorSetpoint();
			if (Math.abs(d - setpoint) < 0.05f)
				d = setpoint;

			this.move(MovementType.SELF, new Vec3d(0, (d - setpoint) / 5f, 0));
		}
	}

	protected PathfindResult getMaxHeightInPatch(Vec3d start, double spacingForward, double spacingSideways, double range)
	{
		var d = -Double.MAX_VALUE;
		var success = false;

		var yaw = getYaw();
		var left = new Vec3d(Math.cos(yaw / 180 * Math.PI), 0, Math.sin(yaw / 180 * Math.PI));

		yaw += 90;
		var forward = new Vec3d(Math.cos(yaw / 180 * Math.PI), 0, Math.sin(yaw / 180 * Math.PI));

		var invSidewaysSpacing = MathHelper.clamp(1 / spacingForward, 0.2, 1);

		for (var x = -1; x <= 1; x++)
		{
			for (double z = 0; z <= 1; z += invSidewaysSpacing)
			{
				var pos = start.add(left.multiply(x * spacingSideways)).add(forward.multiply(z * spacingForward * 3)).add(0, range, 0);

				if (!world.isAir(new BlockPos(MathUtil.floorInt(pos))))
					continue;

				var blockHit = EntityUtil.raycastBlocks(pos, MathUtil.V3D_NEG_Y, range * 2, this, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY);
				var blockDistance = blockHit.getType() == HitResult.Type.MISS ? -range : (blockHit.getPos().y - start.y);

				if (blockDistance > d)
				{
					d = blockDistance;
					success = true;
				}
			}
		}

		return new PathfindResult(success, success ? d : 0);
	}

	@Override
	public boolean acceptLeftClick(PlayerEntity player)
	{
		return false;
	}

	@Override
	protected Vec3d getThrottleVelocity(float throttle)
	{
		var d = QuatUtil.rotate(MathUtil.V3D_NEG_Z, getRotation());
		d = new Vec3d(d.x, 0.02f, d.z);
		return d.multiply(throttle / 2);
	}

	@Override
	protected boolean allowPitchMovement()
	{
		return false;
	}

	@Override
	public float getCameraLerp()
	{
		return 0.75f;
	}
}
