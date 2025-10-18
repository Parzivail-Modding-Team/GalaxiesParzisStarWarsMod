package dev.pswg.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeWaveParticle extends BillboardParticle
{
	private float scaleX = 1;
	private float scaleY = 1;

	protected FragmentationGrenadeWaveParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z, spriteProvider.getFirst());
		setBoundingBoxSpacing(0.25f, 0.25f);
		this.collidesWithWorld = false;
		velocityX = vX;
		velocityY = vY + (double)(random.nextFloat() / 500.0f);
		velocityZ = vZ;
		this.maxAge = 20;
		this.scale = Random.create().nextBetween(0, 5) / 5f + 0.95f;
		this.setSprite(spriteProvider.getFirst());
		this.setColor(Random.create().nextBetween(0, 3) / 3f + 0.97f, Random.create().nextBetween(0, 3) / 3f + 0.98f, 1);
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
			this.alpha = (float)(Math.max(1 - (Math.pow(age / 20, 3) * 2), 0.001f));
		}
	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		updateShape(this.age + tickProgress);

		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp(tickProgress, this.lastX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp(tickProgress, this.lastY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp(tickProgress, this.lastZ, this.z) - vec3d.getZ());

		Quaternionf quaternionf = new Quaternionf();
		this.getRotator().setRotation(quaternionf, camera, tickProgress);
		if (this.zRotation != 0.0F)
		{
			quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastZRotation, this.zRotation));
		}

		Vector3f[] corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float size = this.getSize(tickProgress);

		for (int j = 0; j < 4; ++j)
		{
			Vector3f vector3f = corners[j];
			vector3f.mul(scaleX, scaleY, 1);
			vector3f.rotate(new Quaternionf(camera.getRotation().rotateZ((float)Math.toRadians(180d))));
			vector3f.mul(size);
			vector3f.add(f, g, h);
		}
		var rot = new Quaternionf(camera.getRotation().rotateZ((float)Math.toRadians(180d)));

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickProgress);
		var color = ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue);
		submittable.render(RenderType.PARTICLE_ATLAS_TRANSLUCENT, (float)this.x, (float)this.y, (float)this.z, rot.x, rot.y, rot.z, rot.w, size, getMinU(), getMinV(), getMaxU(), getMaxV(), color, o);
		// TODO: MAKE SURE THIS WORKS
		/*vertexConsumer.vertex(corners[0].x(), corners[0].y(), corners[0].z())
		              .texture(l, n)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o);
		vertexConsumer.vertex(corners[1].x(), corners[1].y(), corners[1].z())
		              .texture(l, m)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o);
		vertexConsumer.vertex(corners[2].x(), corners[2].y(), corners[2].z())
		              .texture(k, m)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o);
		vertexConsumer.vertex(corners[3].x(), corners[3].y(), corners[3].z())
		              .texture(k, n)
		              .color(this.red, this.green, this.blue, this.alpha)
		              .light(o);
		//super.render(submittable, camera, rotation, tickProgress);*/
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
			FragmentationGrenadeWaveParticle fragmentationGrenadeParticle = new FragmentationGrenadeWaveParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			fragmentationGrenadeParticle.setSprite(spriteProvider.getFirst());
			return fragmentationGrenadeParticle;
		}
	}
}
