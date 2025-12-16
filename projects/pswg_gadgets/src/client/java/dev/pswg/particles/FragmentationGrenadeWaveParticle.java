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
		/*updateShape(this.age + tickProgress);

		Vec3d vec3d = camera.getPos();
		Vector3f fgh = new Vector3f(lerp(lastX, x, tickProgress), lerp(lastY, y, tickProgress), lerp(lastZ, z, tickProgress));

		Quaternionf quaternionf = new Quaternionf();
		this.getRotator().setRotation(quaternionf, camera, tickProgress);
		if (this.zRotation != 0.0F) {
			quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastZRotation, this.zRotation));
		}
		float size = this.getSize(tickProgress);

		Quaternionf r180z = new Quaternionf(camera.getRotation().rotateZ((float)Math.toRadians(180d)));
		Vector3f[] corners = CORNERS.clone();
		Arrays.stream(corners).forEach( v -> rotateAndScale(v, r180z, size));

//		float k = this.getMinU();
//		float l = this.getMaxU();
//		float m = this.getMinV();
//		float n = this.getMaxV();
		int brightness = this.getBrightness(tickProgress);
		var color = ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue);
		//submittable.render(RenderType.PARTICLE_ATLAS_TRANSLUCENT, (float)this.x, (float)this.y, (float)this.z, rot.x, rot.y, rot.z, rot.w, size, getMinU(), getMinV(), getMaxU(), getMaxV(), color, o);

		try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(4 * VertexFormats.POSITION_TEXTURE_COLOR_LIGHT.getVertexSize())) {
			//MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers().getBuffer(this.getRenderType())

			BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

			Vector3f vector3f = new Vector3f(-1, -1, 0.0F).rotate(rotation).mul(size).add((float)x, (float)y, (float)z);
			bufferBuilder.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(k, n).color(color).light(o);

			vector3f = new Vector3f(-1, 1, 0.0F).rotate(rotation).mul(size).add((float)x, (float)y, (float)z);
			bufferBuilder.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(k, m).color(color).light(o);

			vector3f = new Vector3f(1, 1, 0.0F).rotate(rotation).mul(size).add((float)x, (float)y, (float)z);
			bufferBuilder.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, m).color(color).light(o);

			vector3f = new Vector3f(1, -1, 0.0F).rotate(rotation).mul(size).add((float)x, (float)y, (float)z);
			bufferBuilder.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, n).color(color).light(o);*/
			/*submittable.render(RenderType.PARTICLE_ATLAS_TRANSLUCENT, (float)this.x, (float)this.y, (float)this.z, rot.x, rot.y, rot.z, rot.w, size, getMinU(), getMinV(), getMaxU(), getMaxV(), color, o);

			Vector3f corner = corners[0];
			bufferBuilder.vertex(corner.x(), corner.y(), corner.z())
			             .color(this.red, this.green, this.blue, this.alpha)
			             .light(o)
			             .texture(l, n);
			bufferBuilder.vertex(corners[1].x(), corners[1].y(), corners[1].z())
			              .texture(l, m)
			              .color(this.red, this.green, this.blue, this.alpha)
			              .light(o);
			bufferBuilder.vertex(corners[2].x(), corners[2].y(), corners[2].z())
			              .texture(k, m)
			              .color(this.red, this.green, this.blue, this.alpha)
			              .light(o);
			bufferBuilder.vertex(corners[3].x(), corners[3].y(), corners[3].z())
			              .texture(k, n)
			              .color(this.red, this.green, this.blue, this.alpha)
			              .light(o);

			BuiltBuffer builtBuffer = bufferBuilder.endNullable();
			var cache =  new LayeredCustomCommandRenderer.VerticesCache();
			GpuDevice gpuDevice = RenderSystem.getDevice();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			TextureManager textureManager = minecraftClient.getTextureManager();
			cache.write(builtBuffer.getBuffer());
			var buffers = submittable.submit(cache);


			if(minecraftClient.worldRenderer != null && buffers != null)
			{
				Framebuffer framebuffer = minecraftClient.getFramebuffer();
				//Framebuffer particleFramebuffer = minecraftClient.worldRenderer.getParticlesFramebuffer();

				if (framebuffer != null)
				{
					try (RenderPass renderPass = gpuDevice.createCommandEncoder()
					                                      .createRenderPass(
							                                      () -> "Particles - Main",
							                                      framebuffer.getColorAttachmentView(),
							                                      OptionalInt.empty(),
							                                      framebuffer.getDepthAttachmentView(),
							                                      OptionalDouble.empty()
					                                      ))
					{
						renderPass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
						renderPass.setUniform("Fog", RenderSystem.getShaderFog());
						renderPass.bindSampler("Sampler2", MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().getGlTextureView());

						submittable.render(buffers, cache, renderPass, textureManager, true);
					}
				}
			}

			//submittable.render(buffers, cache, null, textureManager, true);

			//submittable.submit(cache);

			//BuiltBuffer builtBuffer = bufferBuilder.endNullable();

			//bufferBuilder.vertex()
			//((BillboardParticleSubmittableInvoker)submittable).pswg_gadgets$invokeRenderVertex(bufferBuilder, rot, (float)this.x, (float)this.y, (float)this.z, this.scaleX, this.scaleY, this.scale, getMaxU(), getMaxV(), color, o);
			//((BillboardParticleSubmittableInvoker)submittable).pswg_gadgets$invokeRenderVertex(bufferBuilder, rot, (float)this.x, (float)this.y, (float)this.z, this.scaleX, -this.scaleY, this.scale, getMaxU(), getMaxV(), color, o);
			//((BillboardParticleSubmittableInvoker)submittable).pswg_gadgets$invokeRenderVertex(bufferBuilder, rot, (float)this.x, (float)this.y, (float)this.z, -this.scaleX, this.scaleY, this.scale, getMaxU(), getMaxV(), color, o);
			//((BillboardParticleSubmittableInvoker)submittable).pswg_gadgets$invokeRenderVertex(bufferBuilder, rot, (float)this.x, (float)this.y, (float)this.z, -this.scaleX, -this.scaleY, this.scale, getMaxU(), getMaxV(), color, o);

		}
		// TODO: MAKE SURE THIS WORKS
		//var v = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers().getBuffer(RenderLayer.
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
