package dev.pswg.particles;

import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.particle.GasParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Environment(value = EnvType.CLIENT)
public class NerveGasParticle extends GasParticle
{

	protected NerveGasParticle(NerveGasEntity gasEntity, ClientLevel clientWorld, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteProvider, String particleId)
	{
		super(gasEntity, clientWorld, x, y, z, vX, vY, vZ, spriteProvider, particleId);
		setColor(1, 0.9f, 0.6f);
	}

	@Environment(value = EnvType.CLIENT)
	public static class Factory implements ParticleProvider<GasParticleEffect>
	{
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(GasParticleEffect parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, RandomSource random)
		{
			NerveGasEntity entity = (NerveGasEntity)Minecraft.getInstance().level.getEntity(UUID.fromString(parameters.getGasEntityId()));
			NerveGasParticle gasParticle = new NerveGasParticle(entity, world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, parameters.particleId);
			gasParticle.setSprite(spriteProvider.first());
			return gasParticle;
		}
	}
}
