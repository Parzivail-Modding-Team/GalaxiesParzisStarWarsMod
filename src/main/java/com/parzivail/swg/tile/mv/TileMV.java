package com.parzivail.swg.tile.mv;

import com.parzivail.util.block.TileEntityRotate;

public class TileMV extends TileEntityRotate
{
	public int frame;

	public TileMV()
	{
		this.frame = 0;
	}

	@Override
	public void updateEntity()
	{
		this.frame++;
	}
}
