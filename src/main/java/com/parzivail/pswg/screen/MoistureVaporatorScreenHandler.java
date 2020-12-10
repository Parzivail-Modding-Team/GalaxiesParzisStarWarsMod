package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.container.SwgScreenTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MoistureVaporatorScreenHandler extends InventoryScreenHandler
{
	private final PropertyDelegate propertyDelegate;
	private final World world;

	public MoistureVaporatorScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(2));
	}

	public MoistureVaporatorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(SwgScreenTypes.MoistureVaporator.GX8, syncId, inventory);
		this.propertyDelegate = propertyDelegate;
		checkSize(inventory, 2);
		this.world = playerInventory.player.world;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 31, 35)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return isHydratable(stack);
			}
		});

		this.addSlot(new Slot(inventory, 1, 129, 35)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return false;
			}
		});

		for (int row = 0; row < 3; ++row)
			for (int column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 84));

		for (int column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 8, 142));

		this.addProperties(propertyDelegate);
	}

	protected boolean isHydratable(ItemStack itemStack)
	{
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(itemStack), this.world).isPresent();
	}

	@Environment(EnvType.CLIENT)
	public int getCollectionTimer()
	{
		return this.propertyDelegate.get(0);
	}

	@Environment(EnvType.CLIENT)
	public int getCollectionTimerLength()
	{
		return this.propertyDelegate.get(1);
	}
}
