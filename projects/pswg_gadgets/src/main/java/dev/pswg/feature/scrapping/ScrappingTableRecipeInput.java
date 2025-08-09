package dev.pswg.feature.scrapping;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class ScrappingTableRecipeInput implements RecipeInput
{
	public final ItemStack spanner;
	public final ItemStack cutter;
	public final ItemStack calibrator;
	public final ItemStack scrapItem;

	public ScrappingTableRecipeInput(ItemStack cutter, ItemStack spanner, ItemStack calibrator, ItemStack scrapItem)
	{
		this.spanner = spanner;
		this.cutter = cutter;
		this.calibrator = calibrator;
		this.scrapItem = scrapItem;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot > 3 || slot < 0)
		{
			throw new IllegalArgumentException("No item for index " + slot);
		}
		else
		{
			return switch (slot)
			{
				case 0 -> cutter;
				case 1 -> spanner;
				case 2 -> calibrator;
				case 3 -> scrapItem;
				default -> null;
			};
		}
	}

	@Override
	public boolean isEmpty()
	{
		return spanner.isEmpty() && cutter.isEmpty() && calibrator.isEmpty() && scrapItem.isEmpty();
	}

	@Override
	public int size()
	{
		return 4;
	}
}
