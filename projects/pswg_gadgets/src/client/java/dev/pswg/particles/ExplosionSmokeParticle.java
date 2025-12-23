package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class ExplosionSmokeParticle extends BillboardParticle
{
	private final int variant;
	final int NUM_VARIANTS = 9;
	protected ExplosionSmokeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.getFirst());
		scale(7.5f);
		setBoundingBoxSpacing(0.25f, 0.25f);
		maxAge = random.nextInt(120) + 1280;
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;

		this.setAlpha(0.9f);
		variant = random.nextInt(NUM_VARIANTS);
	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	public void tick()
	{

		lastX = x;
		lastY = y;
		lastZ = z;
		age++;
		if (alpha <= 0.0f||age>=maxAge) {
			markDead();
			return;
		}
		velocityX += random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1);
		velocityZ += random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1);
		velocityY += 0.001;
		move(velocityX, velocityY, velocityZ);
		if(age >=  50 && this.alpha > 0.005f) {
			alpha -= 0.0025f;
		}
	}

	@Environment(value=EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			ExplosionSmokeParticle explosionSmokeParticle = new ExplosionSmokeParticle(world, x, y, z, velocityX,velocityY,velocityZ,  spriteProvider);
			explosionSmokeParticle.setSprite(spriteProvider.getFirst());
			return explosionSmokeParticle;
		}
	}
}
