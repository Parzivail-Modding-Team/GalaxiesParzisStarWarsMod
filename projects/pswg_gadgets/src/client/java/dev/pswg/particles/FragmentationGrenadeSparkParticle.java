package dev.pswg.particles;

import dev.pswg.particle.CrossPointingParticle;
import dev.pswg.utility.math.Ease;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class FragmentationGrenadeSparkParticle extends CrossPointingParticle
{
	protected FragmentationGrenadeSparkParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this.velocityMultiplier = 1;
		this.setAlpha(1F);
		this.setColor(0, 0, 1);
		this.maxAge = (int)(this.random.nextFloat() * 20 + 10);
		this.scale = (float)(this.random.nextFloat() * 0.25 + 0.15);
		this.updateSprite(spriteProvider);
		this.collidesWithWorld = true;
		this.velocityX = vX;
		this.velocityY = vY;
		this.velocityZ = vZ;
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
			this.updateSprite(this.spriteProvider);
			if (this.age > this.maxAge / 2)
			{
				this.setAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
			}

			if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))).isAir())
			{
				this.velocityY -= 0.0145;
			}

			var a = (this.age / (float)this.maxAge) / 4f;
			this.setColor(MathHelper.clamp(Ease.outCubic(1.5f * a), 0, 1), MathHelper.clamp(Ease.outCubic(3 * a), 0, 1), 1);
		}
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
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, net.minecraft.util.math.random.Random random)
		{
			return new FragmentationGrenadeSparkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}
}