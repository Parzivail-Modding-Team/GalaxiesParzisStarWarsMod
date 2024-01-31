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

import static com.parzivail.pswg.container.SwgParticleTypes.*;

public class SwgParticleFactories
{
	//public static final PParticleType SLUG_TRAIL = register(Resources.id("slug_trail"), true, SlugTrailParticle.Factory::new);
	//public static final PParticleType SPARK = register(Resources.id("spark"), true, SparkParticle.Factory::new);
	//	public static final PParticleType SCORCH = register(Resources.id("scorch"), true, ScorchParticle.Factory::new);
	//public static final PParticleType WATER_WAKE = register(Resources.id("water_wake"), true, WaterWakeParticle.Factory::new);
	//	public static final ParticleType<BlockStateParticleEffect> WAKE = registerBlockStateBased(Resources.id("wake"), true, new WakeParticle.Factory());
	//	public static final PParticleType EXPLOSION_SMOKE = register(Resources.id("explosion_smoke"), true, ExplosionSmokeParticle.Factory::new);
	//public static final PParticleType EXPLOSION_SMOKE = registerWithoutFactory(Resources.id("explosion_smoke"), true);
	private static void registerFactory(ParticleType particleType, ParticleFactoryRegistry.PendingParticleFactory<PParticleType> factory)
	{

		ParticleFactoryRegistry.getInstance().register(particleType, factory);
	}

	private static void registerBlockStateBasedFactory(ParticleType particleType, ParticleFactory<BlockStateParticleEffect> factory)
	{

		ParticleFactoryRegistry.getInstance().register(particleType, factory);
	}

	public static void register()
	{
		registerFactory(EXPLOSION_SMOKE, ExplosionSmokeParticle.Factory::new);
		registerFactory(WATER_WAKE, WaterWakeParticle.Factory::new);
		registerFactory(SCORCH, ScorchParticle.Factory::new);
		registerFactory(SPARK, SparkParticle.Factory::new);
		registerFactory(SLUG_TRAIL, SlugTrailParticle.Factory::new);
		registerBlockStateBasedFactory(WAKE, new WakeParticle.Factory());
	}

	public static void handlePlayerSocketPyro(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var playerId = buf.readInt();
		var playerSocket = buf.readString();

		client.execute(() -> {
			assert client.world != null;

			var entity = client.world.getEntityById(playerId);
			if (!(entity instanceof PlayerEntity player))
				return;

			var socket = PlayerSocket.getSocket(player, playerSocket);
			if (socket == null)
				return;

			for (var i = 0; i < 25; i++)
			{
				var vx = client.world.random.nextGaussian() * 0.03;
				var vy = client.world.random.nextGaussian() * 0.03;
				var vz = client.world.random.nextGaussian() * 0.03;

				var sparkVelocity = socket.normal().mul(0.6f * (client.world.random.nextFloat() * 0.5f + 0.5f), new Vector3f());
				client.world.addParticle(
						SwgParticleTypes.SPARK,
						socket.position().x,
						socket.position().y,
						socket.position().z,
						sparkVelocity.x + vx,
						sparkVelocity.y + vy,
						sparkVelocity.z + vz
				);

				if (i % 3 == 0)
				{
					var smokeVelocity = socket.normal().mul(0.05f * (client.world.random.nextFloat() * 0.5f + 0.5f), new Vector3f());
					client.world.addParticle(
							ParticleTypes.SMOKE,
							socket.position().x,
							socket.position().y,
							socket.position().z,
							smokeVelocity.x + vx,
							smokeVelocity.y + vy,
							smokeVelocity.z + vz
					);
				}
			}
		});
	}
}
