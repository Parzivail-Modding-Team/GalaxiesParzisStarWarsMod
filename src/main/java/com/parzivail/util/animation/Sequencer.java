package com.parzivail.util.animation;

public class Sequencer
{
	private Animatable[] track;
	private int length;
	private long startTime;
	private long prevTime;
	private int position;

	public SequenceDirection direction;
	public SequenceEndBehavior endBehavior;
	public boolean playing;

	public Sequencer(Animatable... sequence)
	{
		track = sequence;
		direction = SequenceDirection.Forward;
		endBehavior = SequenceEndBehavior.StopAndReset;
		playing = false;

		computeLength();
	}

	public void tick()
	{
		long t = System.currentTimeMillis();
		long dt = (t - prevTime);

		switch (direction)
		{
			case Forward:
				position += dt;
				break;
			case Reverse:
				position -= dt;
				break;
		}

		checkEnds();

		int l = 0;
		for (Animatable a : track)
		{
			if (l + a.length > position)
			{
				a.tick((position - l) / (float)a.length);
				break;
			}

			l += a.length;
		}

		prevTime = t;
	}

	public void play()
	{
		startTime = prevTime = System.currentTimeMillis();
		playing = true;
	}

	private void stop()
	{
		playing = false;
	}

	public void reset()
	{
		switch (direction)
		{
			case Forward:
				position = 0;
				break;
			case Reverse:
				position = length;
				break;
		}
	}

	public Animatable getCurrentAnimatable()
	{
		int l = 0;
		for (Animatable a : track)
		{
			if (l + a.length > position)
				return a;
			l += a.length;
		}
		return null;
	}

	public int getLength()
	{
		return length;
	}

	public SequenceDirection getDirection()
	{
		return direction;
	}

	public SequenceEndBehavior getEndBehavior()
	{
		return endBehavior;
	}

	public void setDirection(SequenceDirection direction)
	{
		this.direction = direction;
	}

	public void setEndBehavior(SequenceEndBehavior endBehavior)
	{
		this.endBehavior = endBehavior;
	}

	private void computeLength()
	{
		length = 0;
		for (Animatable a : track)
			length += a.length;
	}

	private void checkEnds()
	{
		if (position <= length)
			return;

		switch (endBehavior)
		{
			case Loop:
				reset();
				break;
			case Stop:
				stop();
				break;
			case StopAndReset:
				stop();
				reset();
				break;
		}
	}
}
