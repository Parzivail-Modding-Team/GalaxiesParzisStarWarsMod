package dev.pswg.screenHandler;

import dev.pswg.container.GadgetsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class CorrugatedCrateHandler extends CrateGenericSmallScreenHandler
{
	public CorrugatedCrateHandler(int syncId, PlayerInventory playerInventory)
	{
		super(GadgetsScreenHandlerTypes.CORRUGATED, syncId, playerInventory);
	}
}