package com.parzivail.pswgtk.model.nemi;

import java.util.HashMap;
import java.util.Objects;

public final class NemiModel
{
	private final String exporter;
	private final String name;
	private final NemiTexSize tex;
	private final HashMap<String, NemiPart> parts;

	public NemiModel(String exporter, String name, NemiTexSize tex, HashMap<String, NemiPart> parts)
	{
		this.exporter = exporter;
		this.name = name;
		this.tex = tex;
		this.parts = parts;
	}

	public String exporter()
	{
		return exporter;
	}

	public String name()
	{
		return name;
	}

	public NemiTexSize tex()
	{
		return tex;
	}

	public HashMap<String, NemiPart> parts()
	{
		return parts;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiModel)obj;
		return Objects.equals(this.exporter, that.exporter) &&
		       Objects.equals(this.name, that.name) &&
		       Objects.equals(this.tex, that.tex) &&
		       Objects.equals(this.parts, that.parts);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(exporter, name, tex, parts);
	}

	@Override
	public String toString()
	{
		return "NemiModel[" +
		       "exporter=" + exporter + ", " +
		       "name=" + name + ", " +
		       "tex=" + tex + ", " +
		       "parts=" + parts + ']';
	}
}
