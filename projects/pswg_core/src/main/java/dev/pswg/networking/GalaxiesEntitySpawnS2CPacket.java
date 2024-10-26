package dev.pswg.networking;

import dev.pswg.Galaxies;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;

/**
 * A spawn packet that serializes full-precision values for entity velocity and angles
 */
public class GalaxiesEntitySpawnS2CPacket extends EntitySpawnS2CPacket implements CustomPayload
{
	public static final CustomPayload.Id<GalaxiesEntitySpawnS2CPacket> ID = new Id<>(Galaxies.id("entity_spawn"));
	public static final PacketCodec<RegistryByteBuf, GalaxiesEntitySpawnS2CPacket> CODEC = PacketCodec.of(GalaxiesEntitySpawnS2CPacket::write, GalaxiesEntitySpawnS2CPacket::new);

	private final Vec3d velocity;
	private final float yaw;
	private final float pitch;

	public GalaxiesEntitySpawnS2CPacket(Entity entity, EntityTrackerEntry entityTrackerEntry)
	{
		super(entity, entityTrackerEntry);

		this.velocity = entity.getVelocity();
		this.yaw = entity.getYaw();
		this.pitch = entity.getPitch();
	}

	public GalaxiesEntitySpawnS2CPacket(RegistryByteBuf buf)
	{
		super(buf);

		this.velocity = buf.readVec3d();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();
	}

	@Override
	public void write(RegistryByteBuf buf)
	{
		super.write(buf);

		buf.writeVec3d(this.velocity);
		buf.writeFloat(this.yaw);
		buf.writeFloat(this.pitch);
	}

	/**
	 * @return The full-precision velocity represented by this packet
	 */
	public Vec3d getVelocity()
	{
		return velocity;
	}

	@Override
	public float getYaw()
	{
		return yaw;
	}

	@Override
	public float getPitch()
	{
		return pitch;
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
