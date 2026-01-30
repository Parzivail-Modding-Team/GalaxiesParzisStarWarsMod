package dev.pswg.networking;

import dev.pswg.Galaxies;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record PreciseVelocityParticleS2CPayload(ParticleEffect particleEffect, Vec3d posVector, Vec3d velocityVector) implements CustomPayload
{
	public static final Identifier PRECISE_VELOCITY_PARTICLE_PAYLOAD_ID = Galaxies.id("precise_velocity_particle");
	public static final CustomPayload.Id<PreciseVelocityParticleS2CPayload> ID = new CustomPayload.Id<>(PRECISE_VELOCITY_PARTICLE_PAYLOAD_ID);
	public static final PacketCodec<RegistryByteBuf, PreciseVelocityParticleS2CPayload> CODEC = PacketCodec.of(PreciseVelocityParticleS2CPayload::toPacket, PreciseVelocityParticleS2CPayload::fromPacket);

	private void toPacket(RegistryByteBuf buf)
	{
		ParticleTypes.PACKET_CODEC.encode(buf, particleEffect);
		Vec3d.PACKET_CODEC.encode(buf, posVector);
		Vec3d.PACKET_CODEC.encode(buf, velocityVector);
	}

	private static PreciseVelocityParticleS2CPayload fromPacket(RegistryByteBuf buf)
	{
		ParticleEffect particleEffect = ParticleTypes.PACKET_CODEC.decode(buf);
		Vec3d pos = Vec3d.PACKET_CODEC.decode(buf);
		Vec3d velocity = Vec3d.PACKET_CODEC.decode(buf);

		return new PreciseVelocityParticleS2CPayload(particleEffect, pos, velocity);
	}

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}
