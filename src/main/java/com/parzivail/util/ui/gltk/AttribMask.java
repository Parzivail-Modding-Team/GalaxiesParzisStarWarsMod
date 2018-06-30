package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

/**
 * Created by colby on 9/13/2017.
 */
public enum AttribMask
{
	CurrentBit(GL11.GL_CURRENT_BIT),
	PointBit(GL11.GL_POINT_BIT),
	LineBit(GL11.GL_LINE_BIT),
	PolygonBit(GL11.GL_POLYGON_BIT),
	PolygonStippleBit(GL11.GL_POLYGON_STIPPLE_BIT),
	PixelModeBit(GL11.GL_PIXEL_MODE_BIT),
	LightingBit(GL11.GL_LIGHTING_BIT),
	FogBit(GL11.GL_FOG_BIT),
	DepthBufferBit(GL11.GL_DEPTH_BUFFER_BIT),
	AccumBufferBit(GL11.GL_ACCUM_BUFFER_BIT),
	StencilBufferBit(GL11.GL_STENCIL_BUFFER_BIT),
	ViewportBit(GL11.GL_VIEWPORT_BIT),
	TransformBit(GL11.GL_TRANSFORM_BIT),
	EnableBit(GL11.GL_ENABLE_BIT),
	ColorBufferBit(GL11.GL_COLOR_BUFFER_BIT),
	HintBit(GL11.GL_HINT_BIT),
	EvalBit(GL11.GL_EVAL_BIT),
	ListBit(GL11.GL_LIST_BIT),
	TextureBit(GL11.GL_TEXTURE_BIT),
	ScissorBit(GL11.GL_SCISSOR_BIT),
	AllAttribBits(GL11.GL_ALL_ATTRIB_BITS);

	private final int glValue;

	AttribMask(int glValue)
	{
		this.glValue = glValue;
	}

	public static int encode(EnumSet<AttribMask> set)
	{
		int ret = 0;
		for (AttribMask val : set)
			ret |= val.getGlValue();
		return ret;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
