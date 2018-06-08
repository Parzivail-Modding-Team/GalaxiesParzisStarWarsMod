package com.parzivail.swg.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileLadder;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends PBlockContainer
{
	public BlockLadder()
	{
		// NOTE: make model dynamic (block on top is right angle only)
		super("ladder", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileLadder();
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
	{
		return true;
	}

	@Override
	public void registerIcons(IIconRegister icon)
	{
		blockIcon = icon.registerIcon(Resources.MODID + ":" + "blank");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileRotatable)
		{
			TileRotatable te = (TileRotatable)tile;
			int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
			te.setFacing(l);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		if (p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_) instanceof TileRotatable)
		{
			int meta = (int)((TileRotatable)p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_)).getFacing();
			if (meta % 2 == 0)
				setBlockBounds(0, 0, 0.4f, 1, 1, 0.6f);
			else
				setBlockBounds(0.4f, 0, 0, 0.6f, 1, 1);
		}
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
