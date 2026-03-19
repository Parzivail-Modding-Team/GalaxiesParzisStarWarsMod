package dev.pswg.feature.scrapping.table;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class ScrappingTableScreenHandler extends RecipeBookMenu
{
	private final Container inventory;
	private final Inventory playerInventory;
	private final ContainerData propertyDelegate;
	protected final Level world;
	public final int MAX_TOOL_PROGRESS = 480;

	public ScrappingTableScreenHandler(int syncId, Inventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleContainer(10), new SimpleContainerData(3));
	}

	public ScrappingTableScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate)
	{
		super(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.level();
		this.propertyDelegate = propertyDelegate;
		///  Cutter
		this.addSlot(new ToolSlot(inventory, 0, 6, 46, GadgetsItems.CUTTER_ITEM));
		///  Spanner
		this.addSlot(new ToolSlot(inventory, 1, 6, 68, GadgetsItems.SPANNER_ITEM));
		///  Calibrator
		this.addSlot(new ToolSlot(inventory, 2, 6, 90, GadgetsItems.CALIBRATOR_ITEM));
		///  Input
		this.addSlot(new SingleItemInputSlot(inventory, 3, 81, 28));
		/// Output
		this.addSlot(new OutputSlot(inventory, 4, 129, 46));
		this.addSlot(new OutputSlot(inventory, 5, 150, 46));
		this.addSlot(new OutputSlot(inventory, 6, 129, 68));
		this.addSlot(new OutputSlot(inventory, 7, 150, 68));
		this.addSlot(new OutputSlot(inventory, 8, 129, 90));
		this.addSlot(new OutputSlot(inventory, 9, 150, 90));

		this.addStandardInventorySlots(playerInventory, 8, 124);

		this.addDataSlots(propertyDelegate);
	}

	public int getCutterProgress()
	{
		return propertyDelegate.get(0);
	}

	public int getSpannerProgress()
	{
		return propertyDelegate.get(1);
	}

	public int getCalibratorProgress()
	{
		return propertyDelegate.get(2);
	}

	public int getToolProgress(int toolIndex)
	{
		return propertyDelegate.get(toolIndex);
	}

	public ItemStack getInputItem()
	{
		return inventory.getItem(3);
	}

	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if (id >= 0 && id < 3)
		{
			ItemStack stack = inventory.getItem(id);
			int efficiency = (int)(100 - ((float)stack.getOrDefault(DataComponents.DAMAGE, 0) / (float)stack.getOrDefault(DataComponents.MAX_DAMAGE, 1) * 75));
			switch (id)
			{
				case 0:
					if (propertyDelegate.get(0) > -1)
						propertyDelegate.set(0, Math.min(getCutterProgress() + efficiency, MAX_TOOL_PROGRESS));
					break;
				case 1:
					if (propertyDelegate.get(1) > -1)
						propertyDelegate.set(1, Math.min(getSpannerProgress() + efficiency, MAX_TOOL_PROGRESS));
					break;
				case 2:
					if (propertyDelegate.get(2) > -1)
						propertyDelegate.set(2, Math.min(getCalibratorProgress() + efficiency, MAX_TOOL_PROGRESS));
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}


	@Override
	public PostPlaceAction handlePlacement(boolean craftAll, boolean creative, RecipeHolder<?> recipe, ServerLevel world, Inventory inventory)
	{
		return PostPlaceAction.NOTHING;
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedItemContents finder)
	{
		if (this.inventory instanceof StackedContentsCompatible recipeInputProvider)
		{
			recipeInputProvider.fillStackedContents(finder);
		}
	}

	@Override
	public RecipeBookType getRecipeBookType()
	{
		return RecipeBookType.CRAFTING;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasItem())
		{
			var slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index < this.inventory.getContainerSize())
			{
				if (!this.moveItemStackTo(slotStack, this.inventory.getContainerSize(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(slotStack, 0, this.inventory.getContainerSize(), false))
			{
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
			{
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}

		return itemStack;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return this.inventory.stillValid(player);
	}
}
