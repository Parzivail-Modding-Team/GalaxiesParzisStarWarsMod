package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SpeederEntity extends ShipEntity
{
	private final float yawVelocityDecay = 0.6f;
	private float yawVelocity = 0;

	public SpeederEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	protected double getRepulsorSetpoint()
	{
		return -0.4;
	}

	@Override
	protected int getMaxPassengers()
	{
		return 2;
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

		var d = getMaxHeightInPatch(getPos(), 1 + getThrottle(), 2, 3);
		var setpoint = getRepulsorSetpoint();
		if (Math.abs(d - setpoint) < 0.05f)
			d = setpoint;

		var pilot = getPrimaryPassenger();
		if (pilot instanceof PlayerEntity pe)
		{
			if (pe.sidewaysSpeed > 0)
				yawVelocity += 1.75f;
			else if (pe.sidewaysSpeed < 0)
				yawVelocity -= 1.75f;
		}

		var rotation = new Quaternion(getRotation());

		var v = QuatUtil.project(com.parzivail.util.math.MathUtil.POSY, rotation);
		rotation.hamiltonProduct(new Quaternion(new Vec3f(v), yawVelocity, true));

		setRotation(rotation);

		if (world.isClient)
		{
			clientInstRotation = new Quaternion(rotation);
		}

		for (var p : getPassengerList())
			p.setYaw(p.getYaw() - yawVelocity);

		yawVelocity *= yawVelocityDecay;

		this.move(MovementType.SELF, new Vec3d(0, (d - setpoint) / 5f, 0));
	}

	protected double getMaxHeightInPatch(Vec3d start, double spacingForward, double spacingSideways, double range)
	{
		var d = -Double.MAX_VALUE;

		var yaw = getYaw();
		var left = new Vec3d(Math.cos(yaw / 180 * Math.PI), 0, Math.sin(yaw / 180 * Math.PI));

		yaw += 90;
		var forward = new Vec3d(Math.cos(yaw / 180 * Math.PI), 0, Math.sin(yaw / 180 * Math.PI));

		var invSidewaysSpacing = spacingForward > 1 ? 1 / spacingForward : 1;

		for (var x = -1; x <= 1; x++)
		{
			for (double z = 0; z <= 1; z += invSidewaysSpacing)
			{
				var pos = start.add(left.multiply(x * spacingSideways)).add(forward.multiply(z * spacingForward * 3)).add(0, range, 0);

				if (!world.isAir(new BlockPos(pos)))
					continue;

				var blockHit = EntityUtil.raycastBlocks(pos, MathUtil.NEGY, range * 2, this, RaycastContext.FluidHandling.SOURCE_ONLY);
				var blockDistance = blockHit.getType() == HitResult.Type.MISS ? -range : (blockHit.getPos().y - start.y);

				if (blockDistance > d)
					d = blockDistance;
			}
		}

		return d;
	}

	@Override
	public Vec3d getPassengerSocket(int passengerIndex)
	{
		if (passengerIndex > 0)
			return new Vec3d(0.5f, 0.1f, 1.25f);
		return new Vec3d(-0.5f, 0.1f, 1.25f);
	}

	@Override
	protected Vec3d getThrottleVelocity(float throttle)
	{
		var d = QuatUtil.rotate(MathUtil.NEGZ, getRotation());
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
