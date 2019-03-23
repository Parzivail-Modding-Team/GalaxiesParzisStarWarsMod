package com.parzivail.swg.entity.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityCinematicCamera;
import com.parzivail.swg.network.MessageShipState;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.dimension.Rift;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.item.IGuiOverlay;
import com.parzivail.util.math.RenderedValue;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.SlidingWindow;
import com.parzivail.util.math.lwjgl.Vector3f;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class EntityShip extends Entity implements IEntityAdditionalSpawnData, IGuiOverlay
{
	public RotatedAxes orientation;
	public RotatedAxes previousOrientation;
	public Vector3f angularMomentum;
	public float throttle;
	public EntitySeat[] seats;
	public boolean isInitialized;
	public int ticksStartHyperdrive = -1;
	public UUID shipId;
	public boolean wingsOpen;
	public RenderedValue wingsTimer = new RenderedValue();

	@SideOnly(Side.CLIENT)
	public EntityCinematicCamera camera;
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingPitch = new SlidingWindow(6);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingYaw = new SlidingWindow(5);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingThrottle = new SlidingWindow(8);

	public EntityShip(World worldIn)
	{
		super(worldIn);
		setSize(1, 1);
		orientation = previousOrientation = new RotatedAxes(0, 0, 0);
		angularMomentum = new Vector3f(0, 0, 0);
		throttle = 0;
		shipId = UUID.randomUUID();
	}

	@Override
	protected void entityInit()
	{
		if (!worldObj.isRemote)
			createChildren();
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements)
	{
		if (seats[0] != null && seats[0].riddenByEntity == Client.getPlayer())
		{
			Vector3f here = new Vector3f((float)posX, (float)posY, (float)posZ);
			Vector3f there = new Vector3f((float)x, (float)y, (float)z);
			if (Vector3f.sub(here, there, null).lengthSquared() > 1)
			{
				// Dear server. I respect you telling me where to go, but I decline and will proceed to tell you where I am instead.
				StarWarsGalaxy.network.sendToServer(new MessageShipState(this));
			}
		}
		else
		{
			setPosition(x, y, z);
			setRotation(yaw, pitch);
		}
	}

	public abstract ShipData getData();

	public abstract Vector3f getSeatPosition(int seatIdx);

	@Override
	public void setDead()
	{
		super.setDead();

		if (worldObj.isRemote)
			killCamera();

		for (EntitySeat seat : seats)
			if (seat != null)
				seat.setDead();
	}

	private void killCamera()
	{
		if (camera != null)
			camera.setDead();
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		super.setPosition(x, y, z);
		if (seats != null)
			for (EntitySeat seat : seats)
				if (seat != null)
					seat.trackParent();
	}

	@Override
	public void onUpdate()
	{
		if (ridingEntity != null && ridingEntity.isDead)
			ridingEntity = null;

		lastTickPosX = prevPosX = posX;
		lastTickPosY = prevPosY = posY;
		lastTickPosZ = prevPosZ = posZ;
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;
		previousOrientation = orientation.clone();
		wingsTimer.tick();

		ShipData data = getData();

		if (posY < -64.0D)
			kill();

		if (!isInitialized)
		{
			spawnChildren();
			isInitialized = true;
		}

		StarWarsGalaxy.proxy.handleVehicleKeybinds();

		if (posY > 255 && dimension != StarWarsGalaxy.config.getDimIdHyperspace())
		{
			if (ticksStartHyperdrive == -1)
				ticksStartHyperdrive = ticksExisted;
			else if (ticksExisted - ticksStartHyperdrive > 100)
			{
				Lumberjack.log("Hyperdrive");
				Rift.travelEntity(this, StarWarsGalaxy.config.getDimIdHyperspace());
				ticksStartHyperdrive = -1;
			}
		}
		else
			ticksStartHyperdrive = -1;

		Entity driver = seats == null ? null : (seats[0] == null ? null : seats[0].riddenByEntity);
		if (driver instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)driver;

			if (player == Client.getPlayer())
			{
				float oldThrottle = throttle;
				throttle += player.moveForward * data.acceleration;
				throttle = MathHelper.clamp_float(throttle, 0, data.maxThrottle);

				if (oldThrottle != throttle)
					StarWarsGalaxy.network.sendToServer(new MessageShipState(this));
			}

			consumePlayerOrientation(data, player);

			Vector3f forward = orientation.findLocalVectorGlobally(new Vector3f(0, 0, 1));

			motionX = forward.x * throttle;
			motionY = forward.y * throttle;
			motionZ = forward.z * throttle;

			rotationPitch = orientation.getPitch();
			rotationYaw = orientation.getYaw();

			slidingThrottle.slide(throttle);
		}
		else
		{
			motionX = 0;
			motionY = 0;
			motionZ = 0;
		}

		float dYaw = MathHelper.wrapAngleTo180_float(orientation.getYaw() - previousOrientation.getYaw());
		slidingYaw.slide(dYaw);
		float dPitch = MathHelper.wrapAngleTo180_float(orientation.getPitch() - previousOrientation.getPitch());
		slidingPitch.slide(dPitch);

		float timer = wingsTimer.get(0);
		if (wingsOpen && timer < 20)
			wingsTimer.add(1);
		else if (!wingsOpen && timer > 0)
			wingsTimer.add(-1);

		if (data.isAirVehicle)
		{
			// max distance
			if (data.hasMaxDistance)
			{
				List<AxisAlignedBB> aabb = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMax, 0).addCoord(0, data.distanceMax * 2, 0));
				List<AxisAlignedBB> aabbBig = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMax + 1, 0).addCoord(0, (data.distanceMax + 1) * 2, 0));
				if (aabb.isEmpty())
				{
					if (aabbBig.isEmpty())
						motionY = motionY >= 0 ? -data.repulsorliftForce : motionY;
					else
						motionY = motionY >= 0 ? 0 : motionY;
				}
			}

			// min distance
			if (data.hasMinDistance)
			{
				List<AxisAlignedBB> aabb = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMin, 0).addCoord(0, data.distanceMin * 2, 0));
				List<AxisAlignedBB> aabbSmall = EntityUtils.getBlockAABBs(worldObj, boundingBox.expand(0, data.distanceMin - 1, 0).addCoord(0, (data.distanceMin - 1) * 2, 0));
				if (!aabb.isEmpty())
				{
					if (aabbSmall.isEmpty())
						motionY = motionY <= 0 ? 0 : motionY;
					else
						motionY = motionY <= 0 ? data.repulsorliftForce : motionY;
				}
			}
		}
		else
		{
			// gravity
			if (onGround)
				motionY = 0;
			else
				motionY -= 0.75 * data.repulsorliftForce;
		}

		moveEntity(motionX, motionY, motionZ);
	}

	protected void consumePlayerOrientation(ShipData data, EntityPlayer player)
	{
		// Ok, player.rotationPitch * 0.999999f is a fun one. So, when pitch = -90 or 90, the math makes a lot of
		// assumptions the matrices should be in (since any yaw with pitch = [-90, 90] is the same location so to
		// combat that we don't let it hit 90, just 89.99991 which is close enough and won't bother anyone.
		float playerPitch = -player.rotationPitch * 0.999999f;

		float vehiclePitch = 0;

		if (data.isAirVehicle)
			vehiclePitch = playerPitch;

		orientation.setAngles(-player.rotationYaw, vehiclePitch, 0);
	}

	private void spawnChildren()
	{
		if (worldObj.isRemote)
		{
			spawnCamera();
			return;
		}

		HashMap<Integer, EntityPlayer> players = getPlayersToRemount();

		for (int i = 0; i < seats.length; i++)
		{
			// TODO: seat roles
			seats[i] = new EntitySeat(worldObj, this, i);
			worldObj.spawnEntityInWorld(seats[i]);
			if (players.containsKey(i) && players.get(i) != null)
			{
				players.get(i).mountEntity(seats[i]);
			}
		}
	}

	private HashMap<Integer, EntityPlayer> getPlayersToRemount()
	{
		HashMap<Integer, EntityPlayer> players = new HashMap<>();
		for (Object o : worldObj.playerEntities)
		{
			EntityPlayer player = (EntityPlayer)o;
			PswgExtProp ieep = PswgExtProp.get(player);
			if (ieep.isChangingDimensions() && shipId.equals(ieep.getShipRiding()))
				players.put(ieep.getShipRidingSeatIdx(), player);
		}
		return players;
	}

	public void updateRiderPosition()
	{
		if (riddenByEntity == null)
			return;

		riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
	}

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		return false;
	}

	public boolean canBeCollidedWith()
	{
		return false;
	}

	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return 0.6;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		readTransientState(tagCompound);
		shipId = UUID.fromString(tagCompound.getString("shipId"));

		createChildren();
	}

	private void createChildren()
	{
		ShipData data = getData();
		seats = new EntitySeat[data.numSeats];
	}

	private void spawnCamera()
	{
		camera = new EntityCinematicCamera(this);
		worldObj.spawnEntityInWorld(camera);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setString("shipId", shipId.toString());
		writeTransientState(tagCompound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		buffer.writeFloat(orientation.getYaw());
		buffer.writeFloat(orientation.getPitch());
		buffer.writeFloat(orientation.getRoll());
		buffer.writeFloat(throttle);
		long lower = shipId.getLeastSignificantBits();
		long upper = shipId.getMostSignificantBits();
		buffer.writeLong(lower);
		buffer.writeLong(upper);
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		orientation = new RotatedAxes(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		throttle = buffer.readFloat();
		createChildren();
		long lower = buffer.readLong();
		long upper = buffer.readLong();
		shipId = new UUID(upper, lower);
	}

	public boolean isBootingHyperdrive()
	{
		return posY > 255;
	}

	public void breakdown()
	{
		isInitialized = false;
		for (EntitySeat seat : seats)
			if (seat != null)
				seat.setDead();
	}

	public void consumeInput(ShipInput input)
	{
		switch (input)
		{
			case WingActuate:
				wingsOpen = !wingsOpen;
				break;
		}
	}

	public void writeTransientState(NBTTagCompound state)
	{
		state.setFloat("throttle", throttle);
		state.setFloat("yaw", orientation.getYaw());
		state.setFloat("pitch", orientation.getPitch());
		state.setFloat("roll", orientation.getRoll());
		state.setBoolean("wingsOpen", wingsOpen);
		state.setFloat("wingsTimer", wingsTimer.get(0));
	}

	public void readTransientState(NBTTagCompound state)
	{
		throttle = state.getFloat("throttle");
		orientation = new RotatedAxes(state.getFloat("yaw"), state.getFloat("pitch"), state.getFloat("roll"));
		wingsOpen = state.getBoolean("wingsOpen");
		wingsTimer.set(state.getFloat("wingsTimer"));
	}
}
