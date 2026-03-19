package dev.pswg.blockEntity;

import dev.pswg.blockEntity.screenHandler.CrateGenericSmallScreenHandler;
import dev.pswg.container.GalaxiesBlockEntities;
import dev.pswg.container.GalaxiesScreenHandlerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CrateCorrugatedBlockEntity extends RandomizableContainerBlockEntity implements MenuProvider
{
	private NonNullList<ItemStack> inventory = NonNullList.withSize(15, ItemStack.EMPTY);
	public CrateCorrugatedBlockEntity(BlockPos pos, BlockState state)
	{
		super(GalaxiesBlockEntities.CORRUGATED_CRATE_BLOCK_ENTITY, pos, state);
	}

	@Override
	public Component getDisplayName()
	{
		return Component.literal("Corrugated Crate");
	}

	@Override
	protected Component getDefaultName()
	{
		return Component.nullToEmpty("Corrugated Crate");
	}

	@Override
	protected NonNullList<ItemStack> getItems()
	{
		return this.inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> inventory)
	{
		this.inventory = inventory;
	}

	@Override
	protected void saveAdditional(ValueOutput view)
	{
		super.saveAdditional(view);
		if (!this.trySaveLootTable(view))
		{
			ContainerHelper.saveAllItems(view, this.inventory);
		}
	}

	@Override
	protected void loadAdditional(ValueInput view)
	{
		super.loadAdditional(view);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(view))
		{
			ContainerHelper.loadAllItems(view, this.inventory);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player)
	{
		return new CrateGenericSmallScreenHandler(GalaxiesScreenHandlerTypes.CORRUGATED, syncId, inv, this);
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory)
	{
		return createMenu(syncId, playerInventory, playerInventory.player);
	}

	@Override
	public int getContainerSize()
	{
		return 15;
	}

	@Override
	public void startOpen(ContainerUser user)
	{
		if (user instanceof Player player)
			unpackLootTable(player);
		else
			unpackLootTable(null);
		super.startOpen(user);
	}
}