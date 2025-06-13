package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.particles.*;
import dev.pswg.renderer.FragmentationGrenadeEntityRenderer;
import dev.pswg.renderer.NerveGasGrenadeEntityRenderer;
import dev.pswg.renderer.SmokeGasGrenadeEntityRenderer;
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
		EntityRendererRegistry.register(GadgetsEntities.THERMAL_DETONATOR_ENTITY, ThermalDetonatorEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ThermalDetonatorEntityRenderer.MODEL_LAYER, ThermalDetonatorEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY, FragmentationGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(FragmentationGrenadeEntityRenderer.MODEL_LAYER, FragmentationGrenadeEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS_GRENADE_ENTITY, NerveGasGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(NerveGasGrenadeEntityRenderer.MODEL_LAYER, NerveGasGrenadeEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_GAS_GRENADE_ENTITY, SmokeGasGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(SmokeGasGrenadeEntityRenderer.MODEL_LAYER, SmokeGasGrenadeEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS, EmptyEntityRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_GAS, EmptyEntityRenderer::new);

		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, FragmentationGrenadeWaveParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SMOKE_PARTICLE, SmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.NERVE_GAS_PARTICLE, NerveGasParticle.Factory::new);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
