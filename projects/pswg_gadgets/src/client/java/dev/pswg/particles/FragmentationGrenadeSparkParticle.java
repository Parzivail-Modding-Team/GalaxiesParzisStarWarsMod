package dev.pswg.particles;

import dev.pswg.particle.CrossPointingParticle;
import dev.pswg.utility.math.Ease;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class FragmentationGrenadeSparkParticle extends CrossPointingParticle
{
	private final SpriteSet _spriteProvider;

	protected FragmentationGrenadeSparkParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this._spriteProvider = spriteProvider;
		this.friction = 1;
		this.setAlpha(1F);
		this.setColor(0, 0, 1);
		this.lifetime = (int)(this.random.nextFloat() * 20 + 10);
		this.scale((float)(this.random.nextFloat() * 0.25 + 0.15));
		this.setSpriteFromAge(spriteProvider);
		this.hasPhysics = true;
		this.xd = vX;
		this.yd = vY;
		this.zd = vZ;
	}

	@Override
	public int getLightColor(float tint)
	{
		float f = ((float)this.age + tint) / (float)this.lifetime;
		f = Mth.clamp(f, 0.0F, 1.0F);
		int i = super.getLightColor(tint);
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
		if (!this.removed)
		{
			this.setSpriteFromAge(this._spriteProvider);
			if (this.age > this.lifetime / 2)
			{
				this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
			}

			if (this.level.getBlockState(new BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z))).isAir())
			{
				this.yd -= 0.0145;
			}

			var a = (this.age / (float)this.lifetime) / 4f;
			this.setColor(Mth.clamp(Ease.outCubic(1.5f * a), 0, 1), Mth.clamp(Ease.outCubic(3 * a), 0, 1), 1);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, net.minecraft.util.RandomSource random)
		{
			return new FragmentationGrenadeSparkParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		}
	}
}
