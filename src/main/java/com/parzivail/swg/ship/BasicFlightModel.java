package com.parzivail.swg.ship;

import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

/**
 * Created by colby on 12/26/2017.
 */
public class BasicFlightModel extends Entity
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;

	public BasicFlightModel(World world)
	{
		super(world);

		setSize(1F, 1F);

		preventEntitySpawning = true;
		ignoreFrustumCheck = true;
		renderDistanceWeight = 200D;

		orientation = previousOrientation = new RotatedAxes();
		angularMomentum = new Vector3f();
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public void applyEntityCollision(Entity other)
	{
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer player)
	{
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
			return true;
		else
		{
			if (!this.worldObj.isRemote)
				player.mountEntity(this);

			return true;
		}
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		this.setDead();
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		previousOrientation = orientation.clone();

		if (this.worldObj.isRemote)
			KeyHandler.handleVehicleMovement();

		if (!this.onGround)
			this.motionY -= 9.8f / 100f;

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.orientation.rotateLocalPitch(this.angularMomentum.x);
		this.orientation.rotateLocalYaw(this.angularMomentum.y);
		this.orientation.rotateLocalRoll(this.angularMomentum.z);

		this.angularMomentum.x *= 0.7;
		this.angularMomentum.y *= 0.7;
		this.angularMomentum.z *= 0.7;

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	/**
	 * Tries to moves the entity by the passed in displacement. Args: x, y, z
	 */
	public void moveEntity(double dx, double dy, double dz)
	{
		double tempDX = dx;
		double tempDY = dy;
		double tempDZ = dz;

		List nearAABBs = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(dx, dy, dz));

		for (Object nearAABB : nearAABBs)
		{
			dy = ((AxisAlignedBB)nearAABB).calculateYOffset(this.boundingBox, dy);
			dx = ((AxisAlignedBB)nearAABB).calculateXOffset(this.boundingBox, dx);
			dz = ((AxisAlignedBB)nearAABB).calculateZOffset(this.boundingBox, dz);
		}
		this.boundingBox.offset(dx, dy, dz);

		this.posX += dx;
		this.posY += dy;
		this.posZ += dz;
		this.isCollidedHorizontally = tempDX != dx || tempDZ != dz;
		this.isCollidedVertically = tempDY != dy;
		this.onGround = tempDY != dy && tempDY < 0.0D;
		this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;

		if (tempDX != dx)
			this.motionX = 0.0D;

		if (tempDY != dy)
			this.motionY = 0.0D;

		if (tempDZ != dz)
			this.motionZ = 0.0D;

		try
		{
			this.func_145775_I();
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
	{

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
	{

	}

	public void acceptInput(ShipInput input)
	{
		// TODO: seats and delegate input to seats
		switch (input)
		{
			case RollLeft:
				this.angularMomentum.z += 4;
				break;
			case RollRight:
				this.angularMomentum.z -= 4;
				break;
			case PitchUp:
				//this.parent.angularVelocity.x += 2;
				break;
			case PitchDown:
				//this.parent.angularVelocity.x -= 2;
				break;
			case YawLeft:
				break;
			case YawRight:
				break;
			case ThrottleUp:
				//this.parent.throttle += this.parent.data.maxThrottle * this.parent.data.throttleStep;
				//this.parent.throttle = MathHelper.clamp_float(this.parent.throttle, 0, this.parent.data.maxThrottle);
				break;
			case ThrottleDown:
				//this.parent.throttle -= this.parent.data.maxThrottle * this.parent.data.throttleStep;
				//this.parent.throttle = MathHelper.clamp_float(this.parent.throttle, 0, this.parent.data.maxThrottle);
				break;
			case BlasterFire:
				break;
			case SpecialAesthetic:
				break;
			case SpecialWeapon:
				break;
		}
	}
}
