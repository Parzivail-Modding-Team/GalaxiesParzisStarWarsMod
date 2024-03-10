package com.parzivail.pswg.client.particle;

import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeParticle extends SpriteBillboardParticle
{
	private float scaleX = 1;
	private float scaleY = 1;

	protected FragmentationGrenadeParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		setBoundingBoxSpacing(0.25f, 0.25f);
		this.collidesWithWorld = false;
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
		this.maxAge = 20;
		this.scale = 1;
	}

	@Override
	protected int getBrightness(float tint)
	{
		return 255;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		updateShape(this.age + tickDelta);

		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternionf quaternionf;
		if (this.angle == 0.0F)
		{
			quaternionf = camera.getRotation();
		}
		else
		{
			quaternionf = new Quaternionf(camera.getRotation());
			quaternionf.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
		}

		Vector3f[] corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float size = this.getSize(tickDelta);

		for (int j = 0; j < 4; ++j)
		{
			Vector3f vector3f = corners[j];
			vector3f.mul(scaleX, scaleY, 1);
			vector3f.rotate(quaternionf);
			vector3f.mul(size);
			vector3f.add(f, g, h);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickDelta);
		vertexConsumer.vertex((double)corners[0].x(), (double)corners[0].y(), (double)corners[0].z())
		              .texture(l, n)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o)
		              .next();
		vertexConsumer.vertex((double)corners[1].x(), (double)corners[1].y(), (double)corners[1].z())
		              .texture(l, m)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o)
		              .next();
		vertexConsumer.vertex((double)corners[2].x(), (double)corners[2].y(), (double)corners[2].z())
		              .texture(k, m)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o)
		              .next();
		vertexConsumer.vertex((double)corners[3].x(), (double)corners[3].y(), (double)corners[3].z())
		              .texture(k, n)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o)
		              .next();
	}

	private void updateShape(float age)
	{
		if (age <= 10)
		{
			this.alpha = 1;

			this.scaleX = 2 * (1 + 2 * (float)Math.max(4 * Math.pow(age / 10 - 0.5f, 3), 0));
			this.scaleY = 2 * (1 - (float)Math.pow((age - 4) / 6, 2));
		}
		else
		{
			this.scaleX = this.scaleY = 1;
			this.scale = (float)Math.pow(age / 10, 6);
			this.alpha = (float)(Math.max(1 - Math.pow(age / 20, 3), 0.001f));
		}
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<PParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(PParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			FragmentationGrenadeParticle fragmentationGrenadeParticle = new FragmentationGrenadeParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			fragmentationGrenadeParticle.setSprite(spriteProvider);
			return fragmentationGrenadeParticle;
		}
	}
}
