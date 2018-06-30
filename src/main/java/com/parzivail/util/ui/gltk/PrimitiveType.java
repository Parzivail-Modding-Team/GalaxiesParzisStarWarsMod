package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public enum PrimitiveType
{
	Points(GL11.GL_POINTS),
	Lines(GL11.GL_LINES),
	LineLoop(GL11.GL_LINE_LOOP),
	LineStrip(GL11.GL_LINE_STRIP),
	Triangles(GL11.GL_TRIANGLES),
	TriangleStrip(GL11.GL_TRIANGLE_STRIP),
	TriangleFan(GL11.GL_TRIANGLE_FAN),
	Quads(GL11.GL_QUADS),
	QuadStrip(GL11.GL_QUAD_STRIP),
	Polygon(GL11.GL_POLYGON);

	private final int glValue;

	PrimitiveType(int glValue)
	{
		this.glValue = glValue;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
