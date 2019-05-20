package com.parzivail.swg.register;

import com.parzivail.swg.Resources;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class KeybindRegister
{
	//	@SideOnly(Side.CLIENT)
	//	public static KeyBinding keyShootVehicle;

	//	@SideOnly(Side.CLIENT)
	//	public static InterceptingKeyBinding keyAttack;

	@SideOnly(Side.CLIENT)
	public static KeyBinding keyDebug;
	@SideOnly(Side.CLIENT)
	public static KeyBinding keyShipChangeInputMode;

	public static void register()
	{
		//KeybindRegistry.keyShootVehicle = registerKeybind("shootVehicle", Keyboard.KEY_F);
		keyDebug = registerKeybind("debug", Keyboard.KEY_NONE);
		keyShipChangeInputMode = registerKeybind("shipInputMode", Keyboard.KEY_X);

		//		Client.mc.gameSettings.keyBindAttack = keyAttack = new InterceptingKeyBinding(Client.mc.gameSettings.keyBindAttack);
	}

	private static KeyBinding registerKeybind(String keyName, int keyCode)
	{
		KeyBinding b = new KeyBinding("key." + Resources.MODID + "." + keyName, keyCode, "key." + Resources.MODID);
		ClientRegistry.registerKeyBinding(b);
		return b;
	}
}
