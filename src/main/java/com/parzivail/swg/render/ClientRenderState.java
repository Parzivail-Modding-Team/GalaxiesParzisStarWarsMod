package com.parzivail.swg.render;

import java.util.EnumSet;

public enum ClientRenderState
{
	None, SniperThermal;

	public static EnumSet<ClientRenderState> renderState = EnumSet.of(None);
}
