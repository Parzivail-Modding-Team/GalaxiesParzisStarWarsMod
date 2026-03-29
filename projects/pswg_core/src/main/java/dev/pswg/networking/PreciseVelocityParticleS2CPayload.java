package dev.pswg.networking;

import dev.pswg.Galaxies;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public record PreciseVelocityParticleS2CPayload(ParticleOptions particleEffect, Vec3 posVector, Vec3 velocityVector) implements CustomPacketPayload
{
	public static final Identifier PRECISE_VELOCITY_PARTICLE_PAYLOAD_ID = Galaxies.id("precise_velocity_particle");
	public static final CustomPacketPayload.Type<PreciseVelocityParticleS2CPayload> ID = new CustomPacketPayload.Type<>(PRECISE_VELOCITY_PARTICLE_PAYLOAD_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, PreciseVelocityParticleS2CPayload> CODEC = StreamCodec.ofMember(PreciseVelocityParticleS2CPayload::toPacket, PreciseVelocityParticleS2CPayload::fromPacket);

	private void toPacket(RegistryFriendlyByteBuf buf)
	{
		ParticleTypes.STREAM_CODEC.encode(buf, particleEffect);
		Vec3.STREAM_CODEC.encode(buf, posVector);
		Vec3.STREAM_CODEC.encode(buf, velocityVector);
	}

	private static PreciseVelocityParticleS2CPayload fromPacket(RegistryFriendlyByteBuf buf)
	{
		ParticleOptions particleEffect = ParticleTypes.STREAM_CODEC.decode(buf);
		Vec3 pos = Vec3.STREAM_CODEC.decode(buf);
		Vec3 velocity = Vec3.STREAM_CODEC.decode(buf);

		return new PreciseVelocityParticleS2CPayload(particleEffect, pos, velocity);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return ID;
	}
}
