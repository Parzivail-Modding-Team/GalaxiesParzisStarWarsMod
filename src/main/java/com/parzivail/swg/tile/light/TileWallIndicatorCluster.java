package com.parzivail.swg.tile.light;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileRotatable;
import com.parzivail.util.math.MathUtil;

public class TileWallIndicatorCluster extends TileRotatable
{
	public int color1 = 0;
	public int color2 = 0;
	public int color3 = 0;

	@Override
	public void updateEntity()
	{
		if (MathUtil.oneIn(20))
			color1 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(110))
			color2 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(70))
			color3 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
	}
}
