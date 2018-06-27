package com.parzivail.swg.block;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileGunRack;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.TileRotatable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGunRack extends PBlockContainer
{
	public BlockGunRack()
	{
		super("gunRack", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setHardness(50.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileGunRack();
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

	// TODO
	//	@Override
	//	public void breakBlock(World world, int x, int y, int z, Block block, int wut)
	//	{
	//		TileGunRack rack = (TileGunRack)world.getTileEntity(x, y, z);
	//		if (rack != null)
	//		{
	//			for (ItemStack gun : rack.getGuns())
	//			{
	//				if (gun != null)
	//				{
	//					EntityItem entityitem = new EntityItem(world, x, y, z, gun);
	//					if (gun.hasTagCompound())
	//						entityitem.getEntityItem().setTagCompound((NBTTagCompound)gun.getTagCompound().copy());
	//					world.spawnEntityInWorld(entityitem);
	//				}
	//			}
	//			world.func_147453_f(x, y, z, block);
	//		}
	//		super.breakBlock(world, x, y, z, block, wut);
	//	}
	//
	//	@Override
	//	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sX, float sY, float sZ)
	//	{
	//		if (!world.isRemote)
	//		{
	//			if (player.getHeldItem() == null)
	//			{
	//				TileGunRack gunRack = (TileGunRack)world.getTileEntity(x, y, z);
	//				ItemStack itemStack = gunRack.popGun();
	//				player.inventory.mainInventory[player.inventory.currentItem] = itemStack;
	//			}
	//			else if (player.getHeldItem().getItem() instanceof ItemBlasterRifle || player.getHeldItem().getItem() instanceof ItemBlasterHeavy)
	//			{
	//				if (!(player.getHeldItem().getItemDamage() == 2 && player.getHeldItem().getItem() instanceof ItemBlasterRifle))
	//				{
	//					TileGunRack gunRack = (TileGunRack)world.getTileEntity(x, y, z);
	//					if (gunRack.pushGun(player.getHeldItem()))
	//						player.inventory.mainInventory[player.inventory.currentItem] = null;
	//				}
	//			}
	//		}
	//		world.markBlockForUpdate(x, y, z);
	//		return true;
	//	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		if (p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_) instanceof TileRotatable)
		{
			int meta = (int)((TileRotatable)p_149719_1_.getTileEntity(p_149719_2_, p_149719_3_, p_149719_4_)).getFacing();
			if (meta % 2 == 0)
				setBlockBounds(0, 0, 0.2f, 1, 1.3f, 0.8f);
			else
				setBlockBounds(0.2f, 0, 0, 0.8f, 1.3f, 1);
		}
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
