package com.parzivail.aurek.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil
{
	public static String ensureExtension(String path, String extension)
	{
		if (path.endsWith(extension))
			return path;
		return path + extension;
	}

	public static void zip(ZipOutputStream zip, ByteBuf buffer, String filename)
	{
		try (var b = new ByteBufInputStream(buffer))
		{
			ZipEntry ze = new ZipEntry(filename);
			zip.putNextEntry(ze);

			var availableBytes = buffer.readableBytes();
			if (b.transferTo(zip) != availableBytes)
				throw new IOException("Unexpected end of stream");

			zip.closeEntry();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void zip(ZipOutputStream zip, String sourceFilename, String destFilename)
	{
		try (var b = new FileInputStream(sourceFilename))
		{
			ZipEntry ze = new ZipEntry(destFilename);
			zip.putNextEntry(ze);

			var availableBytes = b.available();
			if (b.transferTo(zip) != availableBytes)
				throw new IOException("Unexpected end of stream");

			zip.closeEntry();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
