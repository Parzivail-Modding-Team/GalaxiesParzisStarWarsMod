package com.parzivail.pswg.mixin;

import com.parzivail.util.math.QuaternionExt;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Quaternion.class)
public class QuaternionMixin implements QuaternionExt
{
	@Shadow
	private float a;
	@Shadow
	private float b;
	@Shadow
	private float c;
	@Shadow
	private float d;

	@Override
	public void set0(float b, float c, float d, float a)
	{
		this.b = b;
		this.c = c;
		this.d = d;
		this.a = a;
	}
}
