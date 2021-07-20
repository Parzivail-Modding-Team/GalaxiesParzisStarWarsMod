package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SpeederEntity extends ShipEntity
{
	public SpeederEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	protected double getRepulsorSetpoint()
	{
		return -0.5;
	}

	@Override
	public boolean usePlayerPerspective()
	{
		return true;
	}

	@Override
	public void tick()
	{
		super.tick();

		var d = getMaxHeightInPatch(getPos(), 1 + getThrottle(), 2, 5);
		var setpoint = getRepulsorSetpoint();
		if (Math.abs(d - setpoint) < 0.05f)
			d = setpoint;

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
				var pos = start.add(left.multiply(x * spacingSideways)).add(forward.multiply(z * spacingForward * 3));

				world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);

				var blockHit = EntityUtil.raycastBlocks(pos, MathUtil.NEGY, range * 2, this, RaycastContext.FluidHandling.SOURCE_ONLY);
				var blockDistance = blockHit.getType() == HitResult.Type.MISS ? -range : (blockHit.getPos().y - start.y);

				if (blockDistance > d)
					d = blockDistance;
			}
		}

		return d;
	}

	@Override
	protected Vec3d getThrottleVelocity(float throttle)
	{
		var d = QuatUtil.rotate(MathUtil.NEGZ, getRotation());
		d = new Vec3d(d.x, 0.02f, d.z);
		return d.multiply(throttle / 2);
	}

	@Override
	public float getCameraLerp()
	{
		return 0.75f;
	}
}
