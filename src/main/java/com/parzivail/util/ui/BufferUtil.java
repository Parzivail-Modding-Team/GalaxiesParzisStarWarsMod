package com.parzivail.util.ui;

import net.minecraft.client.renderer.GLAllocation;

import java.nio.FloatBuffer;

public class BufferUtil
{
	private static final FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	public static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_)
	{
		/**
		 * Update and return colorBuffer with the RGBA values passed as arguments
		 */
		return setColorBuffer((float)p_74517_0_, (float)p_74517_2_, (float)p_74517_4_, (float)p_74517_6_);
	}

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	public static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
	{
		colorBuffer.clear();
		colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
		colorBuffer.flip();
		/** Float buffer used to set OpenGL material colors */
		return colorBuffer;
	}
}
