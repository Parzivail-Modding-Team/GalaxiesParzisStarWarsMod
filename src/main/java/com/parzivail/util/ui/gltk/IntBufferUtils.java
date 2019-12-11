package com.parzivail.util.ui.gltk;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

public class IntBufferUtils
{
	public static IntBuffer makeBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}
}
