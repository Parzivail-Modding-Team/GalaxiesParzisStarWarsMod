package com.parzivail.util.common;

import com.parzivail.util.math.Ease;
import com.parzivail.util.ui.Fx;

import java.util.function.Function;

public class AnimatedValue
{
	private final int msToTake;
	private float previous;
	private float next;
	private long nextTime;

	public AnimatedValue(float value, int msToTake)
	{
		previous = value;
		next = value;
		this.msToTake = msToTake;
	}

	public float animateTo(float value)
	{
		return animateTo(value, Ease::linear);
	}

	public float animateTo(float value, Function<Float, Float> interpolation)
	{
		queueAnimatingTo(value);
		return getValue(interpolation);
	}

	public void queueAnimatingTo(float value)
	{
		long timeHere = Fx.Util.GetMillis();

		if (value != next)
		{
			previous = next;
			next = value;

			nextTime = timeHere + msToTake;
		}
	}

	public float getValue()
	{
		return getValue(Ease::linear);
	}

	public float getValue(Function<Float, Float> interpolation)
	{
		long timeHere = Fx.Util.GetMillis();

		if (timeHere > nextTime)
			return next;

		long timeDiff = (nextTime - timeHere);
		float timeLerp = interpolation.apply(timeDiff / (float)msToTake);

		return next * (1 - timeLerp) + previous * timeLerp;
	}
}
