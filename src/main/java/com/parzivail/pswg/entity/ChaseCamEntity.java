package com.parzivail.pswg.entity;

import com.parzivail.pswg.util.EntityUtil;
import com.parzivail.pswg.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
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

		Quaternion q = parent.getRotation();

		float camDist = getCamDist(0.5f);
		Vec3d cameraPosition = MathUtil.rotate(new Vec3d(0, 0, camDist), q);

		double dX = parent.getX() + cameraPosition.x - getX();
		double dY = parent.getY() + cameraPosition.y - getY();
		double dZ = parent.getZ() + cameraPosition.z - getZ();

		float lerpAmount = 0.35f;

		setPos(getX() + dX * lerpAmount, getY() + dY * lerpAmount, getZ() + dZ * lerpAmount);

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

	public float getCamDist(float partialTicks)
	{
		float tempDistance = getCamDistTarget(partialTicks);

		//		Vector3f cameraPosition = new Vector3f(0, 0, -tempDistance);
		//		cameraPosition = parent.orientation.findLocalVectorGlobally(cameraPosition);
		//
		//		ShipData data = parent.getData();
		//		double d0 = parent.prevPosX + (parent.posX - parent.prevPosX) * partialTicks;
		//		double d1 = parent.prevPosY + (parent.posY - parent.prevPosY) * partialTicks - data.verticalCenteringOffset;
		//		double d2 = parent.prevPosZ + (parent.posZ - parent.prevPosZ) * partialTicks;
		//
		//		double d3 = cameraPosition.x;
		//		double d4 = cameraPosition.y;
		//		double d5 = cameraPosition.z;
		//
		//		for (int k = 0; k < 8; ++k)
		//		{
		//			float f3 = (float)((k & 1) * 2 - 1);
		//			float f4 = (float)((k >> 1 & 1) * 2 - 1);
		//			float f5 = (float)((k >> 2 & 1) * 2 - 1);
		//			f3 *= 0.1F;
		//			f4 *= 0.1F;
		//			f5 *= 0.1F;
		//			MovingObjectPosition movingobjectposition = Client.mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), Vec3.createVectorHelper(d0 - d3 + (double)f3 + (double)f5, d1 - d5 + (double)f4, d2 - d4 + (double)f5));
		//
		//			if (movingobjectposition != null)
		//			{
		//				double d6 = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(d0, d1, d2));
		//
		//				if (d6 < tempDistance)
		//					tempDistance = (float)d6;
		//			}
		//		}

		return tempDistance;
	}

	private float getCamDistTarget(float partialTicks)
	{
		return 20;
		//		int thirdPerson = Client.mc.gameSettings.thirdPersonView;
		//		int scalar = 1;
		//		if (thirdPerson == 0)
		//			return 0;
		//		else if (thirdPerson == 2)
		//			scalar = -1;
		//		float throttle = parent.slidingThrottle.getOldAverage() + (parent.slidingThrottle.getAverage() - parent.slidingThrottle.getOldAverage()) * partialTicks;
		//		return scalar * (10 + 3 * throttle);
	}
}
