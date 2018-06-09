package com.parzivail.swg.block.antenna;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.antenna.TileSatelliteDish;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockRotate;
import com.parzivail.util.block.Tile2DofGimbal;
import com.parzivail.util.math.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSatelliteDish extends PBlockRotate
{
	public BlockSatelliteDish()
	{
		super("satelliteDish", Material.iron, 8);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5f, 1.0F);
		setHardness(50.0F);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
	{
		super.onBlockPlacedBy(world, x, y, z, player, item);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof Tile2DofGimbal)
		{
			Tile2DofGimbal vap = (Tile2DofGimbal)tile;
			float l = MathUtil.roundToNearest((-90 + player.rotationPitch) % 180, 360f / (snapAngles * 2)) / 90f;
			vap.setPitch(l);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileSatelliteDish();
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
