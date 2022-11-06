package com.parzivail.pswgtk.model.nemi;

import java.util.List;
import java.util.Objects;

public final class NemiPart
{
	private final String parent;
	private final NemiEuler rot;
	private final List<NemiBox> boxes;
	private final NemiVec3 pos;

	public NemiPart(String parent, NemiEuler rot, List<NemiBox> boxes, NemiVec3 pos)
	{
		this.parent = parent;
		this.rot = rot;
		this.boxes = boxes;
		this.pos = pos;
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
		       Objects.equals(this.pos, that.pos);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(parent, rot, boxes, pos);
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
