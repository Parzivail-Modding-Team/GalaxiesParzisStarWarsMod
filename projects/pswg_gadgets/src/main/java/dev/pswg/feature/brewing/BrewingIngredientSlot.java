package dev.pswg.feature.brewing;

import dev.pswg.container.GadgetsItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public class BrewingIngredientSlot extends Slot
{
	public BrewingIngredientSlot(Container inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return MixerBrewingPaths.pathMap.containsKey(stack.getItem()) || stack.is(GadgetsItems.Tags.MIXER_FOOD_TAG) || stack.getItem() instanceof DyeItem;
	}
}
