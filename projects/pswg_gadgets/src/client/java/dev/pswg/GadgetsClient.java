package dev.pswg;

import com.google.common.collect.Maps;
import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.entity.grenades.GrenadeEntity;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.feature.drill.HasCapsuleProperty;
import dev.pswg.feature.drill.HasDrillProperty;
import dev.pswg.feature.drill.HasExtractorProperty;
import dev.pswg.networking.MixerSyncS2CPayload;
import dev.pswg.particles.*;
import dev.pswg.renderer.grenades.G3dGrenadeEntityRenderer;
import dev.pswg.renderer.mines.PressureMineEntityRenderer;
import dev.pswg.renderer.mines.TripwireMineEntityRenderer;
import dev.pswg.screens.MixerScreen;
import dev.pswg.screens.ScrappingTableScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import org.joml.Vector2i;

import java.util.Map;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	/**
	 * Registers one grenade against the same normal and primed assets as its item.
	 */
	private static <T extends GrenadeEntity> void registerGrenade(
			EntityType<T> type,
			String modelName,
			boolean primed
	)
	{
		var modelId = Gadgets.id("item/" + modelName + "_in_hand");
		var primedModelId = primed ? Gadgets.id("item/" + modelName + "_primed_in_hand") : null;
		EntityRendererRegistry.register(
				type,
				context -> new G3dGrenadeEntityRenderer<>(context, modelId, primedModelId)
		);
	}

	public static Map<GadgetsParticleRenderer, ParticleGroup<?>> particleRenderers = Maps.newIdentityHashMap();
	@Override
	public void onGalaxiesClientReady()
	{
		registerGrenade(GadgetsEntities.THERMAL_DETONATOR_ENTITY, "thermal_detonator", true);
		registerGrenade(GadgetsEntities.FRAGMENTATION_GRENADE_ENTITY, "fragmentation_grenade", true);
		registerGrenade(GadgetsEntities.NERVE_GAS_GRENADE_ENTITY, "nerve_gas_grenade", false);
		registerGrenade(GadgetsEntities.SMOKE_SIGNAL_GRENADE_ENTITY, "smoke_signal_grenade", true);
		registerGrenade(GadgetsEntities.IMPACT_GRENADE_ENTITY, "impact_grenade", false);
		registerGrenade(GadgetsEntities.INFERNO_GRENADE_ENTITY, "inferno_grenade", true);
		EntityRendererRegistry.register(GadgetsEntities.PRESSURE_MINE_ENTITY, PressureMineEntityRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.TRIPWIRE_MINE_ENTITY, TripwireMineEntityRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.NERVE_GAS, NoopRenderer::new);
		EntityRendererRegistry.register(GadgetsEntities.SMOKE_GAS, NoopRenderer::new);

		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.EXPLOSION_SMOKE_PARTICLE, ExplosionSmokeParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_SPARK_PARTICLE, FragmentationGrenadeSparkParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.FRAGMENTATION_GRENADE_WAVE_PARTICLE, FragmentationGrenadeWaveParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.SMOKE_PARTICLE, SmokeParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.NERVE_GAS_PARTICLE, NerveGasParticle.Factory::new);

		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.TRIPWIRE_LASER_PARTICLE, TripwireLaserParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.DENSE_INFERNO_SCORCH_PARTICLE, InfernoScorchParticle.Factory::new);

		ParticleProviderRegistry.getInstance().register(GadgetsParticleTypes.LASER_CUT_PARTICLE, LaserCutParticle.Factory::new);

		MenuScreens.register(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, ScrappingTableScreen::new);
		MenuScreens.register(GadgetsScreenHandlerTypes.MIXER, MixerScreen::new);

		ClientTickEvents.END_CLIENT_TICK.register(LaserCutterHandler::tick);

		ClientPlayNetworking.registerGlobalReceiver(MixerSyncS2CPayload.TYPE, (mixerSyncS2CPayload, context) -> {
			if (context.player().containerMenu instanceof MixerScreenHandler mixerScreenHandler)
			{
				mixerScreenHandler.drinkEffects = mixerSyncS2CPayload.drinkEffects();
				mixerScreenHandler.drinkColors = mixerSyncS2CPayload.drinkColors();
				mixerScreenHandler.drinkFoods = mixerSyncS2CPayload.drinkFoods();
			}
		});

		MixerScreen.ICON_MAP.put(MobEffects.ABSORPTION, new Vector2i(126, 127));
		MixerScreen.ICON_MAP.put(MobEffects.DOLPHINS_GRACE, new Vector2i(350, 161));
		MixerScreen.ICON_MAP.put(MobEffects.FIRE_RESISTANCE, new Vector2i(336, 33));
		MixerScreen.ICON_MAP.put(MobEffects.HASTE, new Vector2i(190, 447));
		MixerScreen.ICON_MAP.put(MobEffects.HEALTH_BOOST, new Vector2i(46, 225));
		MixerScreen.ICON_MAP.put(MobEffects.INVISIBILITY, new Vector2i(64, 384));
		MixerScreen.ICON_MAP.put(MobEffects.INSTANT_HEALTH, new Vector2i(127, 244));
		MixerScreen.ICON_MAP.put(MobEffects.JUMP_BOOST, new Vector2i(336, 400));
		MixerScreen.ICON_MAP.put(MobEffects.LUCK, new Vector2i(143, 384));
		MixerScreen.ICON_MAP.put(MobEffects.NIGHT_VISION, new Vector2i(95, 324));
		MixerScreen.ICON_MAP.put(MobEffects.REGENERATION, new Vector2i(31, 65));
		MixerScreen.ICON_MAP.put(MobEffects.RESISTANCE, new Vector2i(224, 65));
		MixerScreen.ICON_MAP.put(MobEffects.SPEED, new Vector2i(382, 273));
		MixerScreen.ICON_MAP.put(MobEffects.STRENGTH, new Vector2i(448, 448));

		ConditionalItemModelProperties.ID_MAPPER.put(Gadgets.id("has_capsule"), HasCapsuleProperty.CODEC);
		ConditionalItemModelProperties.ID_MAPPER.put(Gadgets.id("has_extractor"), HasExtractorProperty.CODEC);
		ConditionalItemModelProperties.ID_MAPPER.put(Gadgets.id("has_drill"), HasDrillProperty.CODEC);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
