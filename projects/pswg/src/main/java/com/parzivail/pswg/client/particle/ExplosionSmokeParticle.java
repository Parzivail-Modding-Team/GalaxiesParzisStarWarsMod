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
	private final int variant;
	protected ExplosionSmokeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(7.5f);
		final int NUM_VARIANTS = 12;
		setBoundingBoxSpacing(0.25f, 0.25f);
		maxAge = random.nextInt(120) + 640;
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
		//angle = random.nextFloat() * MathHelper.PI;

		variant = random.nextInt(NUM_VARIANTS);
	}


	@Override
	public void tick()
	{

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		if (alpha <= 0.0f||age>=maxAge) {
			markDead();
			return;
		}
		velocityX += (double)(random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1));
		velocityZ += (double)(random.nextFloat() / 500.0f * (float)(random.nextBoolean() ? 1 : -1));
		velocityY +=0.0015;
		move(velocityX, velocityY, velocityZ);
		if(age >=  75 && this.alpha > 0.01f) {
			alpha-=0.003f;
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
			ExplosionSmokeParticle explosionSmokeParticle = new ExplosionSmokeParticle(world, x, y, z, velocityX,velocityY,velocityZ,  spriteProvider);
			explosionSmokeParticle.setAlpha(0.9f);
			explosionSmokeParticle.setSprite(spriteProvider);
			return explosionSmokeParticle;
		}
	}
}
