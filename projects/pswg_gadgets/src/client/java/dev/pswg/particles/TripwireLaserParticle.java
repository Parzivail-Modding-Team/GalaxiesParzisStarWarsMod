package dev.pswg.particles;

import dev.pswg.particle.CrossPointingParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class TripwireLaserParticle extends CrossPointingParticle
{
	protected TripwireLaserParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this.setAlpha(0.1F);
		this.maxAge = 3;
		this.scale = 0.0625f;
		this.setColor(0.05f + (float)clientWorld.random.nextBetween(0, 100) / 1000f, 0.65f + (float)clientWorld.random.nextBetween(0, 100) / 1000f, 0.9f + (float)clientWorld.random.nextBetween(0, 100) / 1000f);
		this.updateSprite(spriteProvider);
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.dead)
		{
			this.updateSprite(this.spriteProvider);
			this.velocityY -= 0.00001;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			return new TripwireLaserParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}
}