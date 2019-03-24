package com.parzivail.swg.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileNpcSpawner;
import com.parzivail.util.block.PBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockNpcSpawner extends PBlockContainer
{
	public BlockNpcSpawner()
	{
		super("npcSpawner");
		setCreativeTab(StarWarsGalaxy.tab);
		setTextureName(Resources.modColon(name));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileNpcSpawner();
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
	{
		if (worldIn.isRemote)
			return true;
		else
		{
			TileNpcSpawner tile = (TileNpcSpawner)worldIn.getTileEntity(x, y, z);

			if (tile != null)
				player.openGui(StarWarsGalaxy.instance, Resources.GUI_NPC_SPAWNER, worldIn, x, y, z);

			return true;
		}
	}
}
