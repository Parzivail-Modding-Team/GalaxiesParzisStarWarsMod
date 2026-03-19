package dev.pswg.particle;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GasParticleEffect implements ParticleOptions
{
	/// Pair where the first is the gasId and second is minConcentration
	private static final Codec<Pair<String, String>> GAS_ENTITY_ID_CODEC = Codec.pair(Codec.STRING, Codec.STRING);
	private final ParticleType<GasParticleEffect> type;
	public final String gasId;
	public final String particleId;

	public GasParticleEffect(ParticleType<GasParticleEffect> type, String gasId, String particleId)
	{
		this.type = type;
		this.gasId = gasId;
		this.particleId = particleId;
	}

	public static MapCodec<GasParticleEffect> createCodec(ParticleType<GasParticleEffect> type)
	{
		return GAS_ENTITY_ID_CODEC.<GasParticleEffect>xmap(pair -> new GasParticleEffect(type, pair.getFirst(), pair.getSecond()), effect -> Pair.of(effect.gasId, effect.particleId))
		                          .fieldOf("gas_entity_id");
	}

	public static StreamCodec<? super RegistryFriendlyByteBuf, GasParticleEffect> createPacketCodec(ParticleType<GasParticleEffect> type)
	{
		return ByteBufCodecs.fromCodec(GAS_ENTITY_ID_CODEC).map(pair -> new GasParticleEffect(type, pair.getFirst(), pair.getSecond()), effect -> Pair.of(effect.gasId, effect.particleId));
	}

	@Override
	public ParticleType<?> getType()
	{
		return type;
	}

	public String getGasEntityId()
	{
		return gasId;
	}
}
