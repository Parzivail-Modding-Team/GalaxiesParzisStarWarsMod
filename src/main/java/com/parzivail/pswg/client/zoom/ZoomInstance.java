package com.parzivail.pswg.client.zoom;

import net.minecraft.util.Identifier;

/**
 * Stub class until a zoom implementation can be ported
 */
public class ZoomInstance
{
	private final Identifier instanceId;
	private final float modifier;
	private final SmoothTransitionMode transitionMode;
	private final ZoomDivisorMouseModifier mouseModifier;

	public ZoomInstance(Identifier instanceId, float modifier, SmoothTransitionMode transitionMode, ZoomDivisorMouseModifier mouseModifier)
	{

		this.instanceId = instanceId;
		this.modifier = modifier;
		this.transitionMode = transitionMode;
		this.mouseModifier = mouseModifier;
	}

	public void setZoom(boolean zoom)
	{
	}

	public void setZoomDivisor(double divisor)
	{
	}

	public void setZoomOverlay(ZoomOverlay overlay)
	{
	}

	public boolean isOverlayActive()
	{
		return false;
	}

	public SmoothTransitionMode getTransitionMode()
	{
		return transitionMode;
	}
}
