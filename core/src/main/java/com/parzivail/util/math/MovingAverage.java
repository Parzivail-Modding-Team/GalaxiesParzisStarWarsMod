package com.parzivail.util.math;

public class MovingAverage
{
	private final int _windowSize;
	private final float[] _values;

	private int _readIndex;
	private float _total;
	private float _average;
	private float _oldAverage;

	public MovingAverage(int windowSize, float initialValue)
	{
		_windowSize = windowSize;
		_values = new float[windowSize];

		for (var i = 0; i < windowSize; i++)
			_values[i] = initialValue;

		_total = windowSize * initialValue;
		_average = initialValue;
		_oldAverage = initialValue;
	}

	public float getAverage()
	{
		return _average;
	}

	public float getTotal()
	{
		return _total;
	}

	public float getOldAverage()
	{
		return _oldAverage;
	}

	public float update(float nextValue)
	{
		_total -= _values[_readIndex];
		_values[_readIndex] = nextValue;
		_total += _values[_readIndex];
		_readIndex++;

		if (_readIndex >= _windowSize)
		{
			_readIndex = 0;
		}

		_oldAverage = _average;
		_average = _total / _windowSize;
		return _average;
	}
}
