package com.parzivail.aurek.util;

import net.minecraft.util.math.MathHelper;

// Based on net.coderbot.iris.uniforms.transforms.SmoothedFloat
public class AnimatedFloat
{
	private static final double LN_OF_2 = Math.log(2.0);

	private final float epsilon;
	private float target;

	private float previousValue;
	private float value;

	private final float decayConstant;

	public AnimatedFloat(float halfLife, float epsilon, float value)
	{
		this.decayConstant = (float)(1.0f / (halfLife / LN_OF_2));
		this.epsilon = epsilon;

		this.target = value;
		this.value = value;
		this.previousValue = value;
	}

	public void tick()
	{
		this.previousValue = value;

		if (this.value == this.target)
			return;

		float smoothingFactor = (float)(1.0 - Math.exp(-this.decayConstant));
		this.value = MathHelper.lerp(smoothingFactor, this.value, this.target);
		if (Math.abs(this.value - this.target) < this.epsilon)
			this.value = this.target;
	}

	public void setTarget(float target)
	{
		this.target = target;
	}

	public float getTarget()
	{
		return target;
	}

	public void setValue(float value)
	{
		this.target = value;
		this.value = value;
		this.previousValue = value;
	}

	public float getValue()
	{
		return value;
	}

	public float getValue(float tickDelta)
	{
		return MathHelper.lerp(tickDelta, previousValue, value);
	}
}
