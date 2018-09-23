package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class PBlockLayer extends Block
{
	public final String name;

	public PBlockLayer(String name)
	{
		super(Material.snow);
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
		setTextureName(Resources.modColon(name));
		setHardness(0.5F);
		setStepSound(Block.soundTypeSnow);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		func_150154_b(0);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		int l = worldIn.getBlockMetadata(x, y, z) & 7;
		float f = 0.125F;
		return AxisAlignedBB.getBoundingBox((double)x + minX, (double)y + minY, (double)z + minZ, (double)x + maxX, (double)((float)y + (float)l * f), (double)z + maxZ);
	}

	public boolean isOpaqueCube()
	{
		return false;
	}

	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender()
	{
		func_150154_b(0);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z)
	{
		func_150154_b(worldIn.getBlockMetadata(x, y, z));
	}

	protected void func_150154_b(int p_150154_1_)
	{
		int j = p_150154_1_ & 7;
		float f = (float)(2 * (1 + j)) / 16.0F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
	{
		Block block = worldIn.getBlock(x, y - 1, z);
		return (block == this && (worldIn.getBlockMetadata(x, y - 1, z) & 7) == 7 || block.isOpaqueCube() && block.getMaterial().blocksMovement());
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		if (!canPlaceBlockAt(world, x, y, z))
			world.setBlockToAir(x, y, z);
	}

	public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta)
	{
		super.harvestBlock(worldIn, player, x, y, z, meta);
		worldIn.setBlockToAir(x, y, z);
	}

	public Item getItemDropped(int meta, Random random, int fortune)
	{
		return Items.snowball;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random)
	{
		return 1;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	public void updateTick(World worldIn, int x, int y, int z, Random random)
	{
		if (worldIn.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11)
			worldIn.setBlockToAir(x, y, z);
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side)
	{
		return side == 1 || super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	/**
	 * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
	 * version in 1.1.
	 *
	 * @param meta    Blocks Metadata
	 * @param fortune Current item fortune level
	 * @param random  Random number generator
	 *
	 * @return The number of items to drop
	 */
	public int quantityDropped(int meta, int fortune, Random random)
	{
		return (meta & 7) + 1;
	}

	/**
	 * Determines if a new block can be replace the space occupied by this one,
	 * Used in the player's placement code to make the block act like water, and lava.
	 *
	 * @param world The current world
	 * @param x     X Position
	 * @param y     Y position
	 * @param z     Z position
	 *
	 * @return True if the block is replaceable by another block
	 */
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return meta < 7 && blockMaterial.isReplaceable();
	}
}
