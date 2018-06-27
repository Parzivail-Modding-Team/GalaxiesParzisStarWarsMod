package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public enum MaterialFace
{
	Front(GL11.GL_FRONT), Back(GL11.GL_BACK), FrontAndBack(GL11.GL_FRONT_AND_BACK);

	private final int glValue;

	MaterialFace(int glValue)
	{
		this.glValue = glValue;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
