package dev.pswg.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pswg.Galaxies;
import dev.pswg.interaction.GalaxiesEntityItemActionClientManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * User-configurable keybinds
 */
public final class GalaxiesKeybinds
{
	public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Galaxies.id("keybinds"));

	private static KeyMapping primaryAction;

	/**
	 * Initializes the keybinds
	 */
	public static void initialize()
	{
		primaryAction = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.pswg.primary_action",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (primaryAction.consumeClick())
				GalaxiesEntityItemActionClientManager.handlePrimaryItemAction();
		});
	}

	/**
	 * Gets the primary action keybind
	 *
	 * @return The primary action keybind
	 */
	public static KeyMapping getPrimaryAction()
	{
		return primaryAction;
	}
}
