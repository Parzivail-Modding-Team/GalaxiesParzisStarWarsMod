package dev.pswg.particle;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class GasParticleEffect implements ParticleEffect
{
	/// Pair where the first is the gasId and second is minConcentration
	private static final Codec<Pair<Integer, Float>> GAS_ENTITY_ID_CODEC = Codec.pair(Codec.INT, Codec.FLOAT);
	private final ParticleType<GasParticleEffect> type;
	public final int gas;
	public final float minConcentration;

	public GasParticleEffect(ParticleType<GasParticleEffect> type, int gasId, float minConcentration)
	{
		this.type = type;
		this.gas = gasId;
		this.minConcentration = minConcentration;
	}

	public static MapCodec<GasParticleEffect> createCodec(ParticleType<GasParticleEffect> type)
	{
		//Codec.
		return GAS_ENTITY_ID_CODEC.<GasParticleEffect>xmap(pair -> new GasParticleEffect(type, pair.getFirst(), pair.getSecond()), effect -> Pair.of(effect.gas, effect.minConcentration))
		                          .fieldOf("gas_entity_id");
	}

	public static PacketCodec<? super RegistryByteBuf, GasParticleEffect> createPacketCodec(ParticleType<GasParticleEffect> type)
	{
		return PacketCodecs.codec(GAS_ENTITY_ID_CODEC).xmap(pair -> new GasParticleEffect(type, pair.getFirst(), pair.getSecond()), effect -> Pair.of(effect.gas, effect.minConcentration));
	}

	@Override
	public ParticleType<?> getType()
	{
		return type;
	}

	public int getGasEntityId()
	{
		return gas;
	}
}
