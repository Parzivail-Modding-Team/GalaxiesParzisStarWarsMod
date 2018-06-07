package com.parzivail.swg.tile.mv;

import com.parzivail.util.block.TileEntityRotate;

public class TileMV2 extends TileEntityRotate
{
	public int frame;

	public TileMV2()
	{
		this.frame = 0;
	}

	@Override
	public void updateEntity()
	{
		this.frame++;
	}
}
