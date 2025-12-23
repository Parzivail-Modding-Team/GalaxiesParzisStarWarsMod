package dev.pswg.particles;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@Environment(value = EnvType.CLIENT)
public class FragmentationGrenadeWaveParticle extends BillboardParticle
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
		this.maxAge = 20;
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

	public float getScale()
	{
		return getSize(0);
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
