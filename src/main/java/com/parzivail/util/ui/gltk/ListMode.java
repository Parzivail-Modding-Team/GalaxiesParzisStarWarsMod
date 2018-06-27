package com.parzivail.util.ui.gltk;

import org.lwjgl.opengl.GL11;

/**
 * Created by colby on 9/13/2017.
 */
public enum ListMode
{
	Compile(GL11.GL_COMPILE), CompileAndExecute(GL11.GL_COMPILE_AND_EXECUTE);

	private final int glValue;

	ListMode(int glValue)
	{
		this.glValue = glValue;
	}

	public int getGlValue()
	{
		return glValue;
	}
}
