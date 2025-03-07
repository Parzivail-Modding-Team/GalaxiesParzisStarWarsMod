package dev.pswg.particles;

import dev.pswg.PswgGadgetsRenderLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public class NerveGasParticle extends SpriteBillboardParticle
{
	private final int variant;
	final int NUM_VARIANTS = 5;
	final float shrinkSpeed;
	final float growthSpeed;
	final int dirX;
	final int dirZ;
	final float billowing;

	protected NerveGasParticle(ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);
		scale(8f);

		setBoundingBoxSpacing(0f, 0f);
		this.setAlpha(0.05f);
		shrinkSpeed = (float)random.nextBetween(1, 10) / 2000f;
		growthSpeed = (float)random.nextBetween(1, 10) / 100f;
		billowing = (float)random.nextBetween(1, 10) / 25000f;
		variant = random.nextInt(NUM_VARIANTS);
		dirX = random.nextBoolean() ? 1 : -1;
		dirZ = random.nextBoolean() ? 1 : -1;
		velocityX = random.nextFloat() / 8f * dirX;
		velocityZ = random.nextFloat() / 8f * dirZ;
		age = 0;
		this.setColor(1f, 0.9f, 0.6f);
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		var texture = this.sprite.getAtlasId();
		var mc = MinecraftClient.getInstance();
		var v = mc.getBufferBuilders().getEffectVertexConsumers().getBuffer(PswgGadgetsRenderLayers.pswgParticle(texture, true));

		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

		Vector3f[] corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float size = this.getSize(tickDelta);

		for (int j = 0; j < 4; ++j)
		{
			Vector3f vector3f = corners[j];
			vector3f.rotate(camera.getRotation().rotateZ((float)Math.toRadians((float)age * billowing)));
			vector3f.mul(size);
			vector3f.add(f, g, h);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickDelta);
		v.vertex(corners[0].x(), corners[0].y(), corners[0].z())
		 .texture(l, n)
		 .color(this.red, this.green, this.blue, this.alpha)
		 .light(o);
		v.vertex(corners[1].x(), corners[1].y(), corners[1].z())
		 .texture(l, m)
		 .color(this.red, this.green, this.blue, this.alpha)
		 .light(o);
		v.vertex(corners[2].x(), corners[2].y(), corners[2].z())
		 .texture(k, m)
		 .color(this.red, this.green, this.blue, this.alpha)
		 .light(o);
		v.vertex(corners[3].x(), corners[3].y(), corners[3].z())
		 .texture(k, n)
		 .color(this.red, this.green, this.blue, this.alpha)
		 .light(o);
	}

	@Override
	public void tick()
	{

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		if (alpha <= 0.0f)
		{
			markDead();
			return;
		}
		if (age <= 100)
		{
			if (alpha <= 0.9f)
				alpha += 0.025f;
			if (scale <= 2)
				scale += growthSpeed;
		}
		if (age >= 300)
		{
			alpha -= 0.00125f;
		}
		if (age >= 550)
		{
			scale -= shrinkSpeed;

		}
		if (age == 600)
		{
			velocityX = random.nextFloat() / 128f * dirX;
			velocityZ = random.nextFloat() / 128f * dirZ;
		}
		if (age <= 500)
		{
			velocityX *= 0.95;
			velocityZ *= 0.95;
		}
		move(velocityX, velocityY, velocityZ);
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			NerveGasParticle nerveGasParticle = new NerveGasParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			nerveGasParticle.setSprite(spriteProvider);
			return nerveGasParticle;
		}
	}
}
