package dev.pswg.feature.scrapping.table;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ScrappingTableRecipeInput implements RecipeInput
{
	public final ItemStack tool;
	public final ItemStack scrapItem;

	public ScrappingTableRecipeInput(ItemStack tool, ItemStack scrapItem)
	{
		this.tool = tool;
		this.scrapItem = scrapItem;
	}

	@Override
	public ItemStack getItem(int slot)
	{
		if (slot > 2 || slot < 0)
		{
			throw new IllegalArgumentException("No item for index " + slot);
		}
		else
		{
			return switch (slot)
			{
				case 0 -> tool;
				case 1 -> scrapItem;
				default -> null;
			};
		}
	}

	@Override
	public boolean isEmpty()
	{
		return tool.isEmpty() && scrapItem.isEmpty();
	}

	@Override
	public int size()
	{
		return 2;
	}
}
