package com.parzivail.pswg.mixin;

import com.parzivail.util.math.Matrix4fExt;
import net.minecraft.client.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements Matrix4fExt
{
	@Shadow
	protected float a00;

	@Shadow
	protected float a01;

	@Shadow
	protected float a02;

	@Shadow
	protected float a03;

	@Shadow
	protected float a10;

	@Shadow
	protected float a11;

	@Shadow
	protected float a12;

	@Shadow
	protected float a13;

	@Shadow
	protected float a20;

	@Shadow
	protected float a21;

	@Shadow
	protected float a22;

	@Shadow
	protected float a23;

	@Shadow
	protected float a30;

	@Shadow
	protected float a31;

	@Shadow
	protected float a32;

	@Shadow
	protected float a33;

	@Override
	public void set(float a00, float a01, float a02, float a03, float a10, float a11, float a12, float a13, float a20, float a21, float a22, float a23, float a30, float a31, float a32, float a33)
	{
		this.a00 = a00;
		this.a01 = a01;
		this.a02 = a02;
		this.a03 = a03;
		this.a10 = a10;
		this.a11 = a11;
		this.a12 = a12;
		this.a13 = a13;
		this.a20 = a20;
		this.a21 = a21;
		this.a22 = a22;
		this.a23 = a23;
		this.a30 = a30;
		this.a31 = a31;
		this.a32 = a32;
		this.a33 = a33;
	}
}
