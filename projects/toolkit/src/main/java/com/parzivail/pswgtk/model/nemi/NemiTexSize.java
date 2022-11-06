package com.parzivail.pswgtk.model.nemi;

import java.util.Objects;

public final class NemiTexSize
{
	private final int w;
	private final int h;

	public NemiTexSize(int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	public int w()
	{
		return w;
	}

	public int h()
	{
		return h;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiTexSize)obj;
		return this.w == that.w &&
		       this.h == that.h;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(w, h);
	}

	@Override
	public String toString()
	{
		return "NemiTexSize[" +
		       "w=" + w + ", " +
		       "h=" + h + ']';
	}
}
