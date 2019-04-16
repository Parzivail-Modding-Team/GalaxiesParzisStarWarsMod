package com.parzivail.util.ui.gltk;

import com.parzivail.util.common.Lumberjack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

public class TessCallback extends GLUtessellatorCallbackAdapter
{
	public static final TessCallback INSTANCE = new TessCallback();

	@Override
	public void begin(int type)
	{
		GL11.glBegin(type);
	}

	@Override
	public void vertex(Object vertexData)
	{
		double[] vert = (double[])vertexData;
		GL11.glVertex2d(vert[0], vert[1]);
	}

	public void combine(double[] coords, Object[] data, float[] weight, Object[] outData)
	{
		for (int i = 0; i < outData.length; i++)
		{
			double[] combined = new double[6];
			combined[0] = coords[0];
			combined[1] = coords[1];
			outData[i] = combined;
		}
	}

	@Override
	public void end()
	{
		GL11.glEnd();
	}

	@Override
	public void error(int errnum)
	{
		Lumberjack.log("GLUTess Error: %s", errnum);
	}
}
