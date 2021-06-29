package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.pswg.util.QuatUtil;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class T65BXwing extends ShipEntity
{
	private static final TrackedData<Byte> WING_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int WING_DIRECTION_MASK = 0b10000000;
	private static final int WING_TIMER_MASK = 0b01111111;

	private static final TrackedData<Byte> COCKPIT_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int COCKPIT_DIRECTION_MASK = 0b10000000;
	private static final int COCKPIT_TIMER_MASK = 0b01111111;

	private static final TrackedData<Byte> CANNON_BITS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000011;

	private static final RigT65B.Socket[] CANNON_ORDER = { RigT65B.Socket.CannonTopLeft, RigT65B.Socket.CannonBottomLeft, RigT65B.Socket.CannonTopRight, RigT65B.Socket.CannonBottomRight };

	@Environment(EnvType.CLIENT)
	public short clientWingBits;

	@Environment(EnvType.CLIENT)
	public short clientCockpitBits;

	public T65BXwing(EntityType<?> type, World world)
	{
		super(type, world);
	}

	public static T65BXwing create(World world)
	{
		T65BXwing ship = new T65BXwing(SwgEntities.Ship.T65bXwing, world);
		//		ship.setSettings(settings);
		return ship;
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(WING_BITS, (byte)0);
		getDataTracker().startTracking(COCKPIT_BITS, (byte)0);
		getDataTracker().startTracking(CANNON_BITS, (byte)0);
	}

	@Override
	public void acceptFireInput()
	{
		Entity passenger = getPrimaryPassenger();
		if (!(passenger instanceof PlayerEntity))
			return;

		PlayerEntity player = (PlayerEntity)passenger;

		Vec3d pDir = QuatUtil.rotate(MathUtil.NEGZ.multiply(4f), getRotation());
		MatrixStack stack = new MatrixStack();

		byte cannonState = getCannonState();

		Vec3d p = CANNON_ORDER[cannonState].getWorldPosition(stack, this);

		BlasterUtil.fireBolt(world, player, pDir, 100, 50, blasterBoltEntity -> {
			blasterBoltEntity.setVelocity(pDir);
			blasterBoltEntity.setPos(this.getX() + p.x, this.getY() + p.y + 0.25f, this.getZ() + p.z);
		});

		world.playSound(null, player.getBlockPos(), SwgSounds.Ship.XWINGT65B_FIRE, SoundCategory.PLAYERS, 1, 1 + (float)world.random.nextGaussian() / 10);

		cannonState++;
		setCannonState(cannonState);
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions)
	{
		return 0f;
//		return super.getEyeHeight(pose, dimensions);
	}

	@Override
	public void tick()
	{
		super.tick();

		EnumSet<ShipControls> controls = getControls();
		Entity pilot = getPrimaryPassenger();

		// tick wings
		byte wingTimer = getWingTimer();

		if (wingTimer != 0)
		{
			boolean movingUp = areWingsOpening();

			wingTimer--;

			setWings(movingUp, wingTimer);
		}

		if (pilot instanceof PlayerEntity)
		{
			if (controls.contains(ShipControls.SPECIAL1) && wingTimer == 0)
			{
				boolean opening = areWingsOpening();

				setWings(!opening, (byte)20);
			}
		}

		// tick cockpit
		byte cockpitTimer = getCockpitTimer();

		if (cockpitTimer != 0)
		{
			boolean movingUp = isCockpitOpening();

			cockpitTimer--;

			setCockpit(movingUp, cockpitTimer);
		}

		if (pilot instanceof PlayerEntity)
		{
			if (controls.contains(ShipControls.SPECIAL2) && cockpitTimer == 0)
			{
				boolean opening = isCockpitOpening();

				setCockpit(!opening, (byte)20);
			}
		}

		clientWingBits = dataTracker.get(WING_BITS);
		clientCockpitBits = dataTracker.get(COCKPIT_BITS);
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);

		setWings(tag.getBoolean("wingDirection"), tag.getByte("wingTimer"));
		setCockpit(tag.getBoolean("cockpitDirection"), tag.getByte("cockpitTimer"));
		setCannonState(tag.getByte("cannonState"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);

		tag.putBoolean("wingDirection", areWingsOpening());
		tag.putByte("wingTimer", getWingTimer());

		tag.putBoolean("cockpitDirection", isCockpitOpening());
		tag.putByte("cockpitTimer", getCockpitTimer());

		tag.putByte("cannonState", getCannonState());
	}

	public byte getCannonState()
	{
		return (byte)(getDataTracker().get(CANNON_BITS) & CANNON_STATE_MASK);
	}

	public void setCannonState(byte cannonState)
	{
		byte cannons = getDataTracker().get(CANNON_BITS);

		cannons &= ~CANNON_STATE_MASK;
		cannons |= cannonState & CANNON_STATE_MASK;

		getDataTracker().set(CANNON_BITS, cannons);
	}

	public byte getWingTimer()
	{
		return (byte)(getDataTracker().get(WING_BITS) & WING_TIMER_MASK);
	}

	public boolean areWingsOpening()
	{
		return (getDataTracker().get(WING_BITS) & WING_DIRECTION_MASK) != 0;
	}

	@Environment(EnvType.CLIENT)
	public byte getWingTimerClient()
	{
		return (byte)(clientWingBits & WING_TIMER_MASK);
	}

	@Environment(EnvType.CLIENT)
	public boolean areWingsOpeningClient()
	{
		return (clientWingBits & WING_DIRECTION_MASK) != 0;
	}

	public void setWings(boolean direction, byte timer)
	{
		if (direction)
			timer |= WING_DIRECTION_MASK;
		getDataTracker().set(WING_BITS, timer);
	}

	public byte getCockpitTimer()
	{
		return (byte)(getDataTracker().get(COCKPIT_BITS) & COCKPIT_TIMER_MASK);
	}

	public boolean isCockpitOpening()
	{
		return (getDataTracker().get(COCKPIT_BITS) & COCKPIT_DIRECTION_MASK) != 0;
	}

	@Environment(EnvType.CLIENT)
	public byte getCockpitTimerClient()
	{
		return (byte)(clientCockpitBits & COCKPIT_TIMER_MASK);
	}

	@Environment(EnvType.CLIENT)
	public boolean isCockpitOpeningClient()
	{
		return (clientCockpitBits & COCKPIT_DIRECTION_MASK) != 0;
	}

	public void setCockpit(boolean direction, byte timer)
	{
		if (direction)
			timer |= COCKPIT_DIRECTION_MASK;
		getDataTracker().set(COCKPIT_BITS, timer);
	}
}
