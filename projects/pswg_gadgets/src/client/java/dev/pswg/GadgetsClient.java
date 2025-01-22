package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.particles.*;
import dev.pswg.renderer.FragmentationGrenadeEntityRenderer;
import dev.pswg.renderer.NerveGasGrenadeEntityRenderer;
import dev.pswg.renderer.ThermalDetonatorEntityRenderer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

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
		EntityRendererRegistry.register(Gadgets.NERVE_GAS_GRENADE_ENTITY, NerveGasGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(NerveGasGrenadeEntityRenderer.MODEL_LAYER, NerveGasGrenadeEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(Gadgets.NERVE_GAS, EmptyEntityRenderer::new);

		ParticleFactoryRegistry.getInstance().register(Gadgets.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.FRAGMENTATION_GRENADE_SPARK_PARTICLE, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.FRAGMENTATION_GRENADE_WAVE_PARTICLE, FragmentationGrenadeWaveParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.SMOKE_PARTICLE, SmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(Gadgets.NERVE_GAS_PARTICLE, NerveGasParticle.Factory::new);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
