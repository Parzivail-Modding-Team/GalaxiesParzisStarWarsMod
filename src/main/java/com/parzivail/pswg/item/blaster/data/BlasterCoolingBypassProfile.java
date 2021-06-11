package com.parzivail.pswg.item.blaster.data;

import com.parzivail.pswg.Resources;
import net.minecraft.nbt.NbtCompound;
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

	public static BlasterCoolingBypassProfile fromTag(NbtCompound compoundTag, String s)
	{
		NbtCompound tag = compoundTag.getCompound(s);

		return new BlasterCoolingBypassProfile(
				tag.getFloat("primaryBypassTime"),
				tag.getFloat("primaryBypassTolerance"),
				tag.getFloat("secondaryBypassTime"),
				tag.getFloat("secondaryBypassTolerance")
		);
	}

	public static void toTag(NbtCompound compoundTag, String s, BlasterCoolingBypassProfile data)
	{
		NbtCompound tag = new NbtCompound();

		tag.putFloat("primaryBypassTime", data.primaryBypassTime);
		tag.putFloat("primaryBypassTolerance", data.primaryBypassTolerance);
		tag.putFloat("secondaryBypassTime", data.secondaryBypassTime);
		tag.putFloat("secondaryBypassTolerance", data.secondaryBypassTolerance);

		compoundTag.put(s, tag);
	}
}
