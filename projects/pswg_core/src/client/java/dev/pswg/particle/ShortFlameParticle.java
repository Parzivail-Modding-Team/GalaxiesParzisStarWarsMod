package dev.pswg.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ShortFlameParticle extends RisingParticle
{
	ShortFlameParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite)
	{
		super(clientWorld, d, e, f, g, h, i, sprite);
		this.lifetime = clientWorld.getRandom().nextIntBetweenInclusive(2, 5);
	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}

	@Override
	public void move(double dx, double dy, double dz)
	{
		this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
		this.setLocationFromBoundingbox();
	}

	@Override
	public float getQuadSize(float tickDelta)
	{
		float f = ((float)this.age + tickDelta) / (float)this.lifetime;
		return this.quadSize * (1.0F - f * f * 0.5F);
	}

	@Override
	public int getLightCoords(float tint)
	{
		float f = ((float)this.age + tint) / (float)this.lifetime;
		f = Mth.clamp(f, 0.0F, 1.0F);
		int i = super.getLightCoords(tint);
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
			ShortFlameParticle flameParticle = new ShortFlameParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.first());
			flameParticle.setSprite(this.spriteProvider.first());
			return flameParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SmallFactory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteProvider;

		public SmallFactory(SpriteSet spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			ShortFlameParticle flameParticle = new ShortFlameParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.first());
			flameParticle.setSprite(this.spriteProvider.first());
			flameParticle.scale(0.5F);
			return flameParticle;
		}
	}
}

