package com.parzivail.swg.render.pipeline;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public enum EnumFacingAxis
{
	X("x"), Y("y"), Z("z");

	private static final Map<String, EnumFacingAxis> NAME_LOOKUP = Maps.newHashMap();
	private final String name;

	EnumFacingAxis(String name)
	{
		this.name = name;
	}

	/**
	 * Get the axis specified by the given name
	 */
	@Nullable
	public static EnumFacingAxis byName(String name)
	{
		return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
	}

	/**
	 * Like getName but doesn't override the method from Enum.
	 */
	public String getName2()
	{
		return name;
	}

	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	static
	{
		for (EnumFacingAxis enumfacing$axis : values())
		{
			NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(Locale.ROOT), enumfacing$axis);
		}
	}
}
