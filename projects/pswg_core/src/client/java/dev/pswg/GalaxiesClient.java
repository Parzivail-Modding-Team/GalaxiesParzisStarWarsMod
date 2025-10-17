package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.input.GalaxiesKeybinds;
import dev.pswg.interaction.GalaxiesEntityLeftClickClientManager;
import dev.pswg.interaction.GalaxiesPlayerClientActionManager;
import dev.pswg.networking.GalaxiesEntitySpawnS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * The main entrypoint for PSWG client-side registration
 */
public class GalaxiesClient implements ClientModInitializer
{
	private static final MinecraftClient client = MinecraftClient.getInstance();

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
		return client.getRenderTickCounter().getTickProgress(false);
	}

	/**
	 * Gets a translatable text with a keybind value and hint text
	 *
	 * @param keyBinding The keybind to get the value for
	 * @param hint       The hint text to display
	 *
	 * @return A translatable text with the keybind value and hint text
	 */
	public static Text getKeybindHint(KeyBinding keyBinding, Text hint)
	{
		return Text.translatable(I18N_KEYBIND_HINT_KEY, keyBinding.getBoundKeyLocalizedText(), hint);
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
			        .map(MinecraftClient::getNetworkHandler)
			        .ifPresent(handler -> handler.onEntitySpawn(galaxiesEntitySpawnS2CPacket));
		});

		Galaxies.LOGGER.info("Loading PSWG modules and addons via pswg-client-addon");
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesClientReady);
		FabricLoader.getInstance().invokeEntrypoints("pswg-client-addon", GalaxiesClientAddon.class, GalaxiesClientAddon::onGalaxiesFinalizing);

		Galaxies.LOGGER.info("Galaxies client initialized");
	}
}
