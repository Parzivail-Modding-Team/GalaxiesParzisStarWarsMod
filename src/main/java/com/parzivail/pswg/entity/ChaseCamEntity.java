package com.parzivail.pswg.entity;

import com.parzivail.pswg.util.EntityUtil;
import com.parzivail.pswg.util.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
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

		if (parent == null)
		{
			kill();
			return;
		}

		Quaternion q = parent.getRotation();

		float camDistTarget = getCamDistTarget();
		Vec3d camTargetPosition = parent.getPos().add(MathUtil.rotate(new Vec3d(0, 0, camDistTarget), q));
		Vec3d camDpos = camTargetPosition.subtract(getPos());

		float lerpAmount = 0.4f;

		Vec3d lerpPos = getPos().add(camDpos.multiply(lerpAmount));
		BlockHitResult result = this.world.rayTrace(new RayTraceContext(parent.getPos(), lerpPos, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, parent));

		Vec3d resultPos = MathUtil.lerp(0.98f, parent.getPos(), result.getPos());
		setPos(resultPos.x, resultPos.y, resultPos.z);

		Box box = getBoundingBox();
		Vec3d boxCenter = box.getCenter();
		Vec3d pos = getPos();
		this.setBoundingBox(box.offset(pos.subtract(boxCenter)).offset(0, box.getYLength() / 2, 0));

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
		return 0.0F;
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return null;
	}

	private float getCamDistTarget()
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		int thirdPerson = mc.options.perspective;
		int scalar = 1;
		if (thirdPerson == 0)
			return 0;
		else if (thirdPerson == 2)
			scalar = -1;
		float throttle = 1;
		return scalar * (10 + 3 * throttle);
	}
}
