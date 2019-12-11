package com.parzivail.util.ui.gltk;

import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class FloatBufferUtils
{
	public static FloatBuffer makeBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer makeBuffer(Vector2f[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length * 2);
		for (Vector2f v : data)
		{
			buffer.put(v.x);
			buffer.put(v.y);
		}
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer makeBuffer(Vector3f[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length * 3);
		for (Vector3f v : data)
		{
			buffer.put(v.x);
			buffer.put(v.y);
			buffer.put(v.z);
		}
		buffer.flip();

		return buffer;
	}
}
