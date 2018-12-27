package com.parzivail.swg.proxy;

import com.parzivail.util.math.lwjgl.Vector3f;
import org.lwjgl.Sys;

import java.util.ArrayList;

public class LightsaberTrail
{
	public ArrayList<PointSet> points = new ArrayList<>();

	public void addPointSet(int color, Vector3f pBase, Vector3f pEnd)
	{
		points.add(new PointSet(color, pBase, pEnd));
	}

	public void tick()
	{
		points.removeIf(set -> set.age + 60 < Sys.getTime());
	}

	public class PointSet
	{
		public final long age;
		public final int color;
		public final Vector3f pBase;
		public final Vector3f pEnd;

		PointSet(int color, Vector3f pBase, Vector3f pEnd)
		{
			age = Sys.getTime();
			this.color = color;
			this.pBase = pBase;
			this.pEnd = pEnd;
		}
	}
}
