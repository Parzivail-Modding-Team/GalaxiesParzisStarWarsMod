package dev.pswg.blockEntity.screenHandler;

import dev.pswg.container.GalaxiesScreenHandlerTypes;
import net.minecraft.entity.player.PlayerInventory;

public class CorrugatedCrateHandler extends CrateGenericSmallScreenHandler
{
	public CorrugatedCrateHandler(int syncId, PlayerInventory playerInventory)
	{
		super(GalaxiesScreenHandlerTypes.CORRUGATED, syncId, playerInventory);
	}
}