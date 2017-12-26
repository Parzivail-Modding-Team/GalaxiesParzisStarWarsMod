package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeybindRegistry
{
	//	@SideOnly(Side.CLIENT)
	//	public static KeyBinding keyShootVehicle;

	public static void registerAll()
	{
		//KeybindRegistry.keyShootVehicle = registerKeybind("shootVehicle", Keyboard.KEY_F);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}
