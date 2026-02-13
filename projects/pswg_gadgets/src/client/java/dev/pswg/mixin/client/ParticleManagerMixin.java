package dev.pswg.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.pswg.GadgetsClient;
import dev.pswg.particles.CustomRendererParticle;
import dev.pswg.particles.GadgetsParticleRenderer;
import dev.pswg.particles.renderers.FragmentationGrenadeWaveParticleRenderer;
import dev.pswg.particles.renderers.GasParticleRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.SubmittableBatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
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

	private ParticleRenderer<?> createParticleRenderer(GadgetsParticleRenderer particleRenderer)
	{
		return switch (particleRenderer)
		{
			case Gas -> new GasParticleRenderer((ParticleManager)(Object)this);
			case FragmentationGrenadeWave -> new FragmentationGrenadeWaveParticleRenderer((ParticleManager)(Object)this);
		};
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci)
	{
		for (ParticleRenderer<?> particleRenderer : GadgetsClient.particleRenderers.values())
			particleRenderer.tick();
	}

	@Inject(method = "addToBatch", at = @At("HEAD"))
	public void addToBatch(SubmittableBatch batch, Frustum frustum, Camera camera, float tickProgress, CallbackInfo ci)
	{
		for (ParticleRenderer<?> particleRenderer : GadgetsClient.particleRenderers.values())
			if (!particleRenderer.isEmpty())
			{
				batch.add(particleRenderer.render(frustum, camera, tickProgress));
			}
	}

	@Inject(method = "clearParticles", at = @At("HEAD"))
	public void clearParticles(CallbackInfo ci)
	{
		GadgetsClient.particleRenderers.clear();
	}
}
