package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeParticle extends SpriteBillboardParticle
{
	protected FragmentationGrenadeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(1f);
		setBoundingBoxSpacing(0.25f, 0.25f);
		this.collidesWithWorld = false;
		this.setAlpha(1f);
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
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
		if (alpha <= 0.0f)
		{
			markDead();
			return;
		}
		if (age >= 40)
		{
			alpha -= 0.05f;
		}
		scale += 0.15f;
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
