package dev.pswg.blockEntity.screenHandler;

import dev.pswg.container.GalaxiesScreenHandlerTypes;
import net.minecraft.world.entity.player.Inventory;

public class CorrugatedCrateHandler extends CrateGenericSmallScreenHandler
{
	public CorrugatedCrateHandler(int syncId, Inventory playerInventory)
	{
		super(GalaxiesScreenHandlerTypes.CORRUGATED, syncId, playerInventory);
	}
}