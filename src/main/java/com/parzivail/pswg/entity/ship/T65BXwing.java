package com.parzivail.pswg.entity.ship;

import com.parzivail.pswg.client.input.ShipControls;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

import java.util.EnumSet;

public class T65BXwing extends ShipEntity
{
	private static final TrackedData<Short> WINGS = DataTracker.registerData(ShipEntity.class, TrackedDataHandlers.SHORT);
	private static final int WING_DIRECTION_MASK = 0b10000000;
	private static final int WING_TIMER_MASK = 0b01111111;
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
		getDataTracker().startTracking(WINGS, (short)0);
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
				return new Quaternion(0, 0, -angle, true);
			case "WingBottomLeft":
			case "WingTopRight":
				return new Quaternion(0, 0, angle, true);
		}

		return new Quaternion(Quaternion.IDENTITY);
	}

	@Override
	public void tick()
	{
		super.tick();

		//		Vec3d pDir = QuatUtil.rotate(MathUtil.NEGZ.multiply(3f), getRotation());
		//
		//		Vec3d p = RigT65B.Socket.CannonTopLeft.getWorldPosition(this);
		//		world.addParticle(ParticleTypes.SMOKE, this.getX() + p.x, this.getY() + p.y + 0.5f, this.getZ() + p.z, pDir.x, pDir.y, pDir.z);
		//
		//		p = RigT65B.Socket.CannonBottomLeft.getWorldPosition(this);
		//		world.addParticle(ParticleTypes.SMOKE, this.getX() + p.x, this.getY() + p.y + 0.5f, this.getZ() + p.z, pDir.x, pDir.y, pDir.z);
		//
		//		p = RigT65B.Socket.CannonTopRight.getWorldPosition(this);
		//		world.addParticle(ParticleTypes.SMOKE, this.getX() + p.x, this.getY() + p.y + 0.5f, this.getZ() + p.z, pDir.x, pDir.y, pDir.z);
		//
		//		p = RigT65B.Socket.CannonBottomRight.getWorldPosition(this);
		//		world.addParticle(ParticleTypes.SMOKE, this.getX() + p.x, this.getY() + p.y + 0.5f, this.getZ() + p.z, pDir.x, pDir.y, pDir.z);

		short wingTimer = getWingTimer();

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

				setWings(!movingUp, (short)20);
			}
		}

		clientWingState = dataTracker.get(WINGS);
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this);
	}

	public short getWingTimer()
	{
		return (short)(getDataTracker().get(WINGS) & WING_TIMER_MASK);
	}

	public boolean getWingDirection()
	{
		return (getDataTracker().get(WINGS) & WING_DIRECTION_MASK) != 0;
	}

	@Environment(EnvType.CLIENT)
	public short getWingTimerClient()
	{
		return (short)(clientWingState & WING_TIMER_MASK);
	}

	@Environment(EnvType.CLIENT)
	public boolean getWingDirectionClient()
	{
		return (clientWingState & WING_DIRECTION_MASK) != 0;
	}

	public void setWings(boolean direction, short timer)
	{
		if (direction)
			timer |= WING_DIRECTION_MASK;
		getDataTracker().set(WINGS, timer);
	}
}
