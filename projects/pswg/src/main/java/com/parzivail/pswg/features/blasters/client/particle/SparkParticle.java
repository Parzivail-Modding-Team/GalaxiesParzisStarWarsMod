package com.parzivail.pswg.features.blasters.client.particle;

import com.parzivail.util.client.particle.CrossPointingParticle;
import com.parzivail.util.client.particle.PParticleType;
import com.parzivail.util.math.Ease;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SparkParticle extends CrossPointingParticle
{
	protected SparkParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this.velocityMultiplier = 1;
		this.scale = (float)(this.random.nextFloat() * 0.05 + 0.06);
		this.setAlpha(1.0F);
		this.setColor(1, 0, 0);
		this.maxAge = (int)(this.random.nextFloat() * 10 + 5);
		this.setSpriteForAge(spriteProvider);
		this.collidesWithWorld = true;
		this.velocityX = vX;
		this.velocityY = vY;
		this.velocityZ = vZ;
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint)
	{
		float f = ((float)this.age + tint) / (float)this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240)
		{
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.dead)
		{
			this.setSpriteForAge(this.spriteProvider);
			if (this.age > this.maxAge / 2)
			{
				this.setAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
			}

			if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))).isAir())
			{
				this.velocityY -= 0.0245;
			}

			var a = (this.age / (float)this.maxAge);
			this.setColor(1, MathHelper.clamp(Ease.outCubic(3 * a), 0, 1), MathHelper.clamp(Ease.outCubic(1.5f * a), 0, 1));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<PParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(PParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			return new SparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
