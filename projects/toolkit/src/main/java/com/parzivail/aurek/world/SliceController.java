package com.parzivail.aurek.world;

import com.parzivail.aurek.render.ChunkedWorldMesh;
import net.minecraft.util.math.ChunkPos;

public class SliceController implements IChunkOverrider
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
		rebuildMesh(activeX != isActiveX, false);
		isActiveX = activeX;
	}

	public void setActiveZ(boolean activeZ)
	{
		rebuildMesh(false, activeZ != isActiveZ);
		isActiveZ = activeZ;
	}

	public void setReverseX(boolean reverseX)
	{
		this.reverseX = reverseX;
		rebuildMesh(true, false);
	}

	public void setReverseZ(boolean reverseZ)
	{
		this.reverseZ = reverseZ;
		rebuildMesh(false, true);
	}

	public void setValueX(int valueX)
	{
		if (isActiveX)
			rebuildMesh(this.valueX, valueX, this.valueZ, this.valueZ);
		this.valueX = valueX;
	}

	public void setValueZ(int valueZ)
	{
		if (isActiveZ)
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

	private void rebuildMesh(boolean changedX, boolean changedZ)
	{
		ChunkPos.stream(mesh.getMin(), mesh.getMax())
		        .filter(chunkPos -> {
			        // Rebuild chunks on both sides of the border of the slice
			        if (changedX && (chunkPos.x == valueX || chunkPos.x == valueX - 1))
				        return true;
			        if (changedZ && (chunkPos.z == valueZ || chunkPos.z == valueZ - 1))
				        return true;
			        return false;
		        })
		        .forEach(mesh::scheduleRebuild);
	}

	private void rebuildMesh(int oldX, int newX, int oldZ, int newZ)
	{
		ChunkPos.stream(mesh.getMin(), mesh.getMax())
		        .filter(chunkPos -> {
			        // Rebuild chunks on both sides of the border of the slice
			        if (oldX != newX && (chunkPos.x == oldX || chunkPos.x == oldX - 1 || chunkPos.x == newX || chunkPos.x == newX - 1))
				        return true;
			        if (oldZ != newZ && (chunkPos.z == oldZ || chunkPos.z == oldZ - 1 || chunkPos.z == newZ || chunkPos.z == newZ - 1))
				        return true;
			        return false;
		        })
		        .forEach(mesh::scheduleRebuild);
	}

	public boolean shouldChunkRender(int x, int z)
	{
		if (isActiveX && x < valueX == reverseX)
			return false;
		if (isActiveZ && z < valueZ == reverseZ)
			return false;

		return true;
	}

	public boolean shouldChunkRemesh(int x, int z)
	{
		return (isActiveX && (x == valueX || x == valueX - 1)) || (isActiveZ && (z == valueZ || z == valueZ - 1));
	}

	@Override
	public boolean shouldChunkBeEmpty(int x, int z)
	{
		if (isActiveX)
		{
			if (x == valueX && !reverseX)
				return true;
			if (x == valueX - 1 && reverseX)
				return true;
		}

		if (isActiveZ)
		{
			if (z == valueZ && !reverseZ)
				return true;
			if (z == valueZ - 1 && reverseZ)
				return true;
		}

		return false;
	}
}
