package com.parzivail.aurek.model.nemi;

import java.util.List;
import java.util.Objects;

public final class NemiPart
{
	private final String parent;
	private final NemiEuler rot;
	private final List<NemiBox> boxes;
	private final NemiVec3 pos;
	private final boolean mirrored;

	public NemiPart(String parent, NemiEuler rot, List<NemiBox> boxes, NemiVec3 pos, boolean mirrored)
	{
		this.parent = parent;
		this.rot = rot;
		this.boxes = boxes;
		this.pos = pos;
		this.mirrored = mirrored;
	}

	public String parent()
	{
		return parent;
	}

	public NemiEuler rot()
	{
		return rot;
	}

	public List<NemiBox> boxes()
	{
		return boxes;
	}

	public NemiVec3 pos()
	{
		return pos;
	}

	public boolean mirrored()
	{
		return mirrored;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiPart)obj;
		return Objects.equals(this.parent, that.parent) &&
		       Objects.equals(this.rot, that.rot) &&
		       Objects.equals(this.boxes, that.boxes) &&
		       Objects.equals(this.pos, that.pos) &&
		       Objects.equals(this.mirrored, that.mirrored);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(parent, rot, boxes, pos, mirrored);
	}

	@Override
	public String toString()
	{
		return "NemiPart[" +
		       "parent=" + parent + ", " +
		       "rot=" + rot + ", " +
		       "boxes=" + boxes + ", " +
		       "pos=" + pos + ']';
	}
}
