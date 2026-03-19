package dev.pswg;

import com.google.common.collect.Maps;
import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.autoreg.ClientBlockRegistryData;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.models.*;
import dev.pswg.networking.MixerSyncS2CPayload;
import dev.pswg.networking.PreciseVelocityParticleS2CPayload;
import dev.pswg.particles.*;
import dev.pswg.renderer.grenades.*;
import dev.pswg.renderer.mines.PressureMineEntityRenderer;
import dev.pswg.renderer.mines.TripwireMineEntityRenderer;
import dev.pswg.screens.MixerScreen;
import dev.pswg.screens.ScrappingTableScreen;
import dev.pswg.autoreg.AutoGenerateUtil;
import dev.pswg.util.GadgetsGenUtil;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import java.util.Map;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	public static Map<GadgetsParticleRenderer, ParticleGroup<?>> particleRenderers = Maps.newIdentityHashMap();
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
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS, NoopRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_GAS, NoopRenderer::new);

		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, FragmentationGrenadeWaveParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.SMOKE_PARTICLE, SmokeParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.NERVE_GAS_PARTICLE, NerveGasParticle.Factory::new);

		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, TripwireLaserParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);

		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.LASER_CUT_PARTICLE, LaserCutParticle.Factory::new);

		MenuScreens.register(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, ScrappingTableScreen::new);
		MenuScreens.register(GadgetsScreenHandlerTypes.MIXER, MixerScreen::new);

		GadgetsGenUtil.consumeAnnotatedGadgetsBlocks(ClientBlockRegistryData.class, (block, clientData) -> {
			switch (clientData.renderLayer())
			{
				case TRANSPARENT:
					BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
				case CUTOUT_MIPPED:
					BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT_MIPPED);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(LaserCutterHandler::tick);

		ClientPlayNetworking.registerGlobalReceiver(MixerSyncS2CPayload.ID, (mixerSyncS2CPayload, context) -> {
			if (context.player().containerMenu instanceof MixerScreenHandler mixerScreenHandler)
			{
				mixerScreenHandler.drinkEffects = mixerSyncS2CPayload.drinkEffects();
				mixerScreenHandler.drinkColors = mixerSyncS2CPayload.drinkColors();
				mixerScreenHandler.drinkFoods = mixerSyncS2CPayload.drinkFoods();
			}
		});

		MixerScreen.ICON_MAP.put(MobEffects.ABSORPTION, new Tuple<>(126, 127));
		MixerScreen.ICON_MAP.put(MobEffects.DOLPHINS_GRACE, new Tuple<>(350, 161));
		MixerScreen.ICON_MAP.put(MobEffects.FIRE_RESISTANCE, new Tuple<>(336, 33));
		MixerScreen.ICON_MAP.put(MobEffects.HASTE, new Tuple<>(190, 447));
		MixerScreen.ICON_MAP.put(MobEffects.HEALTH_BOOST, new Tuple<>(46, 225));
		MixerScreen.ICON_MAP.put(MobEffects.INVISIBILITY, new Tuple<>(64, 384));
		MixerScreen.ICON_MAP.put(MobEffects.INSTANT_HEALTH, new Tuple<>(127, 244));
		MixerScreen.ICON_MAP.put(MobEffects.JUMP_BOOST, new Tuple<>(336, 400));
		MixerScreen.ICON_MAP.put(MobEffects.LUCK, new Tuple<>(143, 384));
		MixerScreen.ICON_MAP.put(MobEffects.NIGHT_VISION, new Tuple<>(95, 324));
		MixerScreen.ICON_MAP.put(MobEffects.REGENERATION, new Tuple<>(31, 65));
		MixerScreen.ICON_MAP.put(MobEffects.RESISTANCE, new Tuple<>(224, 65));
		MixerScreen.ICON_MAP.put(MobEffects.SPEED, new Tuple<>(382, 273));
		MixerScreen.ICON_MAP.put(MobEffects.STRENGTH, new Tuple<>(448, 448));

		Gadgets.LOGGER.info("Client module initialized");
	}
}
