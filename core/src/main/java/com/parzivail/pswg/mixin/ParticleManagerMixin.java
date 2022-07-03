package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.particle.ScorchParticle;
import com.parzivail.pswg.client.particle.SlugTrailParticle;
import com.parzivail.pswg.client.particle.SparkParticle;
import com.parzivail.pswg.container.SwgParticles;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin
{
	@Shadow
	protected abstract <T extends ParticleEffect>  void registerFactory(ParticleType<T> type, ParticleManager.SpriteAwareFactory<T> factory);

	@Inject(method = "registerDefaultFactories()V", at = @At("TAIL"))
	private void registerDefaultFactories(CallbackInfo ci)
	{
		registerFactory(SwgParticles.SLUG_TRAIL, SlugTrailParticle.Factory::new);
		registerFactory(SwgParticles.SPARK, SparkParticle.Factory::new);
		registerFactory(SwgParticles.SCORCH, ScorchParticle.Factory::new);
	}
}
