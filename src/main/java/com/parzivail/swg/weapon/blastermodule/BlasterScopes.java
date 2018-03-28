package com.parzivail.swg.weapon.blastermodule;

public class BlasterScopes
{
	private static final IBlasterScope[] SCOPES = {
			new ScopeRedDot(), new ScopeReflex(), new ScopeRingReticle(), new ScopeAcog(), new ScopeSpitfire()
	};
	public static final int NUM_SCOPES = SCOPES.length;

	public static IBlasterScope getScope(int id)
	{
		if (id < 0 || id > SCOPES.length)
			return null;
		return SCOPES[id];
	}
}
