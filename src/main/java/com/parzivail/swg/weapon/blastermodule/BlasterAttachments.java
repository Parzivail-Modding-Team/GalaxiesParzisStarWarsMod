package com.parzivail.swg.weapon.blastermodule;

import com.parzivail.swg.weapon.blastermodule.barrel.BarrelDefault;
import com.parzivail.swg.weapon.blastermodule.barrel.BlasterBarrel;
import com.parzivail.swg.weapon.blastermodule.grip.BlasterGrip;
import com.parzivail.swg.weapon.blastermodule.grip.GripNone;
import com.parzivail.swg.weapon.blastermodule.scope.*;

import java.util.HashMap;

public class BlasterAttachments
{
	public static final HashMap<Integer, BlasterAttachment> ATTACHMENTS = new HashMap<>();

	public static BlasterScope scopeIronsights = new ScopeIronsights();
	public static BlasterGrip gripNone = new GripNone();
	public static BlasterBarrel barrelDefault = new BarrelDefault();

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
		ATTACHMENTS.put(attachment.getId(), attachment);
	}
}
