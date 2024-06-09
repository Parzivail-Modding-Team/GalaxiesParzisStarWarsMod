package com.parzivail.util.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class PParticleType extends ParticleType<PParticleType> implements ParticleEffect
{
	private final MapCodec<PParticleType> codec = MapCodec.unit(this::getType);
	private final PacketCodec<RegistryByteBuf, PParticleType> packetCodec = PacketCodec.unit(this);


	public PParticleType(boolean alwaysShow)
	{
		super(alwaysShow);
	}

	@Override
	public PParticleType getType()
	{
		return this;
	}

	@Override
	public MapCodec<PParticleType> getCodec()
	{
		return this.codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, PParticleType> getPacketCodec()
	{
		return packetCodec;
	}
}
