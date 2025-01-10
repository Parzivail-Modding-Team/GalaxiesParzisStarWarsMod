package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.particles.FragmentationGrenadeSparkParticle;
import dev.pswg.particles.ExplosionSmokeParticle;
import dev.pswg.particles.FragmentationGrenadeWaveParticle;
import dev.pswg.renderer.FragmentationGrenadeEntityRenderer;
import dev.pswg.renderer.ThermalDetonatorEntityRenderer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		EntityRendererRegistry.register(Gadgets.THERMAL_DETONATOR_ENTITY, ThermalDetonatorEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ThermalDetonatorEntityRenderer.MODEL_LAYER, ThermalDetonatorEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(Gadgets.FRAGMENTATION_GRENADE_ENTITY, FragmentationGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(FragmentationGrenadeEntityRenderer.MODEL_LAYER, FragmentationGrenadeEntityRenderer.Model::getTexturedModelData);

		ParticleFactoryRegistry.getInstance().register(Gadgets.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.FRAGMENTATION_GRENADE_SPARK, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.FRAGMENTATION_GRENADE_WAVE, FragmentationGrenadeWaveParticle.Factory::new);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
