package dev.pswg.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class GasParticleEffect implements ParticleEffect
{
	private static final Codec<Integer> GAS_ENTITY_ID_CODEC = Codec.INT;
	private final ParticleType<GasParticleEffect> type;
	public final int gas;

	public GasParticleEffect(ParticleType<GasParticleEffect> type, int gasId)
	{
		this.type = type;
		this.gas = gasId;
	}

	public static MapCodec<GasParticleEffect> createCodec(ParticleType<GasParticleEffect> type)
	{
		//Codec.
		return GAS_ENTITY_ID_CODEC.<GasParticleEffect>xmap(gasEntityId -> new GasParticleEffect(type, gasEntityId), effect -> effect.gas)
		                          .fieldOf("gas_entity_id");
	}

	public static PacketCodec<? super RegistryByteBuf, GasParticleEffect> createPacketCodec(ParticleType<GasParticleEffect> type)
	{
		return PacketCodecs.INTEGER.xmap(gasId -> new GasParticleEffect(type, gasId), effect -> effect.gas);
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
