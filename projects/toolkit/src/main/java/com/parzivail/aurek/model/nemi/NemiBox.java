package com.parzivail.aurek.model.nemi;

import java.util.Objects;

public final class NemiBox
{
	private final NemiTexPos tex;
	private final NemiVec3 pos;
	private final NemiVec3 size;
	private final double inflate;
	private final boolean mirrored;

	public NemiBox(NemiTexPos tex, NemiVec3 pos, NemiVec3 size, double inflate, boolean mirrored)
	{
		this.tex = tex;
		this.pos = pos;
		this.size = size;
		this.inflate = inflate;
		this.mirrored = mirrored;
	}

	public NemiTexPos tex()
	{
		return tex;
	}

	public NemiVec3 pos()
	{
		return pos;
	}

	public NemiVec3 size()
	{
		return size;
	}

	public double inflate()
	{
		return inflate;
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
		var that = (NemiBox)obj;
		return Objects.equals(this.tex, that.tex) &&
		       Objects.equals(this.pos, that.pos) &&
		       Objects.equals(this.size, that.size) &&
		       Double.doubleToLongBits(this.inflate) == Double.doubleToLongBits(that.inflate) &&
		       this.mirrored == that.mirrored;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(tex, pos, size, inflate, mirrored);
	}

	@Override
	public String toString()
	{
		return "NemiBox[" +
		       "tex=" + tex + ", " +
		       "pos=" + pos + ", " +
		       "size=" + size + ", " +
		       "inflate=" + inflate + ", " +
		       "mirrored=" + mirrored + ']';
	}
}
