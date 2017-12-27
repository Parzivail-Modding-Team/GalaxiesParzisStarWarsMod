package com.parzivail.swg.ship;

import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/26/2017.
 */
public abstract class BasicFlightModel extends EntityBase
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public Seat[] seats = new Seat[0];

	public BasicFlightModel(World world)
	{
		super(world);

		setSize(0.5F, 0.5F);

		preventEntitySpawning = true;
		ignoreFrustumCheck = true;
		renderDistanceWeight = 200D;

		orientation = previousOrientation = new RotatedAxes();
		angularMomentum = new Vector3f();

		createSeats();
	}

	abstract void createSeats();

	public void spawnSeats()
	{
		for (Seat s : seats)
		{
			s.setLocationAndAngles(this);
			this.worldObj.spawnEntityInWorld(s);
		}
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX - (this.boundingBox.maxX - this.boundingBox.minX) / 2f + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ - (this.boundingBox.maxZ - this.boundingBox.minZ) / 2f + d1);
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
		//orientation = previousOrientation = new RotatedAxes();
	}

	public void acceptInput(ShipInput input)
	{
		// TODO: seats and delegate input to seats
		switch (input)
		{
			case RollLeft:
				this.angularMomentum.z -= 4;
				break;
			case RollRight:
				this.angularMomentum.z += 4;
				break;
			case PitchUp:
				this.angularMomentum.x += 2;
				break;
			case PitchDown:
				this.angularMomentum.x -= 2;
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
