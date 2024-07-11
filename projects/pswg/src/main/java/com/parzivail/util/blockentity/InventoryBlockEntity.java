package com.parzivail.util.blockentity;

import com.parzivail.util.item.ItemUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public abstract class InventoryBlockEntity extends BlockEntity implements Inventory
{
	protected final DefaultedList<ItemStack> inventory;

	public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize)
	{
		super(type, pos, state);
		inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.writeNbt(tag, registryLookup);
		Inventories.writeNbt(tag, this.inventory, registryLookup);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.readNbt(tag, registryLookup);
		Inventories.readNbt(tag, inventory, registryLookup);
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
		var itemStack = Inventories.splitStack(inventory, slot, amount);
		if (!itemStack.isEmpty())
		{
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot)
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
