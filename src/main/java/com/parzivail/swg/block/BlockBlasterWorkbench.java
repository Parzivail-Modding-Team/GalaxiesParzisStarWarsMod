package com.parzivail.swg.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.util.block.PBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBlasterWorkbench extends PBlockContainer
{
	public BlockBlasterWorkbench()
	{
		super("blasterWorkbench");
		setCreativeTab(StarWarsGalaxy.tab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileBlasterWorkbench();
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
	{
		if (worldIn.isRemote)
			return true;
		else
		{
			TileBlasterWorkbench tile = (TileBlasterWorkbench)worldIn.getTileEntity(x, y, z);

			if (tile != null)
				player.openGui(StarWarsGalaxy.instance, Resources.GUI_BLASTER_WORKBENCH, worldIn, x, y, z);

			return true;
		}
	}
}
