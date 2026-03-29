package dev.pswg.particles;

import dev.pswg.particle.CrossPointingParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

public class TripwireLaserParticle extends CrossPointingParticle
{
	private final SpriteSet _spriteProvider;

	protected TripwireLaserParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this._spriteProvider = spriteProvider;
		this.setAlpha(0.1F);
		this.lifetime = 3;
		this.scale(0.0625f);
		this.setColor(
				0.05f + (float)this.random.nextIntBetweenInclusive(0, 100) / 1000f,
				0.65f + (float)this.random.nextIntBetweenInclusive(0, 100) / 1000f,
				0.9f + (float)this.random.nextIntBetweenInclusive(0, 100) / 1000f
		);
		this.setSpriteFromAge(spriteProvider);
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.removed)
		{
			this.setSpriteFromAge(this._spriteProvider);
			this.yd -= 0.00001;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			return new TripwireLaserParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}
}
