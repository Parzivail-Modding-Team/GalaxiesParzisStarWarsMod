package com.parzivail.util.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class PParticleType extends ParticleType<PParticleType> implements ParticleEffect
{
	private static final ParticleEffect.Factory<PParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<>()
	{
		@Override
		public PParticleType read(ParticleType<PParticleType> particleType, StringReader stringReader)
		{
			return (PParticleType)particleType;
		}

		@Override
		public PParticleType read(ParticleType<PParticleType> particleType, PacketByteBuf packetByteBuf)
		{
			return (PParticleType)particleType;
		}
	};
	private final Codec<PParticleType> codec = Codec.unit(this::getType);

	public PParticleType(boolean alwaysShow)
	{
		super(alwaysShow, PARAMETER_FACTORY);
	}

	@Override
	public PParticleType getType()
	{
		return this;
	}

	@Override
	public Codec<PParticleType> getCodec()
	{
		return this.codec;
	}

	@Override
	public void write(PacketByteBuf buf)
	{
	}

	@Override
	public String asString()
	{
		return Registries.PARTICLE_TYPE.getId(this).toString();
	}
}
