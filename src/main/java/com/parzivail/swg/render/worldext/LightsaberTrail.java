package com.parzivail.swg.render.worldext;

import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.GLPalette;
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
		if (points.size() < 2)
			return;

		float outlineOffset = 0.04f;

		Vector3f start = points.get(0).getEndPos();
		Vector3f end = points.get(points.size() - 1).getEndPos();

		float totalLen = Math.abs(Vector3f.sub(end, start, null).lengthSquared());
		//		if (totalLen < 0.02f)
		//			return;

		GL.Begin(PrimitiveType.Quads);
		for (int i = 1; i < points.size(); i++)
		{
			if (i != 1)
				continue;

			LightsaberTrailComponent pointsHere = points.get(i);
			LightsaberTrailComponent pointsPrev = points.get(points.size() - 1);
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

			Vector3f normal = Vector3f.cross(Vector3f.sub(coreEndPrev, coreStartPrev, null), Vector3f.sub(coreEndHere, coreStartPrev, null), null).normalise(null);

			if (normal.y < 0)
				normal.scale(-1);

			normal.scale(0.0175f);

			//			GL.Vertex3(coreStartPrev);
			//			GL.Vertex3(coreEndPrev);
			//			GL.Vertex3(coreEndHere);
			//			GL.Vertex3(coreStartHere);

			// Top
			GL.Vertex3(Vector3f.add(coreStartPrev, normal, null));
			GL.Vertex3(Vector3f.add(coreEndPrev, normal, null));
			GL.Vertex3(Vector3f.add(coreEndHere, normal, null));
			GL.Vertex3(Vector3f.add(coreStartHere, normal, null));

			// Bottom
			GL.Vertex3(Vector3f.sub(coreStartPrev, normal, null));
			GL.Vertex3(Vector3f.sub(coreEndPrev, normal, null));
			GL.Vertex3(Vector3f.sub(coreEndHere, normal, null));
			GL.Vertex3(Vector3f.sub(coreStartHere, normal, null));

			// Prev wall
			GL.Vertex3(Vector3f.add(coreStartPrev, normal, null));
			GL.Vertex3(Vector3f.add(coreEndPrev, normal, null));
			GL.Vertex3(Vector3f.sub(coreEndPrev, normal, null));
			GL.Vertex3(Vector3f.sub(coreStartPrev, normal, null));

			// Here wall
			GL.Vertex3(Vector3f.add(coreStartHere, normal, null));
			GL.Vertex3(Vector3f.add(coreEndHere, normal, null));
			GL.Vertex3(Vector3f.sub(coreEndHere, normal, null));
			GL.Vertex3(Vector3f.sub(coreStartHere, normal, null));

			//			GL.Color(pointsHere.getColor(), (int)(p * 128));
			//
			//			GL.Vertex3(prevEnd);
			//			GL.Vertex3(coreStartPrev);
			//			GL.Vertex3(coreStartHere);
			//			GL.Vertex3(hereEnd);
			//
			//			if (i > points.size() * 0.3)
			//				GL.Color(pointsHere.getCoreColor(), 255);
			//
			//			GL.Vertex3(coreStartPrev);
			//			GL.Vertex3(coreEndPrev);
			//			GL.Vertex3(coreEndHere);
			//			GL.Vertex3(coreStartHere);
			//
			//			GL.Color(pointsHere.getColor(), (int)(p * 128));
			//
			//			GL.Vertex3(coreEndPrev);
			//			GL.Vertex3(prevBase);
			//			GL.Vertex3(hereBase);
			//			GL.Vertex3(coreEndHere);
		}
		GL.End();
		GL.Color(GLPalette.WHITE);
	}
}
