package com.parzivail.pswg.item;

import com.parzivail.pswg.blockentity.PowerCouplingBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.client.TooltipUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CableItem extends Item
{
	public CableItem(Item.Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		var nbt = stack.getOrCreateNbt();
		return nbt.contains("source");
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);

		var nbt = stack.getOrCreateNbt();
		if (nbt.contains("source"))
		{
			var source = nbt.getCompound("source");

			var sourceX = source.getInt("x");
			var sourceY = source.getInt("y");
			var sourceZ = source.getInt("z");

			tooltip.add(TooltipUtil.getStatus(this, sourceX, sourceY, sourceZ));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		var world = context.getWorld();
		var blockPos = context.getBlockPos();
		var blockState = world.getBlockState(blockPos);
		if (blockState.isOf(SwgBlocks.Power.Coupling))
		{
			var playerEntity = context.getPlayer();
			if (!world.isClient && playerEntity != null)
			{
				var nbt = context.getStack().getOrCreateNbt();
				if (nbt.contains("source"))
				{
					var source = nbt.getCompound("source");

					var sourceX = source.getInt("x");
					var sourceY = source.getInt("y");
					var sourceZ = source.getInt("z");
					var sourcePos = new BlockPos(sourceX, sourceY, sourceZ);

					if (!blockPos.equals(sourcePos) && world.getBlockEntity(sourcePos) instanceof PowerCouplingBlockEntity pcbe)
						pcbe.attachTo(world, blockPos);

					nbt.remove("source");
				}
				else
				{
					var e = new NbtCompound();
					e.putInt("x", blockPos.getX());
					e.putInt("y", blockPos.getY());
					e.putInt("z", blockPos.getZ());
					nbt.put("source", e);
				}
			}

			return ActionResult.success(world.isClient);
		}
		else
		{
			return ActionResult.PASS;
		}
	}
}
