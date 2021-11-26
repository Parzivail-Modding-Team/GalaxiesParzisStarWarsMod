package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.DecalParticle;
import com.parzivail.util.client.particle.PParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ScorchParticle extends DecalParticle
{
	private static final int NUM_VARIANTS = 3;
	private final int variant;

	private final float heat;

	protected ScorchParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, float heat, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this.velocityMultiplier = 1;
		this.scale = 0.1f;
		this.setColorAlpha(1.0F);
		var a = MathHelper.lerp(heat, 1, MathHelper.clamp((this.age / (float)this.maxAge) * 2f, 0, 1));
		this.setColor(MathHelper.clamp(getRed(a), 0, 1), MathHelper.clamp(getGreen(a), 0, 1), MathHelper.clamp(getBlue(a), 0, 1));
		this.maxAge = 200;
		this.setSpriteForAge(spriteProvider);
		this.collidesWithWorld = false;
		this.velocityX = vX;
		this.velocityY = vY;
		this.velocityZ = vZ;
		this.angle = clientWorld.random.nextFloat() * MathHelper.PI;

		this.heat = heat;

		this.variant = clientWorld.random.nextInt(NUM_VARIANTS);
	}

	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

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

	private float getRed(float t)
	{
		t *= 5;
		return -0.0581f * t * t + 0.0982f * t + 0.963f;
	}

	private float getGreen(float t)
	{
		t *= 5;
		return 0.0292f * t * t - 0.314f * t + 0.837f;
	}

	private float getBlue(float t)
	{
		t *= 5;
		return 0.0166f * t * t - 0.152f * t + 0.348f;
	}

	public void tick()
	{
		super.tick();

		if (!this.dead)
		{
			var halfAge = this.maxAge / 2f;
			if (this.age > halfAge)
				this.setColorAlpha(1 - (this.age - halfAge) / halfAge);

			this.setSprite(spriteProvider.getSprite(this.variant, NUM_VARIANTS));
			var a = MathHelper.lerp(heat, 1, MathHelper.clamp((this.age / (float)this.maxAge) * 2f, 0, 1));
			// this.setColor(MathHelper.clamp(Ease.outCubic(10 * a), 0, 1), MathHelper.clamp(5 * a, 0, 1), MathHelper.clamp(Ease.inCubic(4 * a), 0, 1));
			this.setColor(MathHelper.clamp(getRed(a), 0, 1), MathHelper.clamp(getGreen(a), 0, 1), MathHelper.clamp(getBlue(a), 0, 1));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<PParticle>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(PParticle defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ)
		{
			var heatEncodedNormal = new Vec3d(vX, vY, vZ);
			var heat = heatEncodedNormal.length();
			var normal = heatEncodedNormal.normalize();
			return new ScorchParticle(clientWorld, x, y, z, normal.x, normal.y, normal.z, (float)heat, this.spriteProvider);
		}
	}
}
