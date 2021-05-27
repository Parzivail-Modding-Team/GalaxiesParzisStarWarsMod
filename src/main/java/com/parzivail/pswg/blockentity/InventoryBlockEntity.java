package com.parzivail.pswg.blockentity;

import com.parzivail.util.item.ItemUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public abstract class InventoryBlockEntity extends BlockEntity implements Inventory
{
	protected final DefaultedList<ItemStack> inventory;

	public InventoryBlockEntity(BlockEntityType<?> type, int inventorySize)
	{
		super(type);
		inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag)
	{
		Inventories.writeNbt(tag, this.inventory);
		return super.writeNbt(tag);
	}

	@Override
	public void readNbt(BlockState state, NbtCompound tag)
	{
		Inventories.readNbt(tag, inventory);
		super.readNbt(state, tag);
	}

	@Override
	public int size()
	{
		return inventory.size();
	}

	@Override
	public boolean isEmpty()
	{
		return ItemUtil.isInventoryEmpty(inventory);
	}

	@Override
	public ItemStack getStack(int slot)
	{
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount)
	{
		ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
		if (!itemStack.isEmpty())
		{
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot)
	{
		ItemStack itemStack = inventory.get(slot);
		if (itemStack.isEmpty())
			return ItemStack.EMPTY;
		else
		{
			inventory.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack)
	{
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack())
		{
			stack.setCount(this.getMaxCountPerStack());
		}

		this.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player)
	{
		if (this.world.getBlockEntity(this.pos) != this)
			return false;
		else
			return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void clear()
	{
		inventory.clear();
		this.markDirty();
	}
}
