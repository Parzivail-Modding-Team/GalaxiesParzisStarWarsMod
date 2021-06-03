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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class T65BXwing extends ShipEntity
{
	private static final TrackedData<Byte> WINGS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int WING_DIRECTION_MASK = 0b10000000;
	private static final int WING_TIMER_MASK = 0b01111111;

	private static final TrackedData<Byte> CANNONS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CANNON_STATE_MASK = 0b00000011;

	private static final RigT65B.Socket[] CANNON_ORDER = { RigT65B.Socket.CannonTopLeft, RigT65B.Socket.CannonBottomLeft, RigT65B.Socket.CannonTopRight, RigT65B.Socket.CannonBottomRight };

	@Environment(EnvType.CLIENT)
	public short clientWingState;

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
		getDataTracker().startTracking(WINGS, (byte)0);
		getDataTracker().startTracking(CANNONS, (byte)0);
	}

	private Quaternion getPartRotation(String part)
	{
		short wingTimer = getWingTimer();

		boolean wingsOpening = getWingDirection();
		float timer = Math.abs(wingTimer);

		float angle;

		if (wingsOpening)
			angle = 13 * (20 - timer) / 20;
		else
			angle = 13 * timer / 20;

		switch (part)
		{
			case "WingTopLeft":
			case "WingBottomRight":
				return QuatUtil.of(0, 0, -angle, true);
			case "WingBottomLeft":
			case "WingTopRight":
				return QuatUtil.of(0, 0, angle, true);
		}

		return new Quaternion(Quaternion.IDENTITY);
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
	public void tick()
	{
		super.tick();

		byte wingTimer = getWingTimer();

		if (wingTimer != 0)
		{
			boolean movingUp = getWingDirection();

			wingTimer--;

			setWings(movingUp, wingTimer);
		}

		Entity pilot = getPrimaryPassenger();
		if (pilot instanceof PlayerEntity)
		{
			EnumSet<ShipControls> controls = getControls();

			if (controls.contains(ShipControls.SPECIAL) && wingTimer == 0)
			{
				boolean movingUp = getWingDirection();

				setWings(!movingUp, (byte)20);
			}
		}

		clientWingState = dataTracker.get(WINGS);
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
		setCannonState(tag.getByte("cannonState"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);

		tag.putBoolean("wingDirection", getWingDirection());
		tag.putByte("wingTimer", getWingTimer());

		tag.putByte("cannonState", getCannonState());
	}

	public byte getCannonState()
	{
		return (byte)(getDataTracker().get(CANNONS) & CANNON_STATE_MASK);
	}

	public void setCannonState(byte cannonState)
	{
		byte cannons = getDataTracker().get(CANNONS);

		cannons &= ~CANNON_STATE_MASK;
		cannons |= cannonState & CANNON_STATE_MASK;

		getDataTracker().set(CANNONS, cannons);
	}

	public byte getWingTimer()
	{
		return (byte)(getDataTracker().get(WINGS) & WING_TIMER_MASK);
	}

	public boolean getWingDirection()
	{
		return (getDataTracker().get(WINGS) & WING_DIRECTION_MASK) != 0;
	}

	@Environment(EnvType.CLIENT)
	public byte getWingTimerClient()
	{
		return (byte)(clientWingState & WING_TIMER_MASK);
	}

	@Environment(EnvType.CLIENT)
	public boolean getWingDirectionClient()
	{
		return (clientWingState & WING_DIRECTION_MASK) != 0;
	}

	public void setWings(boolean direction, byte timer)
	{
		if (direction)
			timer |= WING_DIRECTION_MASK;
		getDataTracker().set(WINGS, timer);
	}
}
