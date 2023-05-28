package com.parzivail.pswg.api;

public enum RegisterResult
{
	Pass(true),
	Cancel(false);

	private final boolean shouldRegister;

	RegisterResult(boolean shouldRegister)
	{
		this.shouldRegister = shouldRegister;
	}

	public boolean shouldRegister()
	{
		return shouldRegister;
	}
}
