package com.parzivail.util.ui;

public class AnimatedValue
{
	private float previous;
	private float next;

	private long nextTime;

	public AnimatedValue(float value)
	{
		this.previous = value;
		this.next = value;
	}

	public float animateToward(float value, float msToTake)
	{
		long timeHere = Fx.Util.GetMillis();

		if (value != next)
		{
			previous = next;
			next = value;

			nextTime = (long)(timeHere + msToTake);
		}

		if (timeHere > nextTime)
			return next;

		long timeDiff = (nextTime - timeHere);
		float timeLerp = timeDiff / msToTake;

		return next * (1 - timeLerp) + previous * timeLerp;
	}
}
