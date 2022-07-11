package com.parzivail.pswg.item.blaster.data;

import com.parzivail.pswg.Resources;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class BlasterCoolingBypassProfile
{
	public static final HashMap<Identifier, BlasterCoolingBypassProfile> PROFILES = new HashMap<>();

	private static BlasterCoolingBypassProfile of(Identifier identifier, BlasterCoolingBypassProfile profile)
	{
		PROFILES.put(identifier, profile);
		return profile;
	}

	public static final BlasterCoolingBypassProfile NONE = of(Resources.id("none"), new BlasterCoolingBypassProfile(0, 0, 0, 0));
	public static final BlasterCoolingBypassProfile DEFAULT = of(Resources.id("default"), new BlasterCoolingBypassProfile(0.7f, 0.05f, 0.3f, 0.1f));

	public float primaryBypassTime;
	public float primaryBypassTolerance;
	public float secondaryBypassTime;
	public float secondaryBypassTolerance;

	public BlasterCoolingBypassProfile(float primaryBypassTime, float primaryBypassTolerance, float secondaryBypassTime, float secondaryBypassTolerance)
	{
		this.primaryBypassTime = primaryBypassTime;
		this.primaryBypassTolerance = primaryBypassTolerance;
		this.secondaryBypassTime = secondaryBypassTime;
		this.secondaryBypassTolerance = secondaryBypassTolerance;
	}

	public static BlasterCoolingBypassProfile read(PacketByteBuf buf)
	{
		var primaryBypassTime = buf.readFloat();
		var primaryBypassTolerance = buf.readFloat();
		var secondaryBypassTime = buf.readFloat();
		var secondaryBypassTolerance = buf.readFloat();
		return new BlasterCoolingBypassProfile(primaryBypassTime, primaryBypassTolerance, secondaryBypassTime, secondaryBypassTolerance);
	}

	public void write(PacketByteBuf buf)
	{
		buf.writeFloat(primaryBypassTime);
		buf.writeFloat(primaryBypassTolerance);
		buf.writeFloat(secondaryBypassTime);
		buf.writeFloat(secondaryBypassTolerance);
	}
}
