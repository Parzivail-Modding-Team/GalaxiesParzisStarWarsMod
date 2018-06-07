package com.parzivail.swg.block.light;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.light.TileFloorLight;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.TileEntityRotate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockFloorLight extends PBlockContainer
{
	public BlockFloorLight()
	{
		super("floorLight", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.5F, 0.8F);
		setHardness(50.0F);
		setLightLevel(1);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileFloorLight();
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
	public void registerIcons(IIconRegister icon)
	{
		blockIcon = icon.registerIcon(Resources.MODID + ":" + "blank");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityRotate)
		{
			TileEntityRotate te = (TileEntityRotate)tile;
			int l = MathHelper.floor_double(player.rotationYaw * 8.0F / 360.0F + 0.5D) & 0x7;
			te.setFacing(l);
		}
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
