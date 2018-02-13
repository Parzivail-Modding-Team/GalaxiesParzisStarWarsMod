package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.keybind.KeyBindingInterceptor;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

public class KeybindRegistry
{
	//	@SideOnly(Side.CLIENT)
	//	public static KeyBinding keyShootVehicle;

	@SideOnly(Side.CLIENT)
	public static KeyBindingInterceptor keyAttack;

	public static void registerAll()
	{
		//KeybindRegistry.keyShootVehicle = registerKeybind("shootVehicle", Keyboard.KEY_F);

		StarWarsGalaxy.mc.gameSettings.keyBindAttack = KeybindRegistry.keyAttack = new KeyBindingInterceptor(StarWarsGalaxy.mc.gameSettings.keyBindAttack);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}
