package com.parzivail.util.network;

import com.parzivail.util.data.PacketByteBufHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class PreciseEntityVelocityUpdateS2CPacket extends EntityVelocityUpdateS2CPacket
{
	public static final PacketCodec<PacketByteBuf, PreciseEntityVelocityUpdateS2CPacket> CODEC = Packet.createCodec(
			PreciseEntityVelocityUpdateS2CPacket::write, PreciseEntityVelocityUpdateS2CPacket::new
	);

	private final Vec3d position;
	private final Vec3d velocity;

	public PreciseEntityVelocityUpdateS2CPacket(Entity entity)
	{
		this(entity.getId(), entity.getPos(), entity.getVelocity());
	}

	public PreciseEntityVelocityUpdateS2CPacket(int id, Vec3d position, Vec3d velocity)
	{
		super(id, velocity);
		this.position = position;
		this.velocity = velocity;
	}

	public PreciseEntityVelocityUpdateS2CPacket(PacketByteBuf buf)
	{
		super(buf);
		this.position = PacketByteBufHelper.readVec3d(buf);
		this.velocity = PacketByteBufHelper.readVec3d(buf);
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		super.write(buf);
		PacketByteBufHelper.writeVec3d(buf, position);
		PacketByteBufHelper.writeVec3d(buf, velocity);
	}

	public Vec3d getVelocity()
	{
		return velocity;
	}

	public Vec3d getPosition()
	{
		return position;
	}

	public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
	{
		var packet = new PreciseEntityVelocityUpdateS2CPacket(buf);
		client.execute(() -> handler.onEntityVelocityUpdate(packet));
	}
}

