package com.parzivail.swg.entity;

import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCinematicCamera extends EntityLivingBase
{
	public EntityShipParentTest parent;

	public EntityCinematicCamera(World world)
	{
		super(world);
		setSize(0F, 0F);
	}

	public EntityCinematicCamera(EntityShipParentTest d)
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

		Vector3f cameraPosition = new Vector3f(0, 0, -15);
		cameraPosition = parent.orientation.findLocalVectorGlobally(cameraPosition);

		double dX = parent.posX + cameraPosition.x - posX;
		double dY = parent.posY + cameraPosition.y - posY;
		double dZ = parent.posZ + cameraPosition.z - posZ;

		float lerpAmount = 0.5F;

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
