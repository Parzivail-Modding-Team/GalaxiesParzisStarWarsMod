package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeParticle extends SpriteBillboardParticle
{
	int phase;
	protected FragmentationGrenadeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(1f);
		setBoundingBoxSpacing(0.25f, 0.25f);
		this.collidesWithWorld = false;
		this.setAlpha(0.6f);
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
		phase = 1;
	}

	@Override
	protected int getBrightness(float tint)
	{
		return 255;
	}

	@Override
	public void tick()
	{

		age++;
		if (phase == 1)
		{
			scale += 0.02f;
			alpha += 0.01f;
			if (alpha >= 1f)
			{

				phase = 2;
			}
		}
		else if (phase == 2)
		{
			scale -= 0.075f;
			alpha -= 0.025f;

			if (alpha <= 0.6f)
			{

				phase = 3;
			}
		}
		else if (phase == 3)
		{
			scale += 0.45f;
			alpha += 0.04f;
			if (alpha >= 0.9f)
			{
				phase = 4;
			}
		}
		else if (phase == 4)
		{
			scale += 0.65f;
			alpha -= 0.075f;
			if (alpha <= 0f)
			{
				markDead();
			}
		}
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<PParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(PParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			FragmentationGrenadeParticle fragmentationGrenadeParticle = new FragmentationGrenadeParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			fragmentationGrenadeParticle.setSprite(spriteProvider);
			return fragmentationGrenadeParticle;
		}
	}
}
