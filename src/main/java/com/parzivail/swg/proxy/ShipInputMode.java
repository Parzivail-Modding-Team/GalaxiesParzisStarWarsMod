package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;

public enum ShipInputMode
{
	YawCentric(Resources.guiDot("shipInputMode.yaw")),
	RollCentric(Resources.guiDot("shipInputMode.roll")),
	Repulsor(Resources.guiDot("shipInputMode.landing"));

	public final String langEntry;

	ShipInputMode(String langEntry)
	{
		this.langEntry = langEntry;
	}
}
