package com.parzivail.pswg.screen;

import com.parzivail.pswg.client.screen.slot.StrictSlot;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.item.lightsaber.LightsaberItem;
import com.parzivail.pswg.item.lightsaber.LightsaberTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class LightsaberForgeScreenHandler extends ScreenHandler
{
	private final Inventory inventory = new SimpleInventory(1)
	{
		public void markDirty()
		{
			super.markDirty();
			LightsaberForgeScreenHandler.this.onContentChanged(this);
		}
	};
	protected final ScreenHandlerContext context;

	public LightsaberForgeScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public LightsaberForgeScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context)
	{
		super(SwgScreenTypes.Workbench.Lightsaber, syncId);
		this.context = context;
		checkSize(inventory, 1);
		inventory.onOpen(playerInventory.player);

		this.addSlot(new StrictSlot(inventory, 0, 14, 63, itemStack -> itemStack.getItem() instanceof LightsaberItem));

		for (int row = 0; row < 3; ++row)
			for (int column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 48, row * 18 + 159));

		for (int column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 48, 217));
	}

	public void setLightsaberTag(CompoundTag lightsaberTag)
	{
		Slot slot = this.slots.get(0);
		ItemStack stack = slot.getStack();

		LightsaberTag tag = LightsaberTag.fromRootTag(lightsaberTag);
		tag.serializeAsSubtag(stack.getOrCreateTag());

		slot.setStack(stack);
		slot.markDirty();
	}

	public void close(PlayerEntity player)
	{
		super.close(player);
		this.context.run((world, blockPos) -> {
			this.dropInventory(player, world, this.inventory);
		});
	}

	public ItemStack transferSlot(PlayerEntity player, int index)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index < this.inventory.size())
			{
				if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false))
			{
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty())
			{
				slot.setStack(ItemStack.EMPTY);
			}
			else
			{
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
