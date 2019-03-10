package com.parzivail.swg.entity;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.math.RotatedAxes;
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

		RotatedAxes axes = parent.orientation;

		float camDist = getCamDist(0.5f);
		Vector3f cameraPosition = new Vector3f(0, 0, -camDist);
		cameraPosition = axes.findLocalVectorGlobally(cameraPosition);

		double dX = parent.posX + cameraPosition.x - posX;
		double dY = parent.posY + cameraPosition.y - posY;
		double dZ = parent.posZ + cameraPosition.z - posZ;

		float lerpAmount = 0.5f;

		setPosition(posX + dX * lerpAmount, posY + dY * lerpAmount, posZ + dZ * lerpAmount);

		rotationYaw = -axes.getYaw();
		rotationPitch = -axes.getPitch();

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
		int thirdPerson = Client.mc.gameSettings.thirdPersonView;
		int scalar = 1;
		if (thirdPerson == 0)
			return 0;
		else if (thirdPerson == 2)
			scalar = -1;
		float throttle = parent.slidingThrottle.getOldAverage() + (parent.slidingThrottle.getAverage() - parent.slidingThrottle.getOldAverage()) * partialTicks;
		return scalar * (10 + 3 * throttle);
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
