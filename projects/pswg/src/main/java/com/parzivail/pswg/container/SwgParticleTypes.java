package com.parzivail.pswg.container;

import com.mojang.serialization.MapCodec;
import com.parzivail.pswg.Resources;
import com.parzivail.util.client.particle.PParticleType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SwgParticleTypes
{
	public static final PParticleType SLUG_TRAIL = register(Resources.id("slug_trail"), true);
	public static final PParticleType SPARK = register(Resources.id("spark"), true);
	public static final PParticleType SCORCH = register(Resources.id("scorch"), true);
	public static final PParticleType WATER_WAKE = register(Resources.id("water_wake"), true);
	public static final ParticleType<BlockStateParticleEffect> WAKE = registerBlockStateBased(Resources.id("wake"), true);
	public static final PParticleType EXPLOSION_SMOKE = register(Resources.id("explosion_smoke"), true);
	private static PParticleType register(Identifier name, boolean alwaysShow)
	{
		return Registry.register(Registries.PARTICLE_TYPE, name, new PParticleType(alwaysShow));
	}

	private static ParticleType<BlockStateParticleEffect> registerBlockStateBased(Identifier name, boolean alwaysShow)
	{
		var particleType = Registry.register(Registries.PARTICLE_TYPE, name, new ParticleType<BlockStateParticleEffect>(alwaysShow)
		{

			private final PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> PACKET_CODEC = BlockStateParticleEffect.createPacketCodec(this);
			private final MapCodec<BlockStateParticleEffect> CODEC = BlockStateParticleEffect.createCodec(this);

			@Override
			public MapCodec<BlockStateParticleEffect> getCodec()
			{
				return CODEC;
			}

			@Override
			public PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> getPacketCodec()
			{
				return PACKET_CODEC;
			}
		});
		return particleType;
	}
	public static void register()
	{
	}
}
