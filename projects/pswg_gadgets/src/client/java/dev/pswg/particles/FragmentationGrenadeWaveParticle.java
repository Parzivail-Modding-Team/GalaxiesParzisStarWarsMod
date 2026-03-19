package dev.pswg.particles;

import dev.pswg.Gadgets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeWaveParticle extends SingleQuadParticle implements CustomRendererParticle
{
	private float scaleX = 1;
	private float scaleY = 1;

	private static final Vector3f[] CORNERS = new Vector3f[] {
			new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
	};

	protected FragmentationGrenadeWaveParticle(ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.first());
		setSize(0.25f, 0.25f);
		this.hasPhysics = false;
		xd = vX;
		yd = vY + (double)(random.nextFloat() / 500.0f);
		zd = vZ;
		this.lifetime = 25;
		this.quadSize = RandomSource.create().nextIntBetweenInclusive(0, 5) / 5f + 0.95f;
		this.setSpriteFromAge(spriteProvider);
		this.setColor(RandomSource.create().nextIntBetweenInclusive(0, 3) / 3f + 0.97f, RandomSource.create().nextIntBetweenInclusive(0, 3) / 3f + 0.98f, 1);
	}

	public Vec3 getPos()
	{
		return new Vec3(this.x, this.y, this.z);
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

	public TextureAtlasSprite getSprite()
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

			this.alpha = (float)(Math.max(1 - (Math.pow(age / 25, 3) * 1.5f), 0.001f));

			this.scaleX = 1;
			this.scaleY = 1;
			this.quadSize = (float)Math.pow((age) / 15, 6);
		}
	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}

	float lerp(double last, double now, float progress)
	{
		return (float)(Mth.lerp(progress, last, now));
	}
	@Override
	protected void extractRotatedQuad(QuadParticleRenderState submittable, Camera camera, Quaternionf rotation, float tickProgress)
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
			return new FragmentationGrenadeWaveParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
		}
	}
}
