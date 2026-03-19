package dev.pswg.blockEntity;

import dev.pswg.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class InventoryBlockEntity extends BlockEntity implements Container
{
	protected final NonNullList<ItemStack> inventory;

	public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
	{
		super(type, pos, state);
		inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput view)
	{
		ContainerHelper.saveAllItems(view, this.inventory);
		super.saveAdditional(view);
	}

	@Override
	protected void loadAdditional(ValueInput view)
	{
		ContainerHelper.loadAllItems(view, inventory);
		super.loadAdditional(view);
	}

	@Override
	public int getContainerSize()
	{
		return inventory.size();
	}

	@Override
	public boolean isEmpty()
	{
		return ItemUtil.isInventoryEmpty(inventory);
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount)
	{
		var itemStack = ContainerHelper.removeItem(inventory, slot, amount);
		if (!itemStack.isEmpty())
		{
			this.setChanged();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot)
	{
		var itemStack = inventory.get(slot);
		if (itemStack.isEmpty())
			return ItemStack.EMPTY;
		else
		{
			inventory.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setItem(int slot, ItemStack stack)
	{
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize())
		{
			stack.setCount(this.getMaxStackSize());
		}

		this.setChanged();
	}

	@Override
	public boolean stillValid(Player player)
	{
		if (this.level.getBlockEntity(this.worldPosition) != this)
			return false;
		else
			return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clearContent()
	{
		inventory.clear();
		this.setChanged();
	}
}