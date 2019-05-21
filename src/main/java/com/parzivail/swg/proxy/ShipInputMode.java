package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;

public enum ShipInputMode
{
	Yaw(Resources.guiDot("shipInputMode.yaw")),
	Roll(Resources.guiDot("shipInputMode.roll")),
	Landing(Resources.guiDot("shipInputMode.landing"));

	public final String langEntry;

	ShipInputMode(String langEntry)
	{
		this.langEntry = langEntry;
	}
}
