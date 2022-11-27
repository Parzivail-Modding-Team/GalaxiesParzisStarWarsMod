package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;

public class WaterWakeParticle extends SpriteBillboardParticle
{
	private final SpriteProvider spriteProvider;
	private final float maxScale;

	public WaterWakeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider)
	{
		super(world, x, y, z);
		this.spriteProvider = spriteProvider;
		float f = this.random.nextFloat() * 0.3F + 0.7F;
		this.red = f;
		this.green = f;
		this.blue = f;
		this.maxScale = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
		this.scale = 0;
		this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
		this.setSpriteForAge(spriteProvider);

		this.gravityStrength = 0.02f;
		this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * velocityY * 0.1f;
		this.velocityX = (Math.random() * 2.0 - 1.0) * velocityX;
		this.velocityZ = (Math.random() * 2.0 - 1.0) * velocityZ;
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick()
	{
		this.scale = this.maxScale * (float)Math.min(Math.pow(20f * this.age / this.maxAge, 4), 1);
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;

		if (this.age++ >= maxAge)
			this.markDead();
		else
		{
			this.velocityY -= this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);

			this.velocityX *= 0.98;
			this.velocityY *= 0.98;
			this.velocityZ *= 0.98;

			if (this.onGround)
			{
				this.velocityX *= 0.7;
				this.velocityZ *= 0.7;
			}
		}

		this.setSpriteForAge(this.spriteProvider);
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
			return new WaterWakeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
