package com.parzivail.swg.tile.mv;

import com.parzivail.util.block.TileRotatable;

public class TileMV2 extends TileRotatable
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
