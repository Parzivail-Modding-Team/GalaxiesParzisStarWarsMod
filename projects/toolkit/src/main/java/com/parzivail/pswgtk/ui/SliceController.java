package com.parzivail.pswgtk.ui;

import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import net.minecraft.util.math.ChunkPos;

public class SliceController
{
	private final ChunkedWorldMesh mesh;

	private boolean isActiveX;
	private boolean reverseX;
	private int valueX;

	private boolean isActiveZ;
	private boolean reverseZ;
	private int valueZ;

	public SliceController(ChunkedWorldMesh mesh)
	{
		this.mesh = mesh;
	}

	public boolean isActiveX()
	{
		return isActiveX;
	}

	public void setActiveX(boolean activeX)
	{
		isActiveX = activeX;
		rebuildMesh();
	}

	public void setActiveZ(boolean activeZ)
	{
		isActiveZ = activeZ;
		rebuildMesh();
	}

	public void setReverseX(boolean reverseX)
	{
		this.reverseX = reverseX;
	}

	public void setReverseZ(boolean reverseZ)
	{
		this.reverseZ = reverseZ;
	}

	public void setValueX(int valueX)
	{
		rebuildMesh(this.valueX, valueX, this.valueZ, this.valueZ);
		this.valueX = valueX;
	}

	public void setValueZ(int valueZ)
	{
		rebuildMesh(this.valueX, this.valueX, this.valueZ, valueZ);
		this.valueZ = valueZ;
	}

	public boolean isActiveZ()
	{
		return isActiveZ;
	}

	public boolean isReverseX()
	{
		return reverseX;
	}

	public boolean isReverseZ()
	{
		return reverseZ;
	}

	public int getValueX()
	{
		return valueX;
	}

	public int getValueZ()
	{
		return valueZ;
	}

	private void rebuildMesh()
	{
		rebuildMesh(this.valueX, this.valueX, this.valueZ, this.valueZ);
	}

	private void rebuildMesh(int oldX, int newX, int oldZ, int newZ)
	{
	}

	public boolean shouldChunkRender(ChunkPos pos)
	{
		if (isActiveX)
		{
			if (reverseX)
				if (pos.x > valueX)
					return false;
				else if (pos.x < valueX)
					return false;
		}

		if (isActiveZ)
		{
			if (reverseZ)
				if (pos.z > valueZ)
					return false;
				else if (pos.z < valueZ)
					return false;
		}

		return true;
	}
}
