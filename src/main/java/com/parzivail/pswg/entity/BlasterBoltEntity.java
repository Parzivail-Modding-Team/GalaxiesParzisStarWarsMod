package com.parzivail.pswg.entity;

import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.container.SwgParticles;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlasterBoltEntity extends ThrownEntity
{
	private static final TrackedData<Integer> LIFE = DataTracker.registerData(BlasterBoltEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> HUE = DataTracker.registerData(BlasterBoltEntity.class, TrackedDataHandlerRegistry.FLOAT);

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, World world)
	{
		super(type, world);
	}

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}

	public static void handleBoltHit(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		var incident = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		var normal = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

		var blockPos = new BlockPos(pos.subtract(normal.multiply(0.1f)));

		assert client.world != null;

		client.world.addParticle(SwgParticles.SCORCH, pos.x + normal.x * 0.01f, pos.y + normal.y * 0.01f, pos.z + normal.z * 0.01f, normal.x, normal.y, normal.z);

		var reflection = normal.multiply(2 * normal.dotProduct(incident)).subtract(incident);

		reflection = reflection.multiply(-1);

		for (var i = 0; i < 16; i++)
		{
			var vx = client.world.random.nextGaussian() * 0.03;
			var vy = client.world.random.nextGaussian() * 0.03;
			var vz = client.world.random.nextGaussian() * 0.03;

			var sparkVelocity = reflection.multiply(0.3f * (client.world.random.nextDouble() * 0.5 + 0.5));
			client.world.addParticle(SwgParticles.SPARK, pos.x, pos.y, pos.z, sparkVelocity.x + vx, sparkVelocity.y + vy, sparkVelocity.z + vz);

			if (i % 3 == 0)
			{
				var debrisVelocity = reflection.multiply(0.15f * (client.world.random.nextDouble() * 0.5 + 0.5));
				client.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, client.world.getBlockState(blockPos)), pos.x, pos.y, pos.z, debrisVelocity.x + vx, debrisVelocity.y + vy, debrisVelocity.z + vz);
			}

			if (i % 2 == 0)
			{
				var smokeVelocity = reflection.multiply(0.08f * (client.world.random.nextDouble() * 0.5 + 0.5));
				client.world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, smokeVelocity.x + vx, smokeVelocity.y + vy, smokeVelocity.z + vz);
			}
		}
	}

	public void setRange(float range)
	{
		var ticksToLive = (int)(range / getVelocity().length());
		setLife(ticksToLive);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		SoundHelper.playBlasterBoltHissSound(this);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		tag.putInt("life", getLife());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		setLife(tag.getInt("life"));
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(LIFE, 0);
		dataTracker.startTracking(HUE, 0.0f);
	}

	private int getLife()
	{
		return dataTracker.get(LIFE);
	}

	private void setLife(int life)
	{
		dataTracker.set(LIFE, life);
	}

	public float getHue()
	{
		return dataTracker.get(HUE);
	}

	public void setHue(float hue)
	{
		dataTracker.set(HUE, hue);
	}

	@Override
	public void tick()
	{
		final var life = getLife() - 1;
		setLife(life);

		if (life <= 0)
		{
			this.discard();
			return;
		}

		var forward = getVelocity().normalize();
		setYaw(-(float)Math.atan2(forward.x, forward.z));
		setPitch((float)Math.asin(forward.y));

		super.tick();
	}

	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);

		if (!this.world.isClient)
		{
			if (hitResult.getType() == HitResult.Type.BLOCK)
			{
				var blockHit = (BlockHitResult)hitResult;

				var incident = this.getVelocity().normalize();
				var normal = new Vec3d(blockHit.getSide().getUnitVector());

				var pos = hitResult.getPos();

				var passedData = WorldEvent.createBuffer(WorldEvent.BLASTER_BOLT_HIT);
				passedData.writeDouble(pos.x);
				passedData.writeDouble(pos.y);
				passedData.writeDouble(pos.z);
				passedData.writeDouble(incident.x);
				passedData.writeDouble(incident.y);
				passedData.writeDouble(incident.z);
				passedData.writeDouble(normal.x);
				passedData.writeDouble(normal.y);
				passedData.writeDouble(normal.z);

				for (var trackingPlayer : PlayerLookup.tracking((ServerWorld)world, blockHit.getBlockPos()))
					ServerPlayNetworking.send(trackingPlayer, SwgPackets.S2C.PacketWorldEvent, passedData);
			}
		}

		this.discard();
	}
}
