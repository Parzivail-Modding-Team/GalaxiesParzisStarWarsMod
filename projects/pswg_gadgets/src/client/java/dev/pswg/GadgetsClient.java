package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.feature.scrapping.ScrappingTableScreenHandler;
import dev.pswg.models.*;
import dev.pswg.particles.*;
import dev.pswg.renderer.grenades.*;
import dev.pswg.renderer.mines.PressureMineEntityRenderer;
import dev.pswg.renderer.mines.TripwireMineEntityRenderer;
import dev.pswg.screens.CrateGenericSmallScreen;
import dev.pswg.screens.ScrappingTableScreen;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		EntityRendererRegistry.register(GadgetsEntities.THERMAL_DETONATOR_ENTITY, ThermalDetonatorEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ThermalDetonatorEntityRenderer.MODEL_LAYER, ThermalDetonatorGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY, FragmentationGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(FragmentationGrenadeEntityRenderer.MODEL_LAYER, FragmentationGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS_GRENADE_ENTITY, NerveGasGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(NerveGasGrenadeEntityRenderer.MODEL_LAYER, NerveGasGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_SIGNAL_GRENADE_ENTITY, SmokeSignalGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(SmokeSignalGrenadeEntityRenderer.MODEL_LAYER, SmokeSignalGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.IMPACT_GRENADE_ENTITY, ImpactGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ImpactGrenadeEntityRenderer.MODEL_LAYER, ImpactGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.INFERNO_GRENADE_ENTITY, InfernoGrenadeEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(InfernoGrenadeEntityRenderer.MODEL_LAYER, InfernoGrenadeModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.PRESSURE_MINE_ENTITY, PressureMineEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(PressureMineEntityRenderer.MODEL_LAYER, PressureMineEntityRenderer.Model::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.TRIPWIRE_MINE_ENTITY, TripwireMineEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(TripwireMineEntityRenderer.MODEL_LAYER, TripwireMineModel::getTexturedModelData);
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS, EmptyEntityRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_GAS, EmptyEntityRenderer::new);

		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, FragmentationGrenadeWaveParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SMOKE_PARTICLE, SmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.NERVE_GAS_PARTICLE, NerveGasParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SMALL_FLASH_PARTICLE, SmallFlashParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, TripwireLaserParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SHORT_FLAME_PARTICLE, ShortFlameParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SMALL_SHORT_FLAME_PARTICLE, ShortFlameParticle.SmallFactory::new);

		HandledScreens.register(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, ScrappingTableScreen::new);
		HandledScreens.register(GadgetsScreenHandlerTypes.CORRUGATED, CrateGenericSmallScreen::new);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
