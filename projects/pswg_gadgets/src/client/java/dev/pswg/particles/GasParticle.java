package dev.pswg.particles;

import dev.pswg.entity.gas.GasEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

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

		this.originalScale = Random.create().nextBetween(75, 125) / 15f;
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
			float m = maxAge / 100f;
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
