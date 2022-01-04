package com.parzivail.util.network;

import com.parzivail.util.data.ByteBufHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class PreciseEntityVelocityUpdateS2CPacket extends EntityVelocityUpdateS2CPacket
{
	private final Vec3d velocity;

	public PreciseEntityVelocityUpdateS2CPacket(Entity entity)
	{
		this(entity.getId(), entity.getVelocity());
	}

	public PreciseEntityVelocityUpdateS2CPacket(int id, Vec3d velocity)
	{
		super(id, velocity);
		this.velocity = velocity;
	}

	public PreciseEntityVelocityUpdateS2CPacket(PacketByteBuf buf)
	{
		super(buf);
		this.velocity = ByteBufHelper.readVec3d(buf);
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		super.write(buf);
		ByteBufHelper.writeVec3d(buf, velocity);
	}

	public Vec3d getVelocity()
	{
		return velocity;
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
	{
		var packet = new PreciseEntityVelocityUpdateS2CPacket(buf);
		client.execute(() -> handler.onEntityVelocityUpdate(packet));
	}
}

