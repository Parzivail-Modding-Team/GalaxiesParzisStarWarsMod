package com.parzivail.pswg.screen;

import com.parzivail.pswg.container.SwgRecipeType;
import com.parzivail.pswg.container.SwgScreenTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MoistureVaporatorScreenHandler extends AbstractRecipeScreenHandler<Inventory>
{
	private final PropertyDelegate propertyDelegate;
	private final World world;
	private final Inventory inventory;

	public MoistureVaporatorScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(2));
	}

	public MoistureVaporatorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(SwgScreenTypes.MoistureVaporator.GX8, syncId);
		this.inventory = inventory;
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

		for (var row = 0; row < 3; ++row)
			for (var column = 0; column < 9; ++column)
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 84));

		for (var column = 0; column < 9; ++column)
			this.addSlot(new Slot(playerInventory, column, column * 18 + 8, 142));

		this.addProperties(propertyDelegate);
	}

	protected boolean isHydratable(ItemStack itemStack)
	{
		return this.world.getRecipeManager().getFirstMatch(SwgRecipeType.Vaporator, new SimpleInventory(itemStack), this.world).isPresent();
	}

	public ItemStack transferSlot(PlayerEntity player, int index)
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

	@Override
	public void populateRecipeFinder(RecipeMatcher finder)
	{
		if (this.inventory instanceof RecipeInputProvider)
			((RecipeInputProvider)this.inventory).provideRecipeInputs(finder);
	}

	@Override
	public void clearCraftingSlots()
	{
		this.getSlot(0).setStack(ItemStack.EMPTY);
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe)
	{
		return recipe.matches(this.inventory, this.world);
	}

	@Override
	public int getCraftingResultSlotIndex()
	{
		return 1;
	}

	@Override
	public int getCraftingWidth()
	{
		return 1;
	}

	@Override
	public int getCraftingHeight()
	{
		return 1;
	}

	@Override
	public int getCraftingSlotCount()
	{
		return 2;
	}

	@Override
	public RecipeBookCategory getCategory()
	{
		return RecipeBookCategory.CRAFTING;
	}

	@Override
	public boolean canInsertIntoSlot(int index)
	{
		return index == 0;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
