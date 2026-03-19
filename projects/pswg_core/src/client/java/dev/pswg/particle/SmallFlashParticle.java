package dev.pswg.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class SmallFlashParticle extends SingleQuadParticle
{
	SmallFlashParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.first());
		this.lifetime = 4;
		this.setSpriteFromAge(spriteProvider);
	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}

	@Override
	protected void extractRotatedQuad(QuadParticleRenderState submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		this.setAlpha(0.6F - ((float)this.age + tickProgress - 1.0F) * 0.25F * 0.5F);
		super.extractRotatedQuad(submittable, camera, rotation, tickProgress);
	}

	@Override
	public float getQuadSize(float tickDelta)
	{
		return 7f * Mth.sin(((float)this.age + tickDelta - 1.0f) * 0.05f * (float)Math.PI);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleProvider<ColorParticleOption>
	{
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(ColorParticleOption parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			SmallFlashParticle flashParticle = new SmallFlashParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			flashParticle.setColor(parameters.getRed(), parameters.getGreen(), parameters.getBlue());
			flashParticle.setSprite(spriteProvider.first());
			return flashParticle;
		}
	}
}

