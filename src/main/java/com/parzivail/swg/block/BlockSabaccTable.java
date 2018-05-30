package com.parzivail.swg.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileSabaccTable;
import com.parzivail.util.block.PBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSabaccTable extends PBlockContainer
{
	public BlockSabaccTable()
	{
		super("sabaccTable");
		setCreativeTab(StarWarsGalaxy.tab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileSabaccTable();
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
	{
		if (worldIn.isRemote)
			return true;
		else
		{
			TileSabaccTable tile = (TileSabaccTable)worldIn.getTileEntity(x, y, z);

			if (tile != null)
				player.openGui(StarWarsGalaxy.instance, Resources.GUI_SABACC_TABLE, worldIn, x, y, z);

			return true;
		}
	}
}
