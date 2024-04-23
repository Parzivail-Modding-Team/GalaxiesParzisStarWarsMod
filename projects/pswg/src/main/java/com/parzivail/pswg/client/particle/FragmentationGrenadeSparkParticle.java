package com.parzivail.pswg.client.particle;

import com.parzivail.pswg.features.blasters.client.particle.SparkParticle;
import com.parzivail.util.client.particle.PParticleType;
import com.parzivail.util.math.Ease;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class FragmentationGrenadeSparkParticle extends SparkParticle
{
	protected FragmentationGrenadeSparkParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, vX, vY, vZ, spriteProvider);
		this.setColor(0, 0, 1);
		this.maxAge = (int)(this.random.nextFloat() * 20 + 10);
		this.scale = (float)(this.random.nextFloat() * 0.25 + 0.15);
	}

	@Override
	public void tick()
	{
		super.tick();
		var a = (this.age / (float)this.maxAge) / 4f;
		this.velocityY -= 0.0145;
		this.setColor(MathHelper.clamp(Ease.outCubic(1.5f * a), 0, 1), MathHelper.clamp(Ease.outCubic(3 * a), 0, 1), 1);
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
			return new FragmentationGrenadeSparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
