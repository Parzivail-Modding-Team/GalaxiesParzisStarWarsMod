package com.parzivail.pswg.util;

import com.parzivail.pswg.Galaxies;
import net.minecraft.util.Identifier;

import java.io.InputStream;

public class PIO
{
	public static InputStream getStream(String domain, Identifier resourceLocation)
	{
		return Galaxies.class.getClassLoader().getResourceAsStream(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
	}
}
