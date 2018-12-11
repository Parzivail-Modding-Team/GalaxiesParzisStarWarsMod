package com.parzivail.swg.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityCinematicCamera;
import com.parzivail.swg.network.MessageFlightModelClientUpdate;
import com.parzivail.swg.network.MessageFlightModelUpdate;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class MultipartFlightModel extends Entity implements IEntityAdditionalSpawnData
{
	@SideOnly(Side.CLIENT)
	public EntityCinematicCamera camera;
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;
	/**
	 * Distance from the bottom of the model to the "center" of it, from which it will rotate in a roll
	 */
	public float verticalCenteringOffset;
	/**
	 * Distance we need to translate the model up to make sure the bottom is in line with the entity bottom
	 */
	public float verticalGroundingOffset;
	public ShipData data;
	private EntitySeat[] seats;
	private UUID[] searchingSeats;
	private int[] clientSearchingSeats;
	private boolean doesClientKnowSeats;
	private SeatData[] seatData;
	private Entity driver;

	public MultipartFlightModel(World world)
	{
		super(world);
		setSize(1, 2);

		preventEntitySpawning = false;
		ignoreFrustumCheck = true;
		renderDistanceWeight = 200D;
		noClip = true;

		orientation = previousOrientation = new RotatedAxes();
		angularMomentum = new Vector3f();

		createData();
	}

	protected void setPivots(float verticalCenteringOffset, float verticalGroundingOffset)
	{
		this.verticalCenteringOffset = verticalCenteringOffset;
		this.verticalGroundingOffset = verticalGroundingOffset;
	}

	protected void createData()
	{
		data = new ShipData();
		seatData = createSeats();
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		setDead();
		return true;
	}

	private SeatData[] createSeats()
	{
		return new SeatData[] {
				new SeatData("Basic Seat", SeatRole.Driver, new Vector3f(0, 0, 2))
		};
	}

	@Override
	public void onUpdate()
	{
		partWatchdog();

		previousOrientation = orientation.clone();

		if (StarWarsGalaxy.proxy.isClientControlled(this))
			StarWarsGalaxy.proxy.handleVehicleMovement();

		if (getDriver() != null)
		{
			Vector3f forward = orientation.findLocalVectorGlobally(new Vector3f(0, 0, -1));

			motionX = forward.x * throttle;
			motionY = forward.y * throttle;
			motionZ = forward.z * throttle;

			moveEntity(motionX, motionY, motionZ);
		}

		orientation.rotateLocalPitch(angularMomentum.x);
		orientation.rotateLocalYaw(angularMomentum.y);
		orientation.rotateLocalRoll(angularMomentum.z);

		angularMomentum.x *= 0.7;
		angularMomentum.y *= 0.7;
		angularMomentum.z *= 0.7;

		if (Math.abs(angularMomentum.x) < 0.001f)
			angularMomentum.x = 0;
		if (Math.abs(angularMomentum.y) < 0.001f)
			angularMomentum.y = 0;
		if (Math.abs(angularMomentum.z) < 0.001f)
			angularMomentum.z = 0;

		if (riddenByEntity != null && riddenByEntity.isDead)
			riddenByEntity = null;
	}

	public void acceptInput(ShipInput input)
	{
		switch (input)
		{
			case RollLeft:
				angularMomentum.z -= 4;
				break;
			case RollRight:
				angularMomentum.z += 4;
				break;
			case PitchUp:
				angularMomentum.x += 2;
				break;
			case PitchDown:
				angularMomentum.x -= 2;
				break;
			case YawLeft:
				break;
			case YawRight:
				break;
			case ThrottleUp:
				throttle += data.maxThrottle * data.acceleration;
				throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);
				break;
			case ThrottleDown:
				throttle -= data.maxThrottle * data.acceleration;
				throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);
				break;
			case BlasterFire:
				break;
			case SpecialAesthetic:
				break;
			case SpecialWeapon:
				break;
		}
		StarWarsGalaxy.network.sendToServer(new MessageFlightModelUpdate(this));
	}

	private void partWatchdog()
	{
		if (seats == null && !worldObj.isRemote)
		{
			seats = new EntitySeat[seatData.length];
			for (int i = 0; i < seatData.length; i++)
				seats[i] = new EntitySeat(this, seatData[i].name, seatData[i].role, seatData[i].pos);
		}

		if (seats == null)
			return;

		if (!doesClientKnowSeats && !worldObj.isRemote && knowsAllSeats())
		{
			StarWarsGalaxy.network.sendToDimension(new MessageFlightModelClientUpdate(this), dimension);
			doesClientKnowSeats = true;
		}

		for (int i = 0; i < seats.length; i++)
		{
			EntitySeat seat = seats[i];
			if (seat == null)
			{
				if (!worldObj.isRemote)
					setSeat(searchingSeats[i], i);
				else
					setSeat(clientSearchingSeats[i], i);
				continue;
			}

			seat.parent = this;

			if (!worldObj.isRemote)
			{
				seat.setLocation();

				if (worldObj.getEntityByID(seat.getEntityId()) == null)
					worldObj.spawnEntityInWorld(seat);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	protected boolean shouldSetPosAfterLoading()
	{
		return false;
	}

	private boolean knowsAllSeats()
	{
		if (seats == null)
			return false;

		for (EntitySeat s : seats)
			if (s == null)
				return false;

		return true;
	}

	@Override
	public void setDead()
	{
		super.setDead();

		if (seats != null)
			for (EntitySeat part : seats)
				part.setDead();

		if (worldObj.isRemote)
			camera.setDead();
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		loadSeatsFromUuids(tagCompound.getString("seats"));
	}

	public void loadSeatsFromUuids(String uuids)
	{
		String[] seatsPairs = uuids.split(";");

		seats = new EntitySeat[seatsPairs.length];
		searchingSeats = new UUID[seatsPairs.length];
		for (int i = 0; i < seatsPairs.length; i++)
		{
			String[] pair = seatsPairs[i].split("\\|");
			long lsb = Long.parseLong(pair[0]);
			long msb = Long.parseLong(pair[1]);
			UUID uuid = new UUID(msb, lsb);
			searchingSeats[i] = uuid;
		}
	}

	public void loadSeatsFromIds(String uuids)
	{
		String[] seatsPairs = uuids.split(";");

		seats = new EntitySeat[seatsPairs.length];
		clientSearchingSeats = new int[seatsPairs.length];
		for (int i = 0; i < seatsPairs.length; i++)
			clientSearchingSeats[i] = Integer.parseInt(seatsPairs[i]);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		if (seats == null)
		{
			tagCompound.setString("seats", "");
			return;
		}

		tagCompound.setString("seats", getSeatUuids());
	}

	public String getSeatUuids()
	{
		StringBuilder sb = new StringBuilder();

		for (EntitySeat part : seats)
			sb.append(part.getUniqueID().getLeastSignificantBits()).append("|").append(part.getUniqueID().getMostSignificantBits()).append(";");

		return sb.toString();
	}

	public String getSeatIds()
	{
		StringBuilder sb = new StringBuilder();

		for (EntitySeat part : seats)
			sb.append(part.getEntityId()).append(";");

		return sb.toString();
	}

	public void setSeat(UUID seatId, int seatIdx)
	{
		Entity entity = EntityUtils.getEntityByUuid(worldObj, seatId);
		setSeat(seatId.toString(), seatIdx, entity);
	}

	public void setSeat(int seatId, int seatIdx)
	{
		Entity entity = worldObj.getEntityByID(seatId);
		setSeat(String.valueOf(seatId), seatIdx, entity);
	}

	void setSeat(String seatId, int seatIdx, Entity entity)
	{
		if (entity == null)
		{
			Lumberjack.warn("Parent failed to locate part with ID " + seatId);
			//setDead();
		}
		else
		{
			if (!(entity instanceof EntitySeat))
				return;

			seats[seatIdx] = (EntitySeat)entity;
			seats[seatIdx].setParent(getEntityId());
			seats[seatIdx].name = seatData[seatIdx].name;
			seats[seatIdx].role = seatData[seatIdx].role;
			seats[seatIdx].position = seatData[seatIdx].pos;
			Lumberjack.warn("Parent located part");
		}
	}

	public boolean isControlling(Entity thePlayer)
	{
		if (seats != null)
			for (EntitySeat seat : seats)
				if (seat.role == SeatRole.Driver && seat.riddenByEntity == thePlayer)
					return true;
		return false;
	}

	public Entity getDriver()
	{
		if (seats == null)
			return null;

		for (EntitySeat seat : seats)
			if (seat != null && seat.role == SeatRole.Driver && seat.riddenByEntity != null)
				return seat.riddenByEntity;

		return null;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		/*
			ByteBufUtils.writeUTF8String(data, driveableType);

			NBTTagCompound tag = new NBTTagCompound();
			driveableData.writeToNBT(tag);
			ByteBufUtils.writeTag(data, tag);

			data.writeFloat(axes.getYaw());
			data.writeFloat(axes.getPitch());
			data.writeFloat(axes.getRoll());

			//Write damage
	        for(EnumDriveablePart ep : EnumDriveablePart.values())
	        {
	            DriveablePart part = getDriveableData().parts.get(ep);
	            data.writeShort((short)part.health);
	            data.writeBoolean(part.onFire);
	        }
		 */
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		/*
			try
			{
				driveableType = ByteBufUtils.readUTF8String(data);
				driveableData = new DriveableData(ByteBufUtils.readTag(data));
				initType(getDriveableType(), true);

				axes.setAngles(data.readFloat(), data.readFloat(), data.readFloat());
				prevRotationYaw = axes.getYaw();
				prevRotationPitch = axes.getPitch();
				prevRotationRoll = axes.getRoll();

				//Read damage
	            for(EnumDriveablePart ep : EnumDriveablePart.values())
	            {
	                DriveablePart part = getDriveableData().parts.get(ep);
	                part.health = data.readShort();
	                part.onFire = data.readBoolean();
	            }

			}
			catch(Exception e)
			{
				FlansMod.log("Failed to retreive plane type from server.");
				super.setDead();
				e.printStackTrace();
			}
		 */

		camera = new EntityCinematicCamera(this);
		worldObj.spawnEntityInWorld(camera);
	}
}
