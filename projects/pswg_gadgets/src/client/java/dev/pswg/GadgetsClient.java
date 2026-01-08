package dev.pswg;

import com.google.common.collect.Maps;
import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.autoreg.ClientBlockRegistryData;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.models.*;
import dev.pswg.packet.MixerSyncS2CPayload;
import dev.pswg.packet.PreciseVelocityParticleS2CPayload;
import dev.pswg.particles.*;
import dev.pswg.renderer.grenades.*;
import dev.pswg.renderer.mines.PressureMineEntityRenderer;
import dev.pswg.renderer.mines.TripwireMineEntityRenderer;
import dev.pswg.screens.MixerScreen;
import dev.pswg.screens.ScrappingTableScreen;
import dev.pswg.autoreg.AutoGenerateUtil;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Pair;

import java.util.Map;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	public static Map<GadgetsParticleRenderer, ParticleRenderer<?>> particleRenderers = Maps.newIdentityHashMap();
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
		ParticleFactoryRegistry.getInstance().register(GadgetsParticleTypes.LASER_CUT_PARTICLE, LaserCutParticle.Factory::new);

		HandledScreens.register(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, ScrappingTableScreen::new);
		//TODO: MOVE
		// HandledScreens.register(GadgetsScreenHandlerTypes.CORRUGATED, CrateGenericSmallScreen::new);
		HandledScreens.register(GadgetsScreenHandlerTypes.MIXER, MixerScreen::new);

		AutoGenerateUtil.consumeAnnotatedGalaxiesBlocks(ClientBlockRegistryData.class, (block, clientData) -> {
			switch (clientData.renderLayer())
			{
				case Transparent:
					BlockRenderLayerMap.putBlock(block, BlockRenderLayer.TRANSLUCENT);
				case CutoutMipped:
					BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT_MIPPED);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(LaserCutterHandler::tick);

		ClientPlayNetworking.registerGlobalReceiver(MixerSyncS2CPayload.ID, (mixerSyncS2CPayload, context) -> {
			if (context.player().currentScreenHandler instanceof MixerScreenHandler mixerScreenHandler)
			{
				mixerScreenHandler.drinkEffects = mixerSyncS2CPayload.drinkEffects();
				mixerScreenHandler.drinkColors = mixerSyncS2CPayload.drinkColors();
				mixerScreenHandler.drinkFoods = mixerSyncS2CPayload.drinkFoods();
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(PreciseVelocityParticleS2CPayload.ID, (preciseVelocityParticleS2CPayload, context) -> {
			double x = preciseVelocityParticleS2CPayload.posVector().x;
			double y = preciseVelocityParticleS2CPayload.posVector().y;
			double z = preciseVelocityParticleS2CPayload.posVector().z;
			double vX = preciseVelocityParticleS2CPayload.velocityVector().x;
			double vY = preciseVelocityParticleS2CPayload.velocityVector().y;
			double vZ = preciseVelocityParticleS2CPayload.velocityVector().z;
			ParticleEffect particleEffect = preciseVelocityParticleS2CPayload.particleEffect();
			context.client().particleManager.addParticle(particleEffect, x, y, z, vX, vY, vZ);
		});

		MixerScreen.ICON_MAP.put(StatusEffects.ABSORPTION, new Pair<>(126, 127));
		MixerScreen.ICON_MAP.put(StatusEffects.DOLPHINS_GRACE, new Pair<>(350, 161));
		MixerScreen.ICON_MAP.put(StatusEffects.FIRE_RESISTANCE, new Pair<>(336, 33));
		MixerScreen.ICON_MAP.put(StatusEffects.HASTE, new Pair<>(190, 447));
		MixerScreen.ICON_MAP.put(StatusEffects.HEALTH_BOOST, new Pair<>(46, 225));
		MixerScreen.ICON_MAP.put(StatusEffects.INVISIBILITY, new Pair<>(64, 384));
		MixerScreen.ICON_MAP.put(StatusEffects.INSTANT_HEALTH, new Pair<>(127, 244));
		MixerScreen.ICON_MAP.put(StatusEffects.JUMP_BOOST, new Pair<>(336, 400));
		MixerScreen.ICON_MAP.put(StatusEffects.LUCK, new Pair<>(143, 384));
		MixerScreen.ICON_MAP.put(StatusEffects.NIGHT_VISION, new Pair<>(95, 324));
		MixerScreen.ICON_MAP.put(StatusEffects.REGENERATION, new Pair<>(31, 65));
		MixerScreen.ICON_MAP.put(StatusEffects.RESISTANCE, new Pair<>(224, 65));
		MixerScreen.ICON_MAP.put(StatusEffects.SPEED, new Pair<>(382, 273));
		MixerScreen.ICON_MAP.put(StatusEffects.STRENGTH, new Pair<>(448, 448));

		GadgetsRenderLayers.init();

		Gadgets.LOGGER.info("Client module initialized");
	}
}
