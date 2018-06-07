package com.parzivail.swg.block.light;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.light.TileHothCeilingLight2;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.TileEntityRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHothCeilingLight2 extends PBlockContainer
{
	public BlockHothCeilingLight2()
	{
		super("hothCeilingLight2", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setLightLevel(1);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileHothCeilingLight2();
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityRotate)
		{
			TileEntityRotate te = (TileEntityRotate)tile;
			int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
			te.setFacing(l);
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		setBlockBounds(0, 0.5f, 0, 1, 1, 1);
	}

	@Override
	public void registerIcons(IIconRegister icon)
	{
		blockIcon = icon.registerIcon(Resources.MODID + ":" + "blank");
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
