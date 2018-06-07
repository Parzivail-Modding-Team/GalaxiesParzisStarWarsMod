package com.parzivail.swg.render.decal;

import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WorldDecals
{
	private static HashMap<Integer, List<Decal>> allDecals = new HashMap<>();

	public static void create(int dim, Decal d)
	{
		if (!allDecals.containsKey(dim))
			allDecals.put(dim, new ArrayList<>());

		List<Decal> decalsInDim = allDecals.get(dim);
		decalsInDim.add(d);

		while (decalsInDim.size() > 50)
			decalsInDim.remove(0);
	}

	public static void render(int dim, float partialTicks)
	{
		if (!allDecals.containsKey(dim))
			return;

		List<Decal> decals = allDecals.get(dim);
		Vec3 playerPos = Minecraft.getMinecraft().thePlayer.getPosition(partialTicks);
		GL.Disable(EnableCap.CullFace);
		GL.Enable(EnableCap.Blend);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (Iterator<Decal> iD = decals.iterator(); iD.hasNext(); )
		{
			Decal d = iD.next();
			if (d.shouldDie() || Math.pow(d.x - playerPos.xCoord, 2) + Math.pow(d.y - playerPos.yCoord, 2) + Math.pow(d.z - playerPos.zCoord, 2) > 10000)
			{
				iD.remove();
				continue;
			}

			GL.PushMatrix();
			GL.Translate(d.x - playerPos.xCoord, d.y - playerPos.yCoord, d.z - playerPos.zCoord);
			rotateToFace(d.direction);
			GL.Translate(0, 0, -0.0001f); // Back it off the block slightly to avoid z-fighting with the block
			GL.Rotate(d.rotation, 0, 0, 1);
			GL.Scale(0.05f * d.size);
			d.render();
			GL.PopMatrix();
		}
	}

	private static void rotateToFace(EnumFacing direction)
	{
		switch (direction)
		{
			case DOWN:
				GL.Rotate(-90, 1, 0, 0);
				break;
			case UP:
				GL.Rotate(90, 1, 0, 0);
				break;
			case NORTH:
				break;
			case SOUTH:
				GL.Rotate(180, 0, 1, 0);
				break;
			case EAST:
				GL.Rotate(-90, 0, 1, 0);
				break;
			case WEST:
				GL.Rotate(90, 0, 1, 0);
				break;
		}
	}
}
