package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.DecalParticle;
import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value= EnvType.CLIENT)
public class ExplosionSmokeParticle extends SpriteBillboardParticle
{
	private static final int NUM_VARIANTS = 12;
	private final int variant;
	protected ExplosionSmokeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		this.scale(5.0f);
		this.setBoundingBoxSpacing(0.25f, 0.25f);
		this.maxAge = this.random.nextInt(50) + 280;
		this.velocityX = vX;
		this.velocityY = vY + (double)(this.random.nextFloat() / 500.0f);
		this.velocityZ = vZ;
		//this.angle = this.random.nextFloat() * MathHelper.PI;

		this.variant = this.random.nextInt(NUM_VARIANTS);
	}


	@Override
	public void tick()
	{

		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge || this.alpha <= 0.0f) {
			this.markDead();
			return;
		}
		this.velocityX += (double)(this.random.nextFloat() / 500.0f * (float)(this.random.nextBoolean() ? 1 : -1));
		this.velocityZ += (double)(this.random.nextFloat() / 500.0f * (float)(this.random.nextBoolean() ? 1 : -1));
		this.velocityY +=0.0015;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.age >= this.maxAge - 60 && this.alpha > 0.01f) {
			this.alpha -= 0.025f;
		}
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	@Environment(value=EnvType.CLIENT)
	public static class Factory  implements ParticleFactory<PParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(PParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			ExplosionSmokeParticle explosionSmokeParticle = new ExplosionSmokeParticle(world, x, y, z, velocityX,velocityY,velocityZ,  this.spriteProvider);
			explosionSmokeParticle.setAlpha(0.9f);
			explosionSmokeParticle.setSprite(this.spriteProvider);
			return explosionSmokeParticle;
		}
	}
}
