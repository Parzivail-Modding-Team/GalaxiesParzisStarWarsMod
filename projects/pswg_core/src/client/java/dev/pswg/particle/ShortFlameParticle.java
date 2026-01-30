package dev.pswg.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ShortFlameParticle extends AbstractSlowingParticle
{
	ShortFlameParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite)
	{
		super(clientWorld, d, e, f, g, h, i, sprite);
		this.maxAge = clientWorld.random.nextBetween(2, 5);
	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	public void move(double dx, double dy, double dz)
	{
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}

	@Override
	public float getSize(float tickDelta)
	{
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		return this.scale * (1.0F - f * f * 0.5F);
	}

	@Override
	public int getBrightness(float tint)
	{
		float f = ((float)this.age + tint) / (float)this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240)
		{
			j = 240;
		}

		return j | k << 16;
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
			ShortFlameParticle flameParticle = new ShortFlameParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
			flameParticle.setSprite(this.spriteProvider.getFirst());
			return flameParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SmallFactory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public SmallFactory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			ShortFlameParticle flameParticle = new ShortFlameParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
			flameParticle.setSprite(this.spriteProvider.getFirst());
			flameParticle.scale(0.5F);
			return flameParticle;
		}
	}
}

