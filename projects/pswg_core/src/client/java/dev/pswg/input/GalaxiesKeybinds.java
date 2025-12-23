package dev.pswg.input;

import dev.pswg.Galaxies;
import dev.pswg.interaction.GalaxiesEntityItemActionClientManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * User-configurable keybinds
 */
public final class GalaxiesKeybinds
{
	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Galaxies.id("keybinds"));

	private static KeyBinding primaryAction;

	/**
	 * Initializes the keybinds
	 */
	public static void initialize()
	{
		primaryAction = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.pswg.primary_action",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (primaryAction.wasPressed())
				GalaxiesEntityItemActionClientManager.handlePrimaryItemAction();
		});
	}

	/**
	 * Gets the primary action keybind
	 *
	 * @return The primary action keybind
	 */
	public static KeyBinding getPrimaryAction()
	{
		return primaryAction;
	}
}
