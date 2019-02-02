package com.parzivail.swg.render.worldext;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;

import java.util.ArrayList;

public class LightsaberTrail
{
	public ArrayList<LightsaberTrailComponent> points = new ArrayList<>();

	public void addPointSet(int bladeColor, int coreColor, float bladeLength, int life, Vector3f pBase, Vector3f pEnd)
	{
		points.add(new LightsaberTrailComponent(bladeColor, coreColor, bladeLength, life, pBase, pEnd));
	}

	public void tick()
	{
		points.removeIf(LightsaberTrailComponent::shouldDie);
	}

	public void render()
	{
		float outlineOffset = 0.33f;

		GL.Begin(PrimitiveType.TriangleStrip);
		for (int i = 1; i < points.size(); i++)
		{
			LightsaberTrailComponent pointsHere = points.get(i);
			LightsaberTrailComponent pointsPrev = points.get(i - 1);
			float p = (float)i / points.size();

			Vector3f hereBase = pointsHere.getBasePos();
			Vector3f hereEnd = pointsHere.getEndPos();

			Vector3f prevBase = pointsPrev.getBasePos();
			Vector3f prevEnd = pointsPrev.getEndPos();

			float len = pointsHere.getBladeLength();
			float topLenLerp = (len - outlineOffset) / len;
			float botLenLerp = outlineOffset / len;

			Vector3f coreStartHere = MathUtil.lerp(topLenLerp, hereBase, hereEnd);
			Vector3f coreStartPrev = MathUtil.lerp(topLenLerp, prevBase, prevEnd);

			Vector3f coreEndHere = MathUtil.lerp(botLenLerp, hereBase, hereEnd);
			Vector3f coreEndPrev = MathUtil.lerp(botLenLerp, prevBase, prevEnd);

			GL.Color(pointsHere.getColor(), (int)(p * 128));

			GL.Vertex3(prevEnd);
			GL.Vertex3(hereEnd);
			GL.Vertex3(coreStartPrev);
			GL.Vertex3(coreStartHere);

			GL.Color(pointsHere.getCoreColor(), (int)(p * 128));

			GL.Vertex3(coreStartPrev);
			GL.Vertex3(coreStartHere);
			GL.Vertex3(coreEndPrev);
			GL.Vertex3(coreEndHere);

			GL.Color(pointsHere.getColor(), (int)(p * 128));

			GL.Vertex3(coreEndPrev);
			GL.Vertex3(coreEndHere);
			GL.Vertex3(prevBase);
			GL.Vertex3(hereBase);
		}
		GL.End();
	}
}
