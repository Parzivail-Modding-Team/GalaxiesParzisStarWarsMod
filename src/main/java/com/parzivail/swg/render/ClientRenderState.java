package com.parzivail.swg.render;

import net.minecraft.item.Item;

import java.util.EnumSet;
import java.util.HashMap;

public enum ClientRenderState
{
	None, SniperThermal;

	public static EnumSet<ClientRenderState> renderState = EnumSet.of(None);
	public static HashMap<Class<? extends Item>, ClientRenderState> renderStateRequest;

	static
	{
		renderStateRequest = new HashMap<>();
		//renderStateRequest.put(ItemSlugRifle.class, SniperThermal);
	}
}
