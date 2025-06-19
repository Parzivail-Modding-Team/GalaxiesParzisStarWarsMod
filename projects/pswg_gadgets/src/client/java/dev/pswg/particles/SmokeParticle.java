package dev.pswg.particles;

import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.particle.GasParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class SmokeParticle extends GasParticle
{

	protected SmokeParticle(SmokeGasEntity gasEntity, ClientWorld clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider spriteProvider)
	{
		super(gasEntity, clientWorld, x, y, z, vX, vY, vZ, spriteProvider);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleFactory<GasParticleEffect>
	{
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(GasParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			SmokeGasEntity entity = (SmokeGasEntity)MinecraftClient.getInstance().world.getEntityById(parameters.getGasEntityId());
			SmokeParticle smokeParticle = new SmokeParticle(entity, world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
			smokeParticle.setSprite(spriteProvider);
			return smokeParticle;
		}
	}
}
