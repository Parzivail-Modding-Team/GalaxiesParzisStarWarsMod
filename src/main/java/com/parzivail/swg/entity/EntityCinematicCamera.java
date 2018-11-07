package com.parzivail.swg.entity;

import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCinematicCamera extends EntityLivingBase
{
	public MultipartFlightModel driveable;

	public EntityCinematicCamera(World world)
	{
		super(world);
		setSize(0F, 0F);
	}

	public EntityCinematicCamera(MultipartFlightModel d)
	{
		this(d.worldObj);
		driveable = d;
		setPosition(d.posX, d.posY, d.posZ);
	}

	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		Vector3f cameraPosition = new Vector3f(0, 0, 15);
		cameraPosition = driveable.orientation.findLocalVectorGlobally(cameraPosition);

		double dX = driveable.posX + cameraPosition.x - posX;
		double dY = driveable.posY + cameraPosition.y - posY;
		double dZ = driveable.posZ + cameraPosition.z - posZ;

		float lerpAmount = 0.3F;

		setPosition(posX + dX * lerpAmount, posY + dY * lerpAmount, posZ + dZ * lerpAmount);

		rotationYaw = 180 - driveable.orientation.getYaw();
		rotationPitch = driveable.orientation.getPitch();
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
