package com.parzivail.pswg.entity;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.util.EntityUtil;
import com.parzivail.pswg.util.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.options.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ChaseCamEntity extends Entity
{
	public ShipEntity parent;

	public ChaseCamEntity(EntityType<?> type, World world)
	{
		super(type, world);
		this.inanimate = true;
	}

	@Override
	protected void initDataTracker()
	{

	}

	public void setParent(ShipEntity entity)
	{
		parent = entity;
		setPos(entity.getX(), entity.getY(), entity.getZ());
	}

	@Override
	public void tick()
	{
		super.tick();

		if (parent == null || parent.removed)
		{
			kill();
			return;
		}

		Quaternion q = parent.getRotation();

		float camDistTarget = getCamDistTarget();
		Vec3d camTargetPosition = parent.getPos().add(QuatUtil.rotate(new Vec3d(0, 0, camDistTarget), q));
		Vec3d camDpos = camTargetPosition.subtract(getPos());

		float lerpAmount = 0.4f;

		Vec3d lerpPos = getPos().add(camDpos.multiply(lerpAmount));
		BlockHitResult result = this.world.raycast(new RaycastContext(parent.getPos(), lerpPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, parent));

		Vec3d resultPos = MathUtil.lerp(0.98f, parent.getPos(), result.getPos());
		updatePosition(resultPos.x, resultPos.y, resultPos.z);

		EntityUtil.updateEulerRotation(this, q);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag)
	{

	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag)
	{

	}

	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return getHeight() / 2f;
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return null;
	}

	private float getCamDistTarget()
	{
		Perspective perspective = Client.minecraft.options.getPerspective();
		int scalar = 1;
		if (perspective == Perspective.FIRST_PERSON)
			return 0;
		else if (perspective == Perspective.THIRD_PERSON_FRONT)
			scalar = -1;
		float throttle = 1;
		return scalar * (10 + 3 * throttle);
	}
}
