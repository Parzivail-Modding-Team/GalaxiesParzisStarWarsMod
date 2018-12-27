package com.parzivail.swg.proxy;

import com.parzivail.swg.render.worldext.LightsaberTrailComponent;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;

import java.util.ArrayList;

public class LightsaberTrail
{
	public ArrayList<LightsaberTrailComponent> points = new ArrayList<>();

	public void addPointSet(int color, int life, Vector3f pBase, Vector3f pEnd)
	{
		points.add(new LightsaberTrailComponent(color, life, pBase, pEnd));
	}

	public void tick()
	{
		points.removeIf(LightsaberTrailComponent::shouldDie);
	}

	public void render()
	{
		GL.Begin(PrimitiveType.TriangleStrip);
		for (int i = 0; i < points.size(); i++)
		{
			LightsaberTrailComponent pointsHere = points.get(i);
			float p = (float)i / points.size();

			Vector3f hereBase = pointsHere.getBasePos();
			Vector3f hereEnd = pointsHere.getEndPos();

			GL.Color(pointsHere.getColor(), (int)(p * 128));
			GL.Vertex3(hereBase.x, hereBase.y, hereBase.z);
			GL.Vertex3(hereEnd.x, hereEnd.y, hereEnd.z);
		}
		GL.End();
	}
}
