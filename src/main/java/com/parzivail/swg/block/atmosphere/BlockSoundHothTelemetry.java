package com.parzivail.swg.block.atmosphere;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.atmosphere.TileAtmoSound;
import com.parzivail.swg.tile.atmosphere.TileSoundHothTelemetry;
import com.parzivail.util.block.PBlockContainer;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSoundHothTelemetry extends PBlockContainer
{
	public BlockSoundHothTelemetry()
	{
		super("soundHothTelemetry");
		this.setCreativeTab(StarWarsGalaxy.tab);
		this.setAlpha();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileSoundHothTelemetry();
	}

	@Override
	public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
	{
		super.breakBlock(worldIn, x, y, z, blockBroken, meta);

		TileEntity te = worldIn.getTileEntity(x, y, z);
		if (te instanceof TileAtmoSound)
			((TileAtmoSound)te).stopSound();
	}
}
