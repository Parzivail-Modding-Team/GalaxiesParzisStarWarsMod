package com.parzivail.swg.tile.light;

import com.parzivail.util.block.TileRotatable;

public class TileWallIndicator extends TileRotatable
{
	public int frame;

	public TileWallIndicator()
	{
		frame = 0;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		frame++;
	}
}
