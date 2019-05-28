package com.parzivail.util.client;

import net.minecraft.client.settings.KeyBinding;

public class InterceptingKeyBinding extends KeyBinding
{
	private boolean isIntercepting;

	public InterceptingKeyBinding(KeyBinding keyBinding)
	{
		super(keyBinding.getKeyDescription(), keyBinding.getKeyCode(), keyBinding.getKeyCategory());
	}

	public boolean isIntercepting()
	{
		return isIntercepting;
	}

	public void setIntercepting(boolean intercepting)
	{
		isIntercepting = intercepting;
	}

	@Override
	public boolean isKeyDown()
	{
		return !isIntercepting && super.isKeyDown();
	}

	@Override
	public boolean isPressed()
	{
		return !isIntercepting && super.isPressed();
	}

	public boolean getInterceptedIsKeyDown()
	{
		return super.isKeyDown();
	}

	public boolean interceptedIsPressed()
	{
		return isIntercepting && super.isPressed();
	}
}
