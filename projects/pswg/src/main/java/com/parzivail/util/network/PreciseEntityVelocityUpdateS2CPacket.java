package com.parzivail.util.network;

import com.parzivail.pswg.container.SwgPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class PreciseEntityVelocityUpdateS2CPacket extends EntityVelocityUpdateS2CPacket implements CustomPayload
{
	public static final PacketCodec<PacketByteBuf, PreciseEntityVelocityUpdateS2CPacket> CODEC = PacketCodec.of(
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
		this.position = buf.readVec3d();
		this.velocity = buf.readVec3d();
	}

	@Override
	public Id<PreciseEntityVelocityUpdateS2CPacket> getId()
	{
		return SwgPackets.S2C.PreciseEntityVelocityUpdate;
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		super.write(buf);
		buf.writeVec3d(position);
		buf.writeVec3d(velocity);
	}

	public Vec3d getVelocity()
	{
		return velocity;
	}

	public Vec3d getPosition()
	{
		return position;
	}

	public static void handle(PreciseEntityVelocityUpdateS2CPacket packet, ClientPlayNetworking.Context context)
	{
		context.client().getNetworkHandler().onEntityVelocityUpdate(packet);
	}
}

