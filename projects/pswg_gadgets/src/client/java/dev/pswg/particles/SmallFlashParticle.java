package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class SmallFlashParticle extends BillboardParticle
{
	SmallFlashParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.getFirst());
		this.maxAge = 4;
		this.updateSprite(spriteProvider);
	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		this.setAlpha(0.6F - ((float)this.age + tickProgress - 1.0F) * 0.25F * 0.5F);
		super.render(submittable, camera, rotation, tickProgress);
	}

	@Override
	public float getSize(float tickDelta)
	{
		return 7f * MathHelper.sin(((float)this.age + tickDelta - 1.0f) * 0.05f * (float)Math.PI);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<TintedParticleEffect>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(TintedParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			SmallFlashParticle flashParticle = new SmallFlashParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			flashParticle.setColor(parameters.getRed(), parameters.getGreen(), parameters.getBlue());
			flashParticle.setSprite(spriteProvider.getFirst());
			return flashParticle;
		}
	}
}

