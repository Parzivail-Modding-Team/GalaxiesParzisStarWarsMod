package com.parzivail.util.item;

import com.parzivail.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PItemBlockSubcardinalDecoration extends PItemBlockStaticDecoration
{
	public PItemBlockSubcardinalDecoration(Block block)
	{
		super(block);
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

		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}
}
