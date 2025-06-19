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
	final int dirX;
	final int dirZ;
	final float billowing;
	final GasEntity gasEntity;

	protected GasParticle(GasEntity gasEntity, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(clientWorld, x, y, z);

		scale(Random.create().nextBetween(300, 400) / 100f);
		setBoundingBoxSpacing(0f, 0f);
		this.setAlpha(0.1f);
		this.gasEntity = gasEntity;
		shrinkSpeed = (float)random.nextBetween(10, 100) / 25000f;
		growthSpeed = (float)random.nextBetween(20, 40) / 5000f;
		billowing = (float)random.nextBetween(1, 10) / 2500f;
		variant = random.nextInt(NUM_VARIANTS);
		dirX = random.nextBoolean() ? 1 : -1;
		dirZ = random.nextBoolean() ? 1 : -1;
		velocityX = random.nextFloat() / 16f * dirX;
		velocityZ = random.nextFloat() / 16f * dirZ;
		age = 0;
		maxAge = gasEntity.MAX_AGE - gasEntity.age;
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

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		age++;
		float ageCoeficient = (float)gasEntity.MAX_AGE / maxAge;
		float inverseAgeCoeficient = (float)maxAge / gasEntity.MAX_AGE;

		//MinecraftClient.getInstance().player.sendMessage(Text.of("age: "+age + " alpha: "+ alpha), false);
		if (alpha < 0.1f)
		{
			//MinecraftClient.getInstance().player.sendMessage(Text.of(""+age), false);
			markDead();
			return;
		}
		if (age <= 250 * inverseAgeCoeficient)
		{
			if (alpha <= 0.2f)
				alpha += 0.0005f * ageCoeficient;
			if (scale <= 2)
				scale += growthSpeed * ageCoeficient;
		}
		if (age >= 400 * inverseAgeCoeficient)
		{
			alpha -= 0.000165f * ageCoeficient;
		}
		if (age >= 600 * inverseAgeCoeficient)
		{
			scale -= shrinkSpeed * ageCoeficient;
		}
		if (age == 700 * inverseAgeCoeficient)
		{
			velocityX = random.nextFloat() / 256f * dirX;
			velocityZ = random.nextFloat() / 256f * dirZ;
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
