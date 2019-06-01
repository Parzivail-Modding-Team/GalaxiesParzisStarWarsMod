package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public enum PolygonMode
{
	Point(GL11.GL_POINT), Line(GL11.GL_LINE), Fill(GL11.GL_FILL);

	private final int glValue;

	PolygonMode(int glValue)
	{
		this.glValue = glValue;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
