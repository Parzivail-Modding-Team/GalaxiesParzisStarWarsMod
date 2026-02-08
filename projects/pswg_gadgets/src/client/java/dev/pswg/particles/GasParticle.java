package dev.pswg.particles;

import dev.pswg.Gadgets;
import dev.pswg.GalaxiesRenderLayers;
import dev.pswg.entity.gas.GasEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public abstract class GasParticle extends BillboardParticle implements CustomRendererParticle
{
	private final int variant;
	final int NUM_VARIANTS = 5;
	final float originalScale;
	final int dirX;
	final int dirZ;
	final float billowing;
	final String particleId;
	final GasEntity gasEntity;

	protected GasParticle(GasEntity gasEntity, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider, String particleId)
	{
		super(clientWorld, x, y, z, spriteProvider.getFirst());

		this.originalScale = Random.create().nextBetween(150, 225) / 15f;
		scale(originalScale);
		setBoundingBoxSpacing(0f, 0f);
		this.setAlpha(0.1f);
		this.gasEntity = gasEntity;
		this.particleId = particleId;
		billowing = (float)random.nextBetween(1, 10) / 2500f;
		variant = random.nextInt(NUM_VARIANTS);
		dirX = random.nextBoolean() ? 1 : -1;
		dirZ = random.nextBoolean() ? 1 : -1;
		velocityX = 0;
		velocityZ = 0;
		if (gasEntity != null)
		{
			age = gasEntity.age;
			maxAge = gasEntity.MAX_AGE;
		}

	}

	@Override
	protected RenderType getRenderType()
	{
		return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
	}

	@Override
	public int getBrightness(float tint)
	{
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		int light = this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
		return Math.max(light, 80);
		//return light;
	}

	@Override
	public GadgetsParticleRenderer getParticleRenderer()
	{
		return GadgetsParticleRenderer.Gas;
	}

	public float getBillowing()
	{
		return billowing;
	}

	public float getAlpha()
	{
		return this.alpha;
	}

	public Sprite getSprite()
	{
		return this.sprite;
	}

	public Vec3d getPos()
	{
		return new Vec3d(this.x, this.y, this.z);
	}

	public int getColor()
	{
		return ColorHelper.getArgb((int)(this.red * 255), (int)(this.green * 255), (int)(this.blue * 255));
	}

	@Override
	protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		VertexConsumer v = mc.getBufferBuilders().getEffectVertexConsumers().getBuffer(GalaxiesRenderLayers.GALAXIES_TRANSLUCENT);

		Vec3d vec3d = camera.getPos();
		float f = (float)(MathHelper.lerp(tickProgress, this.lastX, this.x) - vec3d.getX());
		float g = (float)(MathHelper.lerp(tickProgress, this.lastY, this.y) - vec3d.getY());
		float h = (float)(MathHelper.lerp(tickProgress, this.lastZ, this.z) - vec3d.getZ());

		Vector3f[] corners = new Vector3f[] {
				new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float size = this.getSize(tickProgress);

		for (int j = 0; j < 4; ++j)
		{
			Vector3f vector3f = corners[j];
			vector3f.rotate(camera.getRotation().rotateZ((float)Math.toRadians(billowing)));
			vector3f.mul(size);
			vector3f.add(f, g, h);
		}

		float k = this.getMinU();
		float l = this.getMaxU();
		float m = this.getMinV();
		float n = this.getMaxV();
		int o = this.getBrightness(tickProgress);
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
		var pos = new BlockPos((int)x, (int)y, (int)z);
		if (gasEntity == null || !gasEntity.particleIdList.containsKey(pos) || !gasEntity.particleIdList.get(pos).contains(this.particleId))
		{
			markDead();
		}
		else
		{
			lastX = x;
			lastY = y;
			lastZ = z;
			age++;
			/*if (alpha < 0.1f)
			{
				markDead();
				return;
			}*/
			float m = maxAge / 100f;
			// max = 1000; m = 10; 10 * m = 100; age * age = 100 00
			if (age <= 10 * m)
			{
				alpha = (age * age) / (100 * m * m) * 0.3f;
			}
			if (age >= 90 * m)
			{

			}
			if (age >= 90 * m)
			{
				velocityX *= 0.95;
				velocityZ *= 0.95;
			}
			move(velocityX, velocityY, velocityZ);
		}
	}
}
