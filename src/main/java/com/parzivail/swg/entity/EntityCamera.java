package com.parzivail.swg.entity;

import com.parzivail.swg.proxy.SwgClientProxy;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityCamera extends Entity
{
	private Entity target;
	private float trailingDistance = 4;

	public EntityCamera(World worldIn)
	{
		super(worldIn);
		this.setSize(1, 1);
		noClip = true;
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{

	}

	public void setTarget(Entity target)
	{
		this.target = target;
		this.setSize(target.width, target.height);
	}

	public float getEyeHeight()
	{
		if (this.target == null)
			return this.height * 0.5f;
		return this.target.getEyeHeight();
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		if (target != null)
		{
			float stiffness = 0.4f;

			Vector4f offset = new Vector4f();

			trailingDistance = 8;

			if (target instanceof IFreeRotator)
			{
				float currentOffset = 0;
				if (SwgClientProxy.mc.gameSettings.thirdPersonView != 0)
					currentOffset = 4;

				Matrix4f rotation = ((IFreeRotator)target).getRotationMatrix();
				Matrix4f.transform(rotation, new Vector4f(0, 0, trailingDistance - currentOffset, 0), offset);
			}

			double targetX = target.posX - offset.x;
			double targetY = target.posY - offset.y;
			double targetZ = target.posZ - offset.z;

			this.posX += (targetX - this.posX) * stiffness;
			this.posY += (targetY - this.posY) * stiffness;
			this.posZ += (targetZ - this.posZ) * stiffness;

			this.rotationYaw = target.rotationYaw;
			this.rotationPitch = target.rotationPitch;
		}

		if (this.posY < -64.0D)
		{
			this.outOfWorld();
		}

		this.firstUpdate = false;
	}
}
