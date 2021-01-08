package com.parzivail.util.binary;

import java.nio.ByteBuffer;

/**
 * Accepts various forms of a floating point half-precision (2 byte) number
 * and contains methods to convert to a
 * full-precision floating point number Float and Double instance.
 * <p>
 * This implemention was inspired by x4u who is a user contributing
 * to stackoverflow.com.
 * (https://stackoverflow.com/users/237321/x4u).
 *
 * @author dougestep
 */
public class HalfFloat
{
	private final short halfPrecision;
	private Float fullPrecision;

	/**
	 * Creates an instance of the class from the supplied the supplied
	 * byte array.  The byte array must be exactly two bytes in length.
	 *
	 * @param bytes the two-byte byte array.
	 */
	public HalfFloat(byte[] bytes)
	{
		if (bytes.length != 2)
			throw new IllegalArgumentException("The supplied byte array must be exactly two bytes in length");

		final ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.halfPrecision = buffer.getShort();
	}

	/**
	 * Creates an instance of this class from the supplied short number.
	 *
	 * @param number the number defined as a short.
	 */
	public HalfFloat(final short number)
	{
		this.halfPrecision = number;
		this.fullPrecision = toFullPrecision();
	}

	/**
	 * Creates an instance of this class from the supplied
	 * full-precision floating point number.
	 *
	 * @param number the float number.
	 */
	public HalfFloat(final float number)
	{
		if (number > Short.MAX_VALUE)
			throw new IllegalArgumentException("The supplied float is too large for a two byte representation");
		if (number < Short.MIN_VALUE)
			throw new IllegalArgumentException("The supplied float is too small for a two byte representation");

		final int val = fromFullPrecision(number);
		this.halfPrecision = (short)val;
		this.fullPrecision = number;
	}

	/**
	 * Returns the half-precision float as a number defined as a short.
	 *
	 * @return the short.
	 */
	public short getHalfPrecisionAsShort()
	{
		return halfPrecision;
	}

	/**
	 * Returns a full-precision floating pointing number from the
	 * half-precision value assigned on this instance.
	 *
	 * @return the full-precision floating pointing number.
	 */
	public float getFullFloat()
	{
		if (fullPrecision == null)
			fullPrecision = toFullPrecision();
		return fullPrecision;
	}

	/**
	 * Returns the full-precision float number from the half-precision
	 * value assigned on this instance.
	 *
	 * @return the full-precision floating pointing number.
	 */
	private float toFullPrecision()
	{
		int mantisa = halfPrecision & 0x03ff;
		int exponent = halfPrecision & 0x7c00;

		if (exponent == 0x7c00)
			exponent = 0x3fc00;
		else if (exponent != 0)
		{
			exponent += 0x1c000;
			if (mantisa == 0 && exponent > 0x1c400)
				return Float.intBitsToFloat((halfPrecision & 0x8000) << 16 | exponent << 13);
		}
		else if (mantisa != 0)
		{
			exponent = 0x1c400;
			do
			{
				mantisa <<= 1;
				exponent -= 0x400;
			}
			while ((mantisa & 0x400) == 0);
			mantisa &= 0x3ff;
		}

		return Float.intBitsToFloat((halfPrecision & 0x8000) << 16 | (exponent | mantisa) << 13);
	}

	/**
	 * Returns the integer representation of the supplied
	 * full-precision floating pointing number.
	 *
	 * @param number the full-precision floating pointing number.
	 *
	 * @return the integer representation.
	 */
	private int fromFullPrecision(final float number)
	{
		int fbits = Float.floatToIntBits(number);
		int sign = fbits >>> 16 & 0x8000;

		int val = (fbits & 0x7fffffff) + 0x1000;

		if (val >= 0x47800000)
		{
			if ((fbits & 0x7fffffff) >= 0x47800000)
			{
				if (val < 0x7f800000)
					return sign | 0x7c00;
				return sign | 0x7c00 | (fbits & 0x007fffff) >>> 13;
			}
			return sign | 0x7bff;
		}
		if (val >= 0x38800000)
			return sign | val - 0x38000000 >>> 13;
		if (val < 0x33000000)
			return sign;
		val = (fbits & 0x7fffffff) >>> 23;
		return sign | ((fbits & 0x7fffff | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val);
	}
}
