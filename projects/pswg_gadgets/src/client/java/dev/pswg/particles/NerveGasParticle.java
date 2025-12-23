package dev.pswg.particles;

import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.particle.GasParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class NerveGasParticle extends GasParticle
{

	protected NerveGasParticle(NerveGasEntity gasEntity, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider, float minConcentration)
	{
		super(gasEntity, clientWorld, x, y, z, vX, vY, vZ, spriteProvider, minConcentration);
		setColor(1, 0.9f, 0.6f);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<GasParticleEffect>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(GasParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			NerveGasEntity entity = (NerveGasEntity)MinecraftClient.getInstance().world.getEntityById(parameters.getGasEntityId());
			NerveGasParticle gasParticle = new NerveGasParticle(entity, world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, parameters.minConcentration);
			gasParticle.setSprite(spriteProvider.getFirst());
			return gasParticle;
		}
	}
}
