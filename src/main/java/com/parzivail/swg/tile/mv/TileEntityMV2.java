package com.parzivail.swg.tile.mv;

import com.parzivail.util.block.TileEntityRotate;

public class TileEntityMV2 extends TileEntityRotate
{
	public int frame;

	public TileEntityMV2()
	{
		this.frame = 0;
	}

	@Override
	public void updateEntity()
	{
		this.frame++;
	}
}
