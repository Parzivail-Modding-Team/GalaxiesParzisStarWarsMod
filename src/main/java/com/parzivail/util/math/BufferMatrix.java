package com.parzivail.util.math;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class BufferMatrix
{
	private static final float[] IDENTITY_MATRIX = {
			1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
	};

	private static final FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);

	/**
	 * Make matrix an identity matrix
	 */
	private static void __gluMakeIdentityf(FloatBuffer m)
	{
		int oldPos = m.position();
		m.put(IDENTITY_MATRIX);
		m.position(oldPos);
	}

	/**
	 * @param src
	 * @param inverse
	 *
	 * @return
	 */
	public static boolean invertMatrix(FloatBuffer src, FloatBuffer inverse)
	{
		int i, j, k, swap;
		float t;
		FloatBuffer temp = tempMatrix;

		for (i = 0; i < 16; i++)
		{
			temp.put(i, src.get(i + src.position()));
		}
		__gluMakeIdentityf(inverse);

		for (i = 0; i < 4; i++)
		{
			/*
			 * * Look for largest element in column
			 */
			swap = i;
			for (j = i + 1; j < 4; j++)
			{
				/*
				 * if (fabs(temp[j][i]) > fabs(temp[i][i])) { swap = j;
				 */
				if (Math.abs(temp.get(j * 4 + i)) > Math.abs(temp.get(i * 4 + i)))
				{
					swap = j;
				}
			}

			if (swap != i)
			{
				/*
				 * * Swap rows.
				 */
				for (k = 0; k < 4; k++)
				{
					t = temp.get(i * 4 + k);
					temp.put(i * 4 + k, temp.get(swap * 4 + k));
					temp.put(swap * 4 + k, t);

					t = inverse.get(i * 4 + k);
					inverse.put(i * 4 + k, inverse.get(swap * 4 + k));
					//inverse.put((i << 2) + k, inverse.get((swap << 2) + k));
					inverse.put(swap * 4 + k, t);
					//inverse.put((swap << 2) + k, t);
				}
			}

			if (temp.get(i * 4 + i) == 0)
			{
				/*
				 * * No non-zero pivot. The matrix is singular, which shouldn't *
				 * happen. This means the user gave us a bad matrix.
				 */
				return false;
			}

			t = temp.get(i * 4 + i);
			for (k = 0; k < 4; k++)
			{
				temp.put(i * 4 + k, temp.get(i * 4 + k) / t);
				inverse.put(i * 4 + k, inverse.get(i * 4 + k) / t);
			}
			for (j = 0; j < 4; j++)
			{
				if (j != i)
				{
					t = temp.get(j * 4 + i);
					for (k = 0; k < 4; k++)
					{
						temp.put(j * 4 + k, temp.get(j * 4 + k) - temp.get(i * 4 + k) * t);
						inverse.put(j * 4 + k, inverse.get(j * 4 + k) - inverse.get(i * 4 + k) * t);
						/*inverse.put(
							(j << 2) + k,
							inverse.get((j << 2) + k) - inverse.get((i << 2) + k) * t);*/
					}
				}
			}
		}
		return true;
	}

	/**
	 * @param a
	 * @param b
	 * @param result
	 */
	public static void multiply(FloatBuffer a, FloatBuffer b, FloatBuffer result)
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				result.put(result.position() + i * 4 + j, a.get(a.position() + i * 4 + 0) * b.get(b.position() + 0 * 4 + j) + a.get(a.position() + i * 4 + 1) * b.get(b.position() + 1 * 4 + j) + a.get(a.position() + i * 4 + 2) * b.get(b.position() + 2 * 4 + j) + a.get(a.position() + i * 4 + 3) * b.get(b.position() + 3 * 4 + j));
			}
		}
	}
}
