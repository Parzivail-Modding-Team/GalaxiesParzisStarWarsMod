package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeWaveParticle extends BillboardParticle implements CustomRendererParticle
{
	private float scaleX = 1;
	private float scaleY = 1;

	private static final Vector3f[] CORNERS = new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
	};

	protected FragmentationGrenadeWaveParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.getFirst());
		setBoundingBoxSpacing(0.25f, 0.25f);
		this.collidesWithWorld = false;
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
		this.maxAge = 25;
		this.scale = Random.create().nextBetween(0, 5) / 5f + 0.95f;
		this.updateSprite(spriteProvider);
		this.setColor(Random.create().nextBetween(0, 3) / 3f + 0.97f, Random.create().nextBetween(0, 3) / 3f + 0.98f, 1);
	}

	public Vec3d getPos()
	{
		return new Vec3d(this.x, this.y, this.z);
	}

	public float getScaleX()
	{
		return scaleX;
	}

	public float getScaleY()
	{
		return scaleY;
	}

	public float getAlpha()
	{
		return this.alpha;
	}

	public Sprite getSprite()
	{
		return this.sprite;
	}


	private void updateShape(float age)
	{
		if (age <= 15)
		{
			this.alpha = 1;

			this.scaleX = 2 * (1 + 2 * (float)Math.max(4 * Math.pow(age / 10 - 0.5f, 3), 0));
			this.scaleY = 2 * (1 - (float)Math.pow((age - 4) / 6, 2));
		}
		else
		{
			this.alpha = (float)(Math.max(1 - (Math.pow(age / 25, 3) * 2.5f), 0.001f));

			this.scaleX = 1;
			this.scaleY = 1;
			this.scale = (float)Math.pow(age / 15, 6);
		}
	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	float lerp(double last, double now, float progress)
	{
		return (float)(MathHelper.lerp(progress, last, now));
	}
	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		updateShape(this.age + tickProgress);
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	private void rotateAndScale(Vector3f vector3f, Quaternionf r180z, float size)
	{
		vector3f.mul(scaleX, scaleY, 1);
		vector3f.rotate(r180z);
		vector3f.mul(size);
		//vector3f.add(f, g, h);
	}

	@Override
	public GadgetsParticleRenderer getParticleRenderer()
	{
		return GadgetsParticleRenderer.FragmentationGrenadeWave;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random)
		{
			return new FragmentationGrenadeWaveParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
		}
	}
}
