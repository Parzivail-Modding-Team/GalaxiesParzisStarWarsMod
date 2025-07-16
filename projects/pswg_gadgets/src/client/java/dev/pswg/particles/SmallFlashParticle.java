package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SmallFlashParticle extends SpriteBillboardParticle
{
	SmallFlashParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		this.maxAge = 4;
		this.setSprite(spriteProvider);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		this.setAlpha(0.6F - ((float)this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
		super.render(vertexConsumer, camera, tickDelta);
	}

	@Override
	public float getSize(float tickDelta)
	{
		return 7f * MathHelper.sin(((float)this.age + tickDelta - 1.0f) * 0.05f * (float)Math.PI);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			SmallFlashParticle flashParticle = new SmallFlashParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			flashParticle.setSprite(spriteProvider);
			return flashParticle;
		}
	}
}

