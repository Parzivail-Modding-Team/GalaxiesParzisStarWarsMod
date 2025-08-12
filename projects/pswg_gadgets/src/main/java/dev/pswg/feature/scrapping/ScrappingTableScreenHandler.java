package dev.pswg.feature.scrapping;

import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ScrappingTableScreenHandler extends AbstractRecipeScreenHandler
{
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;
	public final int MAX_TOOL_PROGRESS = 48;

	public ScrappingTableScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(8), new ArrayPropertyDelegate(3));
	}

	public ScrappingTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
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
		this.addSlot(new OutputSlot(inventory, 4, 129, 54));
		this.addSlot(new OutputSlot(inventory, 5, 129, 80));
		this.addSlot(new OutputSlot(inventory, 6, 150, 54));
		this.addSlot(new OutputSlot(inventory, 7, 150, 80));

		this.addPlayerSlots(playerInventory, 8, 124);

		this.addProperties(propertyDelegate);
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
		return inventory.getStack(3);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id)
	{
		if (id >= 0 && id < 3)
		{
			switch (id)
			{
				case 0:
					if (propertyDelegate.get(0) > -1)
						propertyDelegate.set(0, Math.min(getCutterProgress() + 10, MAX_TOOL_PROGRESS));
					break;
				case 1:
					if (propertyDelegate.get(1) > -1)
						propertyDelegate.set(1, Math.min(getSpannerProgress() + 10, MAX_TOOL_PROGRESS));
					break;
				case 2:
					if (propertyDelegate.get(2) > -1)
						propertyDelegate.set(2, Math.min(getCalibratorProgress() + 10, MAX_TOOL_PROGRESS));
			}
			return true;
		}
		return super.onButtonClick(player, id);
	}


	@Override
	public PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory)
	{
		return PostFillAction.NOTHING;
	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder)
	{
		if (this.inventory instanceof RecipeInputProvider recipeInputProvider)
		{
			recipeInputProvider.provideRecipeInputs(finder);
		}
	}

	@Override
	public RecipeBookType getCategory()
	{
		return RecipeBookType.CRAFTING;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			var slotStack = slot.getStack();
			itemStack = slotStack.copy();
			if (index < this.inventory.size())
			{
				if (!this.insertItem(slotStack, this.inventory.size(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(slotStack, 0, this.inventory.size(), false))
			{
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
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
