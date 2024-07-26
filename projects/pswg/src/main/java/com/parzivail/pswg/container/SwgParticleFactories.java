package com.parzivail.pswg.container;

import com.parzivail.pswg.client.particle.ExplosionSmokeParticle;
import com.parzivail.pswg.client.particle.WakeParticle;
import com.parzivail.pswg.client.particle.WaterWakeParticle;
import com.parzivail.pswg.client.render.player.PlayerSocket;
import com.parzivail.pswg.features.blasters.client.particle.ScorchParticle;
import com.parzivail.pswg.features.blasters.client.particle.SlugTrailParticle;
import com.parzivail.pswg.features.blasters.client.particle.SparkParticle;
import com.parzivail.pswg.network.PlayerSocketSparksS2CPacket;
import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import org.joml.Vector3f;

import static com.parzivail.pswg.container.SwgParticleTypes.*;

public class SwgParticleFactories
{
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

	public static void handlePlayerSocketPyro(PlayerSocketSparksS2CPacket packet, ClientPlayNetworking.Context context)
	{
		var entity = context.client().world.getEntityById(packet.playerId());
		if (!(entity instanceof PlayerEntity player))
			return;

		var socket = PlayerSocket.getSocket(player, packet.playerSocket());
		if (socket == null)
			return;

		for (var i = 0; i < 25; i++)
		{
			var vx = context.client().world.random.nextGaussian() * 0.03;
			var vy = context.client().world.random.nextGaussian() * 0.03;
			var vz = context.client().world.random.nextGaussian() * 0.03;

			var sparkVelocity = socket.normal().mul(0.6f * (context.client().world.random.nextFloat() * 0.5f + 0.5f), new Vector3f());
			context.client().world.addParticle(
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
				var smokeVelocity = socket.normal().mul(0.05f * (context.client().world.random.nextFloat() * 0.5f + 0.5f), new Vector3f());
				context.client().world.addParticle(
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
	}
}
