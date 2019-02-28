package com.parzivail.swg.entity;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCinematicCamera extends EntityLivingBase
{
	public EntityShip parent;

	public EntityCinematicCamera(World world)
	{
		super(world);
		setSize(0F, 0F);
	}

	public EntityCinematicCamera(EntityShip d)
	{
		this(d.worldObj);
		parent = d;
		setPosition(d.posX, d.posY, d.posZ);
	}

	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		float camDist = getCamDist(0.5f);
		Vector3f cameraPosition = new Vector3f(0, 0, -camDist);
		cameraPosition = parent.orientation.findLocalVectorGlobally(cameraPosition);

		double dX = parent.posX + cameraPosition.x - posX;
		double dY = parent.posY + cameraPosition.y - posY;
		double dZ = parent.posZ + cameraPosition.z - posZ;

		float lerpAmount = 0.8F;

		setPosition(posX + dX * lerpAmount, posY + dY * lerpAmount, posZ + dZ * lerpAmount);

		rotationYaw = -parent.orientation.getYaw();
		rotationPitch = -parent.orientation.getPitch();

		while (rotationYaw - prevRotationYaw < -180.0F)
			prevRotationYaw -= 360.0F;

		while (rotationYaw - prevRotationYaw >= 180.0F)
			prevRotationYaw += 360.0F;

		while (renderYawOffset - prevRenderYawOffset < -180.0F)
			prevRenderYawOffset -= 360.0F;

		while (renderYawOffset - prevRenderYawOffset >= 180.0F)
			prevRenderYawOffset += 360.0F;

		while (rotationPitch - prevRotationPitch < -180.0F)
			prevRotationPitch -= 360.0F;

		while (rotationPitch - prevRotationPitch >= 180.0F)
			prevRotationPitch += 360.0F;
	}

	public float getCamDist(float partialTicks)
	{
		float tempDistance = getCamDistTarget(partialTicks);

		//		Vector3f cameraPosition = new Vector3f(0, 0, -tempDistance);
		//		cameraPosition = parent.orientation.findLocalVectorGlobally(cameraPosition);
		//
		//		Vec3 shipPos = Vec3.createVectorHelper(parent.posX, parent.posY, parent.posZ);
		//		Vec3 camPos = Vec3.createVectorHelper(parent.posX + cameraPosition.x, parent.posY + cameraPosition.y, parent.posZ + cameraPosition.z);
		//		MovingObjectPosition mop = worldObj.rayTraceBlocks(camPos, shipPos);
		//
		//		if (mop != null)
		//		{
		//			double mopDistance = mop.hitVec.distanceTo(shipPos);
		//			if (mopDistance < tempDistance)
		//				return (float)mopDistance;
		//		}

		return tempDistance;
	}

	private float getCamDistTarget(float partialTicks)
	{
		float throttle = parent.slidingThrottle.getOldAverage() + (parent.slidingThrottle.getAverage() - parent.slidingThrottle.getOldAverage()) * partialTicks;
		return 10 + 3 * throttle;
	}

	@Override
	public ItemStack getHeldItem()
	{
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int p_71124_1_)
	{
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
	{

	}

	@Override
	public ItemStack[] getInventory()
	{
		return new ItemStack[0];
	}
}
