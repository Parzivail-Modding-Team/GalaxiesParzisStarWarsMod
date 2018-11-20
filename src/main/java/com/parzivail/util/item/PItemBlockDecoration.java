package com.parzivail.util.item;

import com.parzivail.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PItemBlockDecoration extends ItemBlock
{
	public PItemBlockDecoration(Block block)
	{
		super(block);
		maxStackSize = 64;
	}

	/**
	 * Called to actually place the block, after the location is determined
	 * and all permission checks have been made.
	 *
	 * @param stack  The item stack that was used to place the block. This can be changed inside the method.
	 * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
	 * @param side   The side the player (or machine) right-clicked on.
	 */
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		int l = (int)(MathUtil.roundToNearest(player.rotationYaw % 360, 45) / 45f);
		if (l < 0)
			l += 8;
		metadata = l;

		if (!world.setBlock(x, y, z, blockInstance, metadata, 3))
			return false;

		if (world.getBlock(x, y, z) == blockInstance)
		{
			blockInstance.onBlockPlacedBy(world, x, y, z, player, stack);
			blockInstance.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}
}
