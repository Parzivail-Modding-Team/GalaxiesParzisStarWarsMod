package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.swg.weapon.blastermodule.scope.*;

import java.util.ArrayList;

public class BlasterAttachments
{
	public static final ArrayList<BlasterAttachment> ATTACHMENTS = new ArrayList<>();

	static
	{
		register(new ScopeRedDot());
		register(new ScopeReflex());
		register(new ScopeRingReticle());
		register(new ScopeAcog());
		register(new ScopeSpitfire());
	}

	private static void register(BlasterAttachment attachment)
	{
		ATTACHMENTS.add(attachment);
	}
}
