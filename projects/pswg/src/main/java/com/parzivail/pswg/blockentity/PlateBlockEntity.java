package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.block.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PlateBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
	public List<ItemStack> FOODS = new ArrayList<>();
	int foodCount = 0;

	public PlateBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Misc.PlateBlockEntityType, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("food_amount", FOODS.size());
		for (int i = 0; i < FOODS.size(); i++)
			nbt.put("food" + i, this.FOODS.get(i).writeNbt(new NbtCompound()));
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		for (int i = 0; i < nbt.getInt("food_amount"); i++)
			this.FOODS.add(i, ItemStack.fromNbt(nbt.getCompound("food" + i)));
		super.readNbt(nbt);
	}

	public void addFood(ItemStack stack)
	{
		FOODS.add(stack);
	}

	public void takeFood(PlayerEntity player)
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
	public void eatFood(PlayerEntity player){
		if (!this.FOODS.isEmpty())
		{
			if (!getWorld().isClient)
			{
				var stack = FOODS.get(FOODS.size() - 1);
				player.getHungerManager().add(stack.getItem().getFoodComponent().getHunger(), stack.getItem().getFoodComponent().getSaturationModifier());
			}
			FOODS.remove(FOODS.size()-1);
		}
	}

	@Override
	public void fromClientTag(NbtCompound nbt)
	{
		for (int i = 0; i < nbt.getInt("food_amount"); i++)
			this.FOODS.add(i, ItemStack.fromNbt(nbt.getCompound("food" + i)));
	}

	@Override
	public NbtCompound toClientTag(NbtCompound nbt)
	{
		nbt.putInt("food_amount", FOODS.size());
		for (int i = 0; i < FOODS.size(); i++)
			nbt.put("food" + i, this.FOODS.get(i).writeNbt(new NbtCompound()));
		return nbt;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return toClientTag(super.toInitialChunkDataNbt());
	}

	@Override
	public Identifier getSyncPacketId()
	{
		return SwgPackets.S2C.SyncBlockToClient;
	}
}
