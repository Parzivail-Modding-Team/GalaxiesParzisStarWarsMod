package com.parzivail.pswgtk.util;

public class FileUtil
{
	public static String ensureExtension(String path, String extension)
	{
		if (path.endsWith(extension))
			return path;
		return path + extension;
	}
}
