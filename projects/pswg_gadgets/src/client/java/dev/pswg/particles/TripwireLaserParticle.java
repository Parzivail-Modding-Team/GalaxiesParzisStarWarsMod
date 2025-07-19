package dev.pswg.particles;

import dev.pswg.particle.CrossPointingParticle;
import dev.pswg.util.math.Ease;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TripwireLaserParticle extends CrossPointingParticle
{
	protected TripwireLaserParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this.setAlpha(0.1F);
		this.maxAge = 3;
		this.scale = 0.0625f;
		this.setColor(0.05f + (float)clientWorld.random.nextBetween(0, 100) / 1000f, 0.65f + (float)clientWorld.random.nextBetween(0, 100) / 1000f, 0.9f + (float)clientWorld.random.nextBetween(0, 100) / 1000f);
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.dead)
		{
			this.setSpriteForAge(this.spriteProvider);
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
		public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			return new TripwireLaserParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}