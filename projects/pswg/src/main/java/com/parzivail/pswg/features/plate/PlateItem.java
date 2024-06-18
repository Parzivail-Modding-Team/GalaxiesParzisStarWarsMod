package com.parzivail.pswg.features.plate;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.item.IDefaultNbtProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlateItem extends BlockItem implements IDefaultNbtProvider
{

	public PlateItem(Settings settings)
	{
		super(SwgBlocks.Misc.Plate, settings);
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
	{

		if (world.getBlockEntity(pos) instanceof PlateBlockEntity plateBlockEntity)
		{
			var foodAmount = stack.getNbt().getInt("food_amount");
			for (int i = 0; i < foodAmount; i++)
				plateBlockEntity.FOODS.add(i, ItemStack.fromNbt(stack.getNbt().getCompound("food" + i)));
			world.setBlockState(pos, world.getBlockState(pos).with(PlateBlock.FOOD_VALUE, calculateFood(plateBlockEntity.FOODS)));
		}

		return super.postPlacement(pos, world, player, stack, state);
	}

	private int calculateFood(List<ItemStack> foodList)
	{
		int sT = 0;
		int layerT = 0;
		for (int i = 0; i < foodList.size(); i++)
		{
			if (foodList.get(i).isIn(SwgTags.Items.MAIN_COURSE))
				sT += (5 - layerT);
			else
				sT++;
			if (sT == getLayerMaxPosition(layerT))
				layerT++;
		}
		return sT;
	}

	private int getLayerMaxPosition(int layer)
	{
		int s = 0;
		for (int i = 0; i <= layer; i++)
			s += (5 - i);
		return s;
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new PlateItemTag().toSubtag();
	}
}
