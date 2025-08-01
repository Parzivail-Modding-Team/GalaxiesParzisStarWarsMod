package dev.pswg.particles;

import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.particle.GasParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ShortFlameParticle extends AbstractSlowingParticle
{
	ShortFlameParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
	{
		super(clientWorld, d, e, f, g, h, i);
		this.maxAge = clientWorld.random.nextBetween(2, 5);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			ShortFlameParticle flameParticle = new ShortFlameParticle(clientWorld, d, e, f, g, h, i);
			flameParticle.setSprite(this.spriteProvider);
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
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			ShortFlameParticle flameParticle = new ShortFlameParticle(clientWorld, d, e, f, g, h, i);
			flameParticle.setSprite(this.spriteProvider);
			flameParticle.scale(0.5F);
			return flameParticle;
		}
	}
}

