package com.parzivail.swg.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.swg.network.MessageSeatInit;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
	public float throttle;

	public ShipData data;
	public Seat[] seats;

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
		createData();
	}

	protected void createSeats()
	{
		seats = new Seat[0];
	}

	protected void createData()
	{
		data = new ShipData();
	}

	public void spawnSeats()
	{
		if (worldObj.isRemote)
			return;

		for (int i = 0; i < seats.length; i++)
		{
			seats[i].setLocationAndAngles(this);
			this.worldObj.spawnEntityInWorld(seats[i]);
			StarWarsGalaxy.network.sendToAll(new MessageSeatInit(this, seats[i], i));
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

	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
	{
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer player)
	{
		if (this.riddenByEntity == null)
		{
			if (!this.worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		this.setDead();
		return true;
	}

	@Override
	public void setDead()
	{
		for (Seat s : seats)
			s.setDead();
		super.setDead();
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		previousOrientation = orientation.clone();

		if (this.worldObj.isRemote)
			KeyHandler.handleVehicleMovement();

		//if (!this.onGround)
		//	this.motionY -= 9.8f / 100f;

		Vector3f forward = this.orientation.findLocalVectorGlobally(new Vector3f(0, 0, -1));
		//Lumberjack.log(this.throttle);
		this.moveEntity(this.motionX + forward.x * throttle, this.motionY + forward.y * throttle, this.motionZ + forward.z * throttle);
		//this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.orientation.rotateLocalPitch(this.angularMomentum.x);
		this.orientation.rotateLocalYaw(this.angularMomentum.y);
		this.orientation.rotateLocalRoll(this.angularMomentum.z);

		this.angularMomentum.x *= 0.7;
		this.angularMomentum.y *= 0.7;
		this.angularMomentum.z *= 0.7;

		if (Math.abs(this.angularMomentum.x) < 0.001f)
			this.angularMomentum.x = 0;
		if (Math.abs(this.angularMomentum.y) < 0.001f)
			this.angularMomentum.y = 0;
		if (Math.abs(this.angularMomentum.z) < 0.001f)
			this.angularMomentum.z = 0;

		//for (Seat s : seats)
		//	s.setLocationAndAngles(this);
		Lumberjack.log("%s\t%s", forward, throttle);

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}
}
