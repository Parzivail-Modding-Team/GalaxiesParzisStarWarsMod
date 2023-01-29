package com.parzivail.pswg.screen;

import com.parzivail.pswg.blockentity.BlasterWorkbenchBlockEntity;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.util.Consumers;
import com.parzivail.util.inventory.StrictSlot;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlasterWorkbenchScreenHandler extends ScreenHandler
{
	private final Inventory inventory;

	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(6));
	}

	public BlasterWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockInventory)
	{
		super(SwgScreenTypes.Workbench.Blaster, syncId);
		this.inventory = blockInventory;
		checkSize(inventory, 6);
		inventory.onOpen(playerInventory.player);

		var onDirtyCallback = (Runnable)() -> onContentChanged(inventory);
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_BLASTER, 19, 19, itemStack -> itemStack.getItem() instanceof BlasterItem, onDirtyCallback));
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_1, 19, 45, Consumers::never, onDirtyCallback));
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_ROD, 19, 63, Consumers::never, onDirtyCallback));
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_PLATE, 19, 81, Consumers::never, onDirtyCallback));
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_COIL, 19, 99, Consumers::never, onDirtyCallback));
		this.addSlot(new StrictSlot(inventory, BlasterWorkbenchBlockEntity.SLOT_BOX, 19, 117, Consumers::never, onDirtyCallback));

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 174));

		for (var column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 8, 232));
	}

	public static void handleSetBlasterTag(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var tag = buf.readNbt();

		server.execute(() -> {
			if (player.currentScreenHandler instanceof BlasterWorkbenchScreenHandler screenHandler)
			{
				screenHandler.setBlasterTag(tag);
			}
		});
	}

	public void setBlasterTag(NbtCompound blasterTag)
	{
		var slot = this.slots.get(BlasterWorkbenchBlockEntity.SLOT_BLASTER);
		var stack = slot.getStack();

		var tag = BlasterTag.fromRootTag(blasterTag);
		tag.serializeAsSubtag(stack.getOrCreateNbt());

		slot.setStack(stack);
		slot.markDirty();
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			var itemStack2 = slot.getStack();
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
