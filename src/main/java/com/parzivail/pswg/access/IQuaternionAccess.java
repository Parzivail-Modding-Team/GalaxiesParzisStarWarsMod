package com.parzivail.pswg.access;

import net.minecraft.util.math.Quaternion;

public interface IQuaternionAccess
{
	void set0(float x, float y, float z, float w);

	@SuppressWarnings("ConstantConditions")
	static IQuaternionAccess from(Quaternion self)
	{
		return (IQuaternionAccess)(Object)self;
	}
}
