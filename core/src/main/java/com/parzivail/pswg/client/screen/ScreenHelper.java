package com.parzivail.pswg.client.screen;

import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.screen.AstromechScreenHandler;
import com.parzivail.util.entity.EntityWithInventory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public class ScreenHelper
{
	public static <T extends ScreenHandler> Screen createEntityScreen(T handler, PlayerInventory inventory, EntityWithInventory<T> inventoryEntity)
	{
		if (handler instanceof AstromechScreenHandler ash && inventoryEntity instanceof AstromechEntity astromech)
			return new AstromechScreen(ash, inventory, astromech);

		return null;
	}
}
