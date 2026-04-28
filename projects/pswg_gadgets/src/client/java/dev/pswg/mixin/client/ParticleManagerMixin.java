package dev.pswg.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.pswg.GadgetsClient;
import dev.pswg.particles.CustomRendererParticle;
import dev.pswg.particles.GadgetsParticleRenderer;
import dev.pswg.particles.renderers.FragmentationGrenadeWaveParticleGroup;
import dev.pswg.particles.renderers.GasParticleGroup;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin
{
	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Queue;poll()Ljava/lang/Object;"))
	public Object poll(Object particle)
	{
		if (particle instanceof CustomRendererParticle customParticle)
		{
			GadgetsClient.particleRenderers.computeIfAbsent(customParticle.getParticleRenderer(), this::createParticleRenderer).add((Particle)particle);
		}
		return particle;
	}

	private ParticleGroup<?> createParticleRenderer(GadgetsParticleRenderer particleRenderer)
	{
		return switch (particleRenderer)
		{
			case Gas -> new GasParticleGroup((ParticleEngine)(Object)this);
			case FragmentationGrenadeWave -> new FragmentationGrenadeWaveParticleGroup((ParticleEngine)(Object)this);
		};
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci)
	{
		for (ParticleGroup<?> particleRenderer : GadgetsClient.particleRenderers.values())
			particleRenderer.tickParticles();
	}

	@Inject(method = "extract", at = @At("HEAD"))
	public void addToBatch(ParticlesRenderState batch, Frustum frustum, Camera camera, float tickProgress, CallbackInfo ci)
	{
		for (ParticleGroup<?> particleRenderer : GadgetsClient.particleRenderers.values())
			if (!particleRenderer.isEmpty())
			{
				batch.add(particleRenderer.extractRenderState(frustum, camera, tickProgress));
			}
	}

	@Inject(method = "clearParticles", at = @At("HEAD"))
	public void clearParticles(CallbackInfo ci)
	{
		GadgetsClient.particleRenderers.clear();
	}
}
