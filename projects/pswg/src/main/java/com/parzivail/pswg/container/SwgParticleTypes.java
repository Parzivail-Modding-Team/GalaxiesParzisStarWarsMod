package com.parzivail.pswg.container;

import com.mojang.serialization.Codec;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.particle.ExplosionSmokeParticle;
import com.parzivail.pswg.client.particle.WakeParticle;
import com.parzivail.pswg.client.particle.WaterWakeParticle;
import com.parzivail.pswg.client.render.player.PlayerSocket;
import com.parzivail.pswg.features.blasters.client.particle.ScorchParticle;
import com.parzivail.pswg.features.blasters.client.particle.SlugTrailParticle;
import com.parzivail.pswg.features.blasters.client.particle.SparkParticle;
import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class SwgParticleTypes
{
	public static final PParticleType SLUG_TRAIL = register(Resources.id("slug_trail"), true);
	public static final PParticleType SPARK = register(Resources.id("spark"), true);
	public static final PParticleType SCORCH = register(Resources.id("scorch"), true);
	public static final PParticleType WATER_WAKE = register(Resources.id("water_wake"), true);
	public static final ParticleType<BlockStateParticleEffect> WAKE = registerBlockStateBased(Resources.id("wake"), true);
	public static final PParticleType EXPLOSION_SMOKE = register(Resources.id("explosion_smoke"), true);
	public static final PParticleType FRAGMENTATION_GRENADE = register(Resources.id("fragmentation_grenade"), true);
	private static PParticleType register(Identifier name, boolean alwaysShow)
	{
		return Registry.register(Registries.PARTICLE_TYPE, name, new PParticleType(alwaysShow));
	}

	private static ParticleType<BlockStateParticleEffect> registerBlockStateBased(Identifier name, boolean alwaysShow)
	{
		var particleType = Registry.register(Registries.PARTICLE_TYPE, name, new ParticleType<BlockStateParticleEffect>(alwaysShow, BlockStateParticleEffect.PARAMETERS_FACTORY)
		{
			@Override
			public Codec<BlockStateParticleEffect> getCodec()
			{
				return BlockStateParticleEffect.createCodec(this);
			}
		});
		return particleType;
	}
	public static void register()
	{
	}
}
