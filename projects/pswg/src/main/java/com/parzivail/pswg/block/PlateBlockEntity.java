package com.parzivail.pswg.block;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PlateBlockEntity extends BlockEntity
{
	public static int FOOD_AMOUNT;
	public List<ItemStack> FOODS = new ArrayList<>();

	public PlateBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Misc.PlateBlockEntityType, pos, state);
		FOOD_AMOUNT = 0;
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		nbt.putInt("food_amount", FOODS.size());
		for (int i = 0; i < FOODS.size(); i++)
		{
			nbt.put("Food" + i, this.FOODS.get(i).getNbt());
		}
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		FOOD_AMOUNT = nbt.getInt("food_amount");
		for (int i = 0; i < FOOD_AMOUNT; i++)
		{
			this.FOODS.set(i, ItemStack.fromNbt(nbt.getCompound("Food" + i)));
		}
	}

	public void addFood(ItemStack stack)
	{
		FOODS.add(stack);
		FOOD_AMOUNT++;
	}

	public void removeFood(PlayerEntity player)
	{
		player.giveItemStack(FOODS.get(FOODS.size() - 1));
		FOODS.remove(FOODS.size() - 1);
		FOOD_AMOUNT--;
	}
}
