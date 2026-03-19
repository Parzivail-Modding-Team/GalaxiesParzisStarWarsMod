package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class ExplosionSmokeParticle extends SingleQuadParticle
{
	private final int variant;
	final int NUM_VARIANTS = 9;
	protected ExplosionSmokeParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.first());
		scale(7.5f);
		setSize(0.25f, 0.25f);
		lifetime = random.nextInt(120) + 1280;
		xd = vX;
		yd = vY + (double)(random.nextFloat() / 500.0f);
		zd = vZ;

		this.setAlpha(0.9f);
		variant = random.nextInt(NUM_VARIANTS);
	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}

	@Override
	public void tick()
	{

		xo = x;
		yo = y;
		zo = z;
		age++;
		if (alpha <= 0.0f||age>=lifetime) {
			remove();
			return;
		}
		xd += random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1);
		zd += random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1);
		yd += 0.001;
		move(xd, yd, zd);
		if(age >=  50 && this.alpha > 0.005f) {
			alpha -= 0.0025f;
		}
	}

	@Environment(value=EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			ExplosionSmokeParticle explosionSmokeParticle = new ExplosionSmokeParticle(world, x, y, z, velocityX,velocityY,velocityZ,  spriteProvider);
			explosionSmokeParticle.setSprite(spriteProvider.first());
			return explosionSmokeParticle;
		}
	}
}
