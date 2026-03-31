package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.container.GalaxiesParticleTypes;
import dev.pswg.container.GalaxiesScreenHandlerTypes;
import dev.pswg.data.BinaryCodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.interaction.GalaxiesEntityLeftClickClientManager;
import dev.pswg.interaction.GalaxiesPlayerClientActionManager;
import dev.pswg.item.SwgDrinkTintSource;
import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import dev.pswg.networking.IPreciseSpawnDataEntity;
import dev.pswg.networking.PreciseVelocityParticleS2CPayload;
import dev.pswg.particle.ShortFlameParticle;
import dev.pswg.particle.SmallFlashParticle;
import dev.pswg.rendering.models.GalaxiesModelBakery;
import dev.pswg.screens.CrateGenericSmallScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.List;
import java.util.Optional;

/**
 * The main entrypoint for PSWG client-side registration
 */
public class GalaxiesClient implements ClientModInitializer
{
	private static final Minecraft client = Minecraft.getInstance();

	/**
	 * A resource loader for quad buffer files
	 */
	public static final BinaryCodecDataLoader<GalaxiesModelBakery.GQuadGeometry> GQB_LOADER = new BinaryCodecDataLoader<>(
			Galaxies.id("gqb"),
			List.of(ResourceReloaderKeys.Client.MODELS),
			"models",
			true,
			(i) -> IdentifierUtil.hasExtension(i, "gqb"),
			GalaxiesModelBakery.GQuadGeometry.PACKET_CODEC
	);

	/**
	 * A translatable text with two parameters: the keybind value, and the hint text
	 */
	public static final String I18N_KEYBIND_HINT_KEY = getI18nKey(Galaxies.id("hint.keybind"));

	/**
	 * Gets the fractional number of ticks accumulated in the frames rendered
	 * since the last game tick
	 *
	 * @return The fractional number of ticks [0,1)
	 */
	public static float getTickDelta()
	{
		return client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	/**
	 * Gets a translatable text with a keybind value and hint text
	 *
	 * @param keyBinding The keybind to get the value for
	 * @param hint       The hint text to display
	 *
	 * @return A translatable text with the keybind value and hint text
	 */
	public static Component getKeybindHint(KeyMapping keyBinding, Component hint)
	{
		return Component.translatable(I18N_KEYBIND_HINT_KEY, keyBinding.getTranslatedKeyMessage(), hint);
	}

	/**
	 * Gets a text translation key for the given identifier
	 *
	 * @param identifier The identifier to get a translation key for
	 *
	 * @return The translation key for the given identifier
	 */
	public static String getI18nKey(Identifier identifier)
	{
		return String.format("text.%s.%s", identifier.getNamespace(), identifier.getPath());
	}

	@Override
	public void onInitializeClient()
	{
		GalaxiesKeybinds.initialize();

		GalaxiesEntityLeftClickClientManager.initialize();
		GalaxiesPlayerClientActionManager.initialize();

		// Forward spawn packets to the network handler
		ClientPlayNetworking.registerGlobalReceiver(GalaxiesEntitySpawnS2CPacket.ID, (galaxiesEntitySpawnS2CPacket, context) -> {
			Optional.ofNullable(context.client())
			        .map(Minecraft::getConnection)
			        .ifPresent(handler -> {
				        handler.handleAddEntity(galaxiesEntitySpawnS2CPacket.toVanillaPacket());

				        var world = context.client().level;
				        if (world == null)
				        {
					        return;
				        }

				        var entity = world.getEntity(galaxiesEntitySpawnS2CPacket.getEntityId());
				        if (entity instanceof IPreciseSpawnDataEntity preciseSpawnDataEntity)
				        {
					        preciseSpawnDataEntity.applySpawnData(galaxiesEntitySpawnS2CPacket);
				        }
			        });
		});

		ClientPlayNetworking.registerGlobalReceiver(PreciseVelocityParticleS2CPayload.TYPE, (preciseVelocityParticleS2CPayload, context) -> {
			double x = preciseVelocityParticleS2CPayload.posVector().x;
			double y = preciseVelocityParticleS2CPayload.posVector().y;
			double z = preciseVelocityParticleS2CPayload.posVector().z;
			double vX = preciseVelocityParticleS2CPayload.velocityVector().x;
			double vY = preciseVelocityParticleS2CPayload.velocityVector().y;
			double vZ = preciseVelocityParticleS2CPayload.velocityVector().z;
			ParticleOptions particleEffect = preciseVelocityParticleS2CPayload.particleEffect();
			context.client().particleEngine.createParticle(particleEffect, x, y, z, vX, vY, vZ);
		});

		// Register the quad buffer loader
		registerClientReloader(GQB_LOADER);

		//Register tints
		ItemTintSources.ID_MAPPER.put(Galaxies.id("drink"), SwgDrinkTintSource.CODEC);

		// Register particles
		ParticleProviderRegistry.getInstance().register(GalaxiesParticleTypes.SMALL_FLASH_PARTICLE, SmallFlashParticle.Factory::new);

		ParticleProviderRegistry.getInstance().register(GalaxiesParticleTypes.SHORT_FLAME_PARTICLE, ShortFlameParticle.Factory::new);
		ParticleProviderRegistry.getInstance().register(GalaxiesParticleTypes.SMALL_SHORT_FLAME_PARTICLE, ShortFlameParticle.SmallFactory::new);

		MenuScreens.register(GalaxiesScreenHandlerTypes.CORRUGATED, CrateGenericSmallScreen::new);

		GalaxiesRenderLayers.init();

		Galaxies.LOGGER.info("Loading PSWG modules and addons via pswg-client-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesClientReady);
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesFinalizing);

		Galaxies.LOGGER.info("Galaxies client initialized");
	}

	/**
	 * Registers a client resource reloader against Fabric's v1 resource loader
	 * API.
	 *
	 * @param reloader The reloader to register.
	 */
	private static void registerClientReloader(BinaryCodecDataLoader<?> reloader)
	{
		var resourceLoader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
		resourceLoader.registerReloadListener(reloader.getId(), reloader);

		for (var dependency : reloader.getDependencies())
		{
			resourceLoader.addListenerOrdering(reloader.getId(), dependency);
		}
	}
}
