package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class SmokeParticle extends SpriteBillboardParticle
{
	private final int variant;
	final int NUM_VARIANTS = 9;

	protected SmokeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(16f);

		setBoundingBoxSpacing(0.25f, 0.25f);
		this.setAlpha(0.95f);
		variant = random.nextInt(NUM_VARIANTS);
	}

	@Override
	public void tick()
	{

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		if (alpha <= 0.0f)
		{
			markDead();
			return;
		}
		if (age >= 100)
		{
			alpha -= 0.0025f;
		}
		velocityX += random.nextFloat() / 2000f * (age / 50f) * (random.nextBoolean() ? 1 : -1);
		velocityZ += random.nextFloat() / 2000f * (age / 50f) * (random.nextBoolean() ? 1 : -1);
		if (age >= 150)
		{
			velocityY += 0.00005;
		}
		move(velocityX, velocityY, velocityZ);
		setColor(1, 0.8f, 1);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			SmokeParticle explosionSmokeParticle = new SmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			explosionSmokeParticle.setSprite(spriteProvider);
			return explosionSmokeParticle;
		}
	}
}
