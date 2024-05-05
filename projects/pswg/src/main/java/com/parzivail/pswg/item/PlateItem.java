package com.parzivail.pswg.item;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class PlateItem extends BlockItem
{
	public List<ItemStack> FOODS = new ArrayList<>();

	public PlateItem(Settings settings)
	{
		super(SwgBlocks.Misc.Plate, settings);
	}

	@Override
	public void postProcessNbt(NbtCompound nbt)
	{
		var max = nbt.getInt("food_amount");
		for (int i = 0; i < max; i++)
		{
			FOODS.add(i, ItemStack.fromNbt(nbt.getCompound("food" + i)));
		}
		super.postProcessNbt(nbt);
	}
}
