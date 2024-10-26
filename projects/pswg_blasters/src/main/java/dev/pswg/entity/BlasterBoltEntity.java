package dev.pswg.entity;

import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import dev.pswg.networking.GalaxiesNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BlasterBoltEntity extends Entity
{
	public BlasterBoltEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	public void tick()
	{
		super.tick();

		move(MovementType.SELF, getVelocity());

		if (this.getWorld() instanceof ServerWorld serverWorld && this.age > 20)
			kill(serverWorld);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);

		float yaw = packet.getYaw();
		float pitch = packet.getPitch();
		setAngles(yaw, pitch);

		if (packet instanceof GalaxiesEntitySpawnS2CPacket precisePacket)
		{
			setVelocity(precisePacket.getVelocity());
			readCustomDataFromNbt(precisePacket.getCustomData(this, NbtCompound.CODEC).getOrThrow());
		}
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry)
	{
		var nbt = new NbtCompound();
		writeCustomDataToNbt(nbt);

		return GalaxiesNetworking.createPlayS2CPacket(new GalaxiesEntitySpawnS2CPacket(
				this,
				entityTrackerEntry,
				NbtCompound.CODEC,
				nbt
		));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt)
	{
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
	}
}
