package com.parzivail.pswg.client.screen;

import com.parzivail.pswg.entity.MannequinEntity;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.screen.AstromechScreenHandler;
import com.parzivail.pswg.screen.MannequinScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public class ScreenHelper
{
	public static <T extends ScreenHandler> Screen createEntityScreen(MinecraftClient client, T handler, PlayerInventory inventory, Object entity)
	{
		if (handler instanceof AstromechScreenHandler ash && entity instanceof AstromechEntity astromech)
			return new AstromechScreen(ash, inventory, astromech);

		if (handler instanceof MannequinScreenHandler mannequinScreenHandler && entity instanceof MannequinEntity mannequin)
		{
			return new CharacterScreen(client.currentScreen, new CharacterScreen.Context(true, mannequin.getSpecies()));
		}

		return null;
	}
}
