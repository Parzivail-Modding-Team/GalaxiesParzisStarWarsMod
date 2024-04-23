package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PlateBlockEntity extends BlockEntity
{
	public List<ItemStack> FOODS = new ArrayList<>();

	public PlateBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Misc.PlateBlockEntityType, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("food_amount", FOODS.size());
		for (int i = 0; i < FOODS.size(); i++)
			nbt.put("food" + i, this.FOODS.get(i).writeNbt(new NbtCompound()));
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		for (int i = 0; i < nbt.getInt("food_amount"); i++)
		{
			this.FOODS.add(i, ItemStack.fromNbt(nbt.getCompound("food" + i)));
		}
	}

	public void addFood(ItemStack stack)
	{
		FOODS.add(stack);
	}

	public void removeFood(PlayerEntity player)
	{
		if (!this.FOODS.isEmpty())
		{
			if (getWorld() instanceof ServerWorld)
			{
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
				serverPlayer.getInventory().insertStack(FOODS.get(FOODS.size() - 1));
			}
			FOODS.remove(FOODS.size() - 1);
		}

	}
}
