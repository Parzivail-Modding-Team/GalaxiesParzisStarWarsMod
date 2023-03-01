package com.parzivail.util.network;

import com.parzivail.util.data.PacketByteBufHelper;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PreciseEntitySpawnS2CPacket extends EntitySpawnS2CPacket
{
	private final Vec3d velocity;

	private PreciseEntitySpawnS2CPacket(Entity entity, int entityData)
	{
		super(entity, entityData);
		this.velocity = entity.getVelocity();
	}

	public PreciseEntitySpawnS2CPacket(PacketByteBuf buf)
	{
		super(buf);
		this.velocity = PacketByteBufHelper.readVec3d(buf);
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		super.write(buf);
		PacketByteBufHelper.writeVec3d(buf, velocity);
	}

	public Vec3d getVelocity()
	{
		return velocity;
	}

	@SuppressWarnings("unchecked")
	public static Packet<ClientPlayPacketListener> createPacket(Identifier id, Entity entity, int entityData)
	{
		var passedData = new PacketByteBuf(Unpooled.buffer());
		new PreciseEntitySpawnS2CPacket(entity, entityData).write(passedData);
		return (Packet<ClientPlayPacketListener>)ServerPlayNetworking.createS2CPacket(id, passedData);
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
	{
		var packet = new PreciseEntitySpawnS2CPacket(buf);
		client.execute(() -> handler.onEntitySpawn(packet));
	}
}

