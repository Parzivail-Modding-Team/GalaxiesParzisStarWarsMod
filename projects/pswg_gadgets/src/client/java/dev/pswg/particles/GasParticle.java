package dev.pswg.particles;

import dev.pswg.PswgGadgetsRenderLayers;
import dev.pswg.entity.gas.GasEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

@Environment(value = EnvType.CLIENT)
public abstract class GasParticle extends SpriteBillboardParticle
{
	private final int variant;
	final int NUM_VARIANTS = 5;
	final float shrinkSpeed;
	final float growthSpeed;
	final float originalScale;
	final float maxScale;
	float alphaScaling;
	final int dirX;
	final int dirZ;
	final float billowing;
	final float minConcentration;
	final GasEntity gasEntity;

	protected GasParticle(GasEntity gasEntity, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider, float minConcentration)
	{
		super(clientWorld, x, y, z);

		this.originalScale = Random.create().nextBetween(50, 75) / 100f;
		scale(originalScale);
		setBoundingBoxSpacing(0f, 0f);
		this.setAlpha(0.1f);
		this.gasEntity = gasEntity;
		this.minConcentration = minConcentration;
		shrinkSpeed = (float)random.nextBetween(10, 100) / 25000f;
		growthSpeed = (float)random.nextBetween(20, 40) * 0.000005f;
		billowing = (float)random.nextBetween(1, 10) / 2500f;
		variant = random.nextInt(NUM_VARIANTS);
		dirX = random.nextBoolean() ? 1 : -1;
		dirZ = random.nextBoolean() ? 1 : -1;
		velocityX = 0;
		velocityZ = 0;
		age = 0;
		maxAge = gasEntity != null ? gasEntity.MAX_AGE - gasEntity.age : 1000;
		maxScale = growthSpeed * 250 + originalScale;
		alphaScaling = gasEntity != null ? gasEntity.blockConcentration.getOrDefault(new BlockPos((int)x, (int)y, (int)z), 1f) : 1;


	}

	@Override
	protected int getBrightness(float tint)
	{
		BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
		int light = this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
		return Math.max(light, 80);
		//return light;
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
			vector3f.rotate(camera.getRotation().rotateZ((float)Math.toRadians(billowing)));
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
		float blockConcentration = gasEntity != null ? gasEntity.blockConcentration.getOrDefault(new BlockPos((int)x, (int)y, (int)z), 1f) : 1;
		;
		alphaScaling = blockConcentration;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		float ageCoeficient = gasEntity != null ? (float)gasEntity.MAX_AGE / maxAge : 1;
		float inverseAgeCoeficient = gasEntity != null ? (float)maxAge / gasEntity.MAX_AGE : 1;
		if (alpha < 0.1f || minConcentration > blockConcentration)
		{
			markDead();
			return;
		}
		if (age <= 250 * inverseAgeCoeficient)
		{
			alpha = age / (250 * inverseAgeCoeficient) * 0.25f * alphaScaling + 0.1f;
			scale = growthSpeed * ageCoeficient * age + originalScale;
		}

		if (age >= 400 * inverseAgeCoeficient)
		{
			alpha = (0.1f + 0.25f * alphaScaling) - (0.25f * alphaScaling * ((age - (400 * inverseAgeCoeficient)) / (600 * inverseAgeCoeficient)));
		}
		if (age >= 600 * inverseAgeCoeficient)
		{
			scale = maxScale - (shrinkSpeed * ageCoeficient * (age - 600 * inverseAgeCoeficient));
		}
		if (age <= 500 * inverseAgeCoeficient)
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
}
