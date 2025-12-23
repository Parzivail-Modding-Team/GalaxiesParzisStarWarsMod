package dev.pswg.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.pswg.Gadgets;
import dev.pswg.GadgetsClient;
import dev.pswg.particles.FragmentationGrenadeWaveParticle;
import dev.pswg.particles.FragmentationGrenadeWaveParticleRenderer;
import net.minecraft.client.particle.ParticleManager;
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
		if (particle instanceof FragmentationGrenadeWaveParticle fragParticle)
		{
			if (GadgetsClient.pswgParticleRenderer == null)
				GadgetsClient.pswgParticleRenderer = new FragmentationGrenadeWaveParticleRenderer((ParticleManager)(Object)this);
			GadgetsClient.pswgParticleRenderer.add(fragParticle);
		}
		return particle;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci)
	{
		if (!(GadgetsClient.pswgParticleRenderer == null))
			GadgetsClient.pswgParticleRenderer.tick();
	}

	@Inject(method = "addToBatch", at = @At("HEAD"))
	public void addToBatch(SubmittableBatch batch, Frustum frustum, Camera camera, float tickProgress, CallbackInfo ci)
	{
		if (GadgetsClient.pswgParticleRenderer != null && !GadgetsClient.pswgParticleRenderer.isEmpty())
		{
			batch.add(GadgetsClient.pswgParticleRenderer.render(frustum, camera, tickProgress));
		}
	}

	@Inject(method = "clearParticles", at = @At("HEAD"))
	public void clearParticles(CallbackInfo ci)
	{
		GadgetsClient.pswgParticleRenderer = null;
	}
}
