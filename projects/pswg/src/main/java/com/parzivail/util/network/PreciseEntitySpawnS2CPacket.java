package com.parzivail.util.network;

import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.entity.IPrecisionSpawnEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;

public class PreciseEntitySpawnS2CPacket extends EntitySpawnS2CPacket implements CustomPayload
{
	public static final PacketCodec<RegistryByteBuf, PreciseEntitySpawnS2CPacket> CODEC = PacketCodec.of(
			PreciseEntitySpawnS2CPacket::write, PreciseEntitySpawnS2CPacket::new
	);

	private final Vec3d velocity;
	private final NbtCompound data;

	public PreciseEntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry, int entityData)
	{
		super(entity, entityTrackerEntry, entityData);
		this.velocity = entity.getVelocity();
		this.data = new NbtCompound();
		if (entity instanceof IPrecisionSpawnEntity pse)
			pse.writeSpawnData(data);
	}

	public PreciseEntitySpawnS2CPacket(RegistryByteBuf buf)
	{
		super(buf);
		this.velocity = buf.readVec3d();
		this.data = buf.readNbt();
	}

	@Override
	protected void write(RegistryByteBuf buf)
	{
		super.write(buf);
		buf.writeVec3d(velocity);
		buf.writeNbt(data);
	}

	@Override
	public Id<PreciseEntitySpawnS2CPacket> getId()
	{
		return SwgPackets.S2C.PreciseEntitySpawn;
	}

	public Vec3d getVelocity()
	{
		return velocity;
	}

	public NbtCompound getData()
	{
		return data;
	}

	public static void handle(PreciseEntitySpawnS2CPacket packet, ClientPlayNetworking.Context context)
	{
		context.client().getNetworkHandler().onEntitySpawn(packet);
	}
}

