package com.parzivail.swg.ship;

import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.common.Pair;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RotatedAxes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by colby on 12/26/2017.
 */
public abstract class BasicFlightModel extends EntityBase
{
	private static final int DATA_SEATID = 12;
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;

	private ArrayList<Pair<Integer, UUID>> searchingSeats;

	/**
	 * Distance from the bottom of the model to the "center" of it, from which it will rotate in a roll
	 */
	public float verticalCenteringOffset;
	/**
	 * Distance we need to translate the model up to make sure the bottom is in line with the entity bottom
	 */
	public float verticalGroundingOffset;

	public ShipData data;
	protected Seat[] seats;
	protected Vector3f[] seatOffsets;

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

		for (int i = 0; i < this.seats.length; i++)
			this.dataWatcher.addObject(DATA_SEATID + i, 0);

		createData();
	}

	protected void setPivots(float verticalCenteringOffset, float verticalGroundingOffset)
	{
		this.verticalCenteringOffset = verticalCenteringOffset;
		this.verticalGroundingOffset = verticalGroundingOffset;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
	}

	protected void createSeats()
	{
		seats = new Seat[0];
	}

	protected void createData()
	{
		data = new ShipData();
	}

	public Seat getSeat(int seatIdx)
	{
		return (Seat)this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(DATA_SEATID + seatIdx));
	}

	public void setSeat(int seatIdx, Seat seat)
	{
		this.dataWatcher.updateObject(DATA_SEATID + seatIdx, seat.getEntityId());
	}

	public int getNumSeats()
	{
		return seats.length;
	}

	public void spawnSeats()
	{
		if (worldObj.isRemote)
			return;

		for (int i = 0; i < seats.length; i++)
		{
			seats[i].setLocationAndAngles(this);
			this.worldObj.spawnEntityInWorld(seats[i]);
			this.dataWatcher.updateObject(DATA_SEATID + i, seats[i].getEntityId());
			//StarWarsGalaxy.network.sendToAll(new MessageSeatInit(this, seats[i], i));
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
		for (int i = 0; i < this.seats.length; i++)
		{
			NBTTagCompound seat = new NBTTagCompound();
			seat.setLong("most", this.seats[i].getUniqueID().getMostSignificantBits());
			seat.setLong("least", this.seats[i].getUniqueID().getLeastSignificantBits());
			tag.setTag("seat" + i, seat);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
	{
		for (int i = 0; i < this.seats.length; i++)
		{
			NBTTagCompound seat = tag.getCompoundTag("seat" + i);
			long most = seat.getLong("most");
			long least = seat.getLong("least");
			UUID uuid = new UUID(most, least);
			Entity eSeat = EntityUtils.getEntityByUuid(this.worldObj, uuid);
			if (eSeat != null)
			{
				Lumberjack.debug("Found seat %s at NBT load.", i);
				this.seats[i] = (Seat)eSeat;
				this.dataWatcher.updateObject(DATA_SEATID + i, eSeat.getEntityId());
			}
			else
			{
				if (searchingSeats == null)
					searchingSeats = new ArrayList<>();
				searchingSeats.add(new Pair<>(i, uuid));
			}
		}
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
		for (int i = 0; i < this.seats.length; i++)
			this.getSeat(i).setDead();
		super.setDead();
	}

	@Override
	public void onUpdate()
	{
		if (this.posY < -64.0D)
			this.setDead();

		this.prevPosX = this.lastTickPosX = this.posX;
		this.prevPosY = this.lastTickPosY = this.posY;
		this.prevPosZ = this.lastTickPosZ = this.posZ;

		this.rotationYaw = 180 - this.orientation.getYaw();
		this.rotationPitch = this.orientation.getPitch();
		this.prevRotationYaw = 180 - this.previousOrientation.getYaw();
		this.prevRotationPitch = this.previousOrientation.getPitch();

		previousOrientation = orientation.clone();

		if (this.worldObj.isRemote && EntityUtils.isClientControlled(this))
			KeyHandler.handleVehicleMovement();

		if (searchingSeats != null && searchingSeats.size() > 0)
		{
			for (Iterator<Pair<Integer, UUID>> i = searchingSeats.iterator(); i.hasNext(); )
			{
				Pair<Integer, UUID> pair = i.next();
				Entity eSeat = EntityUtils.getEntityByUuid(this.worldObj, pair.right);
				if (eSeat != null)
				{
					Lumberjack.debug("Found seat %s at update query.", pair.left);
					this.seats[pair.left] = (Seat)eSeat;
					this.seats[pair.left].ship = this;
					this.dataWatcher.updateObject(DATA_SEATID + pair.left, eSeat.getEntityId());
					i.remove();
				}
				else
					Lumberjack.debug("Still lost seat %s at update query.", pair.left);
			}
		}

		for (int i = 0; i < this.seats.length; i++)
		{
			if (this.getSeat(i) == null)
				continue;
			this.getSeat(i).ship = this;
			this.getSeat(i).setLocationAndAngles(this);
		}

		//Lumberjack.log(this.getPosition(0));

		//if (!this.onGround)
		//	this.motionY -= 9.8f / 100f;

		if (this.getSeat(0) != null && this.getSeat(0).riddenByEntity instanceof EntityLivingBase)
		{
			Vector3f forward = this.orientation.findLocalVectorGlobally(new Vector3f(0, 0, -1));
			//Lumberjack.log(this.throttle);
			this.moveEntity(this.motionX + forward.x * throttle, this.motionY + forward.y * throttle, this.motionZ + forward.z * throttle);
		}
		else
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
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

		//Lumberjack.log("%s\t%s", forward, throttle);

		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}
}
