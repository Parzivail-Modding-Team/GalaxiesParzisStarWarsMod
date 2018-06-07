package com.parzivail.swg.block.light;


import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.light.TileEntityHothCeilingLight;
import com.parzivail.util.block.HarvestLevel;
import com.parzivail.util.block.PBlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockHothCeilingLight extends PBlockContainer
{
	public BlockHothCeilingLight()
	{
		super("hothCeilingLight", Material.iron);
		setCreativeTab(StarWarsGalaxy.tab);
		setBlockBounds(0.4f, 0.8f, 0.4f, 0.6f, 1, 0.6f);
		setHardness(50.0F);
		setLightLevel(1);
		this.setHarvestLevel("pickaxe", HarvestLevel.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityHothCeilingLight();
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
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
