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
		public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			BlockState blockState = blockStateParticleEffect.getBlockState();
			return !blockState.isAir() ? new WakeParticle(clientWorld, d, e, f, g, h, i, blockState) : null;
		}
	}

	public WakeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state)
	{
		super(world, x, y, z, velocityX, velocityY, velocityZ, state);
		this.gravityStrength = 0.02f;
		this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * velocityY * 0.1f;
		this.velocityX = (Math.random() * 2.0 - 1.0) * velocityX;
		this.velocityZ = (Math.random() * 2.0 - 1.0) * velocityZ;
	}

	@Override
	public void tick()
	{
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;

		if (this.maxAge-- <= 0)
			this.markDead();
		else
		{
			this.velocityY -= this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);

			this.velocityX *= 0.98;
			this.velocityY *= 0.98;
			this.velocityZ *= 0.98;

			if (this.onGround)
				this.markDead();
		}
	}
}
