package com.parzivail.pswg.mixin;

import com.parzivail.util.math.QuaternionExt;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Quaternion.class)
public class QuaternionMixin implements QuaternionExt
{
	@Shadow
	private float x;
	@Shadow
	private float y;
	@Shadow
	private float z;
	@Shadow
	private float w;

	@Override
	public void set0(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
}
