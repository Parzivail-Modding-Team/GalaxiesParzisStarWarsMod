package com.parzivail.pswg.mixin;

import com.parzivail.util.math.Matrix4fExt;
import net.minecraft.util.math.Matrix4f;
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

	public float getM00()
	{
		return this.a00;
	}

	public float getM01()
	{
		return this.a01;
	}

	public float getM02()
	{
		return this.a02;
	}

	public float getM03()
	{
		return this.a03;
	}

	public float getM10()
	{
		return this.a10;
	}

	public float getM11()
	{
		return this.a11;
	}

	public float getM12()
	{
		return this.a12;
	}

	public float getM13()
	{
		return this.a13;
	}

	public float getM20()
	{
		return this.a20;
	}

	public float getM21()
	{
		return this.a21;
	}

	public float getM22()
	{
		return this.a22;
	}

	public float getM23()
	{
		return this.a23;
	}

	public float getM30()
	{
		return this.a30;
	}

	public float getM31()
	{
		return this.a31;
	}

	public float getM32()
	{
		return this.a32;
	}

	public float getM33()
	{
		return this.a33;
	}
}
