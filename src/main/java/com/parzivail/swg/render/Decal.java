package com.parzivail.swg.render;

import com.parzivail.util.ui.gltk.GL;
import net.minecraft.util.EnumFacing;

public class Decal
{
	private final float x;
	private final float y;
	private final float z;
	private final EnumFacing direction;

	public Decal(float x, float y, float z, EnumFacing direction)
	{

		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
	}

	public void render()
	{
		GL.Translate(x, y, z);
		rotateToFace();
	}

	private void rotateToFace()
	{
		switch (direction)
		{
			case DOWN:
				break;
			case UP:
				break;
			case NORTH:
				break;
			case SOUTH:
				break;
			case EAST:
				break;
			case WEST:
				break;
		}
	}
}
