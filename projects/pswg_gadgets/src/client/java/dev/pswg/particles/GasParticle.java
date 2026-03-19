package dev.pswg.particles;

import dev.pswg.entity.gas.GasEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

@Environment(value = EnvType.CLIENT)
public abstract class GasParticle extends SingleQuadParticle implements CustomRendererParticle
{
	private final int variant;
	final int NUM_VARIANTS = 5;
	final float originalScale;
	final int dirX;
	final int dirZ;
	final float billowing;
	final String particleId;
	final GasEntity gasEntity;

	protected GasParticle(GasEntity gasEntity, ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider, String particleId)
	{
		super(clientWorld, x, y, z, spriteProvider.first());

		this.originalScale = RandomSource.create().nextIntBetweenInclusive(75, 125) / 15f;
		scale(originalScale);
		setSize(0f, 0f);
		this.setAlpha(0.1f);
		this.gasEntity = gasEntity;
		this.particleId = particleId;
		billowing = (float)random.nextIntBetweenInclusive(1, 10) / 2500f;
		variant = random.nextInt(NUM_VARIANTS);
		dirX = random.nextBoolean() ? 1 : -1;
		dirZ = random.nextBoolean() ? 1 : -1;
		xd = 0;
		zd = 0;
		if (gasEntity != null)
		{
			age = gasEntity.tickCount;
			lifetime = gasEntity.MAX_AGE * 2;
		}

	}

	@Override
	protected Layer getLayer()
	{
		return Layer.TRANSLUCENT;
	}

	@Override
	public int getLightCoords(float tint)
	{
		BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
		int light = this.level.hasChunkAt(blockPos) ? LevelRenderer.getLightCoords(this.level, blockPos) : 0;
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

	public TextureAtlasSprite getSprite()
	{
		return this.sprite;
	}

	public Vec3 getPos()
	{
		return new Vec3(this.x, this.y, this.z);
	}

	public int getColor()
	{
		return ARGB.color((int)(this.rCol * 255), (int)(this.gCol * 255), (int)(this.bCol * 255));
	}

	@Override
	public void tick()
	{
		var pos = BlockPos.containing(x, y, z);
		if (gasEntity == null || !gasEntity.particleIdList.containsKey(pos) || !gasEntity.particleIdList.get(pos).contains(this.particleId) || age >= lifetime)
		{
			remove();
		}
		else
		{
			xo = x;
			yo = y;
			zo = z;
			age++;
			float m = lifetime / 100f;
			if (age <= 10 * m)
			{
				alpha = (float)(age * age) / (lifetime * lifetime) * 0.3f * 100;
			}
			if (age >= 90 * m)
			{
				alpha = (1 - (float)(age) / (lifetime)) * 0.3f * 10;
				xd *= 0.95;
				zd *= 0.95;
			}
			move(xd, yd, zd);
		}
	}
}
