package dev.pswg.blockEntity;

import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.screenHandler.CrateGenericSmallScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CrateCorrugatedBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public CrateCorrugatedBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.CORRUGATED_CRATE_BLOCK_ENTITY, pos, state, 15);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.literal("Corrugated Crate");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(GadgetsScreenHandlerTypes.CORRUGATED, syncId, inv, this);
	}
}