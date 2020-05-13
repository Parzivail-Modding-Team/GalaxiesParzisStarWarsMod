package com.parzivail.util.math;

import net.minecraft.util.math.Quaternion;

public interface QuaternionExt
{
	void set0(float x, float y, float z, float w);

	@SuppressWarnings("ConstantConditions")
	static QuaternionExt from(Quaternion self)
	{
		return (QuaternionExt)(Object)self;
	}
}
