package com.parzivail.pswg.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;

public class WakeParticle extends BlockDustParticle
{
	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<BlockStateParticleEffect>
	{
		@Override
		public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			BlockState blockState = blockStateParticleEffect.getBlockState();
			return !blockState.isAir() ? new WakeParticle(clientWorld, d, e, f, g, h, i, blockState) : null;
		}
	}

	private final float maxScale;

	public WakeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state)
	{
		super(world, x, y, z, velocityX, velocityY, velocityZ, state);
		this.gravityStrength = 0.02f;
		this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * velocityY * 0.1f;
		this.velocityX = (Math.random() * 2.0 - 1.0) * velocityX;
		this.velocityZ = (Math.random() * 2.0 - 1.0) * velocityZ;

		this.maxScale = 0.04F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
	}

	@Override
	public void tick()
	{
		this.scale = this.maxScale * (float)Math.min(Math.pow(20f * this.age / this.maxAge, 4), 1);

		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;

		if (this.age++ >= maxAge)
			this.markDead();
		else
		{
			this.velocityY -= this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);

			this.velocityX *= 0.98;
			this.velocityY *= 0.98;
			this.velocityZ *= 0.98;

			if (this.onGround)
			{
				this.velocityX *= 0.7;
				this.velocityZ *= 0.7;
			}
		}
	}
}
