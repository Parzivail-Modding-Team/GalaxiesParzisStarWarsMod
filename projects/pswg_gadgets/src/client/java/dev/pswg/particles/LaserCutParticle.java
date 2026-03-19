package dev.pswg.particles;

import dev.pswg.particle.DecalParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LaserCutParticle extends DecalParticle
{
	private static final int NUM_VARIANTS = 1;
	private final int variant;
	private final float heat;
	private final SpriteSet _spriteProvider;

	protected LaserCutParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, float heat, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider);
		this._spriteProvider = spriteProvider;

		this.friction = 1;
		this.scale(1 / 32f);
		this.setAlpha(1F);
		var a = Mth.lerp(Mth.clamp((this.age / (float)this.lifetime) * 2f, 0, 1), heat, 1.0f);
		this.setColor(Mth.clamp(getRed(a), 0, 1), Mth.clamp(getGreen(a), 0, 1), Mth.clamp(getBlue(a), 0, 1));
		this.lifetime = clientWorld.random.nextIntBetweenInclusive(350, 400);
		this.hasPhysics = false;
		this.xd = vX;
		this.yd = vY;
		this.zd = vZ;
		this.heat = heat;
		this.variant = this.random.nextInt(NUM_VARIANTS);
		this.setSprite(this._spriteProvider.get(this.variant, NUM_VARIANTS));
	}

	@Override
	public ParticleRenderType getGroup()
	{
		return ParticleRenderType.SINGLE_QUADS;
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

	@Override
	public void tick()
	{
		super.tick();

		if (!this.removed)
		{
			var halfAge = this.lifetime / 2f;
			if (this.age > halfAge)
				this.setAlpha(1 - (this.age - halfAge) / halfAge);

			this.setSprite(this._spriteProvider.get(this.variant, NUM_VARIANTS));
			var a = Mth.lerp(Mth.clamp((this.age / (float)this.lifetime) * 2f, 0, 1), heat, 1.0f);
			// this.setColor(MathHelper.clamp(Ease.outCubic(10 * a), 0, 1), MathHelper.clamp(5 * a, 0, 1), MathHelper.clamp(Ease.inCubic(4 * a), 0, 1));
			this.setColor(Mth.clamp(getRed(a), 0, 1), Mth.clamp(getGreen(a), 0, 1), Mth.clamp(getBlue(a), 0, 1));
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
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			var heatEncodedNormal = new Vec3(velocityX, velocityY, velocityZ);
			var heat = heatEncodedNormal.length();
			var normal = heatEncodedNormal.normalize();
			return new LaserCutParticle(world, x, y, z, normal.x, normal.y, normal.z, (float)heat, this.spriteProvider);
		}
	}
}
