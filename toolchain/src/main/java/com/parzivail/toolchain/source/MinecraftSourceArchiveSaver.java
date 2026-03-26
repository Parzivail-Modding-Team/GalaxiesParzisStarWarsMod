package com.parzivail.toolchain.source;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Saves Vineflower output archives in a thread-safe way.
 */
public final class MinecraftSourceArchiveSaver implements IResultSaver
{
	/**
	 * The destination source archive.
	 */
	private final Path _sourcesArchive;

	/**
	 * The open archive output stream.
	 */
	private ZipOutputStream _outputStream;

	/**
	 * The written archive entry names.
	 */
	private final Set<String> _entries;

	/**
	 * The optional line-map writer.
	 */
	private Writer _lineMapWriter;

	/**
	 * Creates a saver for one source archive.
	 *
	 * @param sourcesArchive the destination archive
	 */
	public MinecraftSourceArchiveSaver(Path sourcesArchive)
	{
		_sourcesArchive = sourcesArchive;
		_entries = new HashSet<>();
	}

	@Override
	public void createArchive(String path, String archiveName, Manifest manifest)
	{
		try
		{
			Files.createDirectories(_sourcesArchive.getParent());
			_outputStream = manifest == null
			                ? new ZipOutputStream(Files.newOutputStream(_sourcesArchive))
			                : new JarOutputStream(Files.newOutputStream(_sourcesArchive), manifest);
		}
		catch (IOException exception)
		{
			throw new RuntimeException("Failed to create Minecraft source archive.", exception);
		}
	}

	@Override
	public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content)
	{
		saveClassEntry(path, archiveName, qualifiedName, entryName, content, null);
	}

	@Override
	public void saveClassEntry(
			String path,
			String archiveName,
			String qualifiedName,
			String entryName,
			String content,
			int[] mapping
	)
	{
		if (!_entries.add(entryName))
		{
			DecompilerContext.getLogger().writeMessage(
					"Zip entry " + entryName + " already exists in " + _sourcesArchive,
					IFernflowerLogger.Severity.WARN
			);
			return;
		}

		try
		{
			var zipEntry = new ZipEntry(entryName);

			if (mapping != null && DecompilerContext.getOption(IFernflowerPreferences.DUMP_CODE_LINES))
			{
				zipEntry.setExtra(getCodeLineData(mapping));
			}

			_outputStream.putNextEntry(zipEntry);

			if (content != null)
			{
				_outputStream.write(content.getBytes(StandardCharsets.UTF_8));
			}

			_outputStream.closeEntry();
		}
		catch (IOException exception)
		{
			DecompilerContext.getLogger().writeMessage("Cannot write entry " + entryName, exception);
		}
	}

	@Override
	public void closeArchive(String path, String archiveName)
	{
		try
		{
			if (_outputStream != null)
			{
				_outputStream.close();
				_outputStream = null;
			}

			if (_lineMapWriter != null)
			{
				_lineMapWriter.close();
				_lineMapWriter = null;
			}
		}
		catch (IOException exception)
		{
			throw new RuntimeException("Failed to finalize Minecraft source archive.", exception);
		}
	}

	@Override
	public void saveFolder(String path)
	{
	}

	@Override
	public void copyFile(String source, String path, String entryName)
	{
	}

	@Override
	public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping)
	{
	}

	@Override
	public void saveDirEntry(String path, String archiveName, String entryName)
	{
	}

	@Override
	public void copyEntry(String source, String path, String archiveName, String entry)
	{
	}
}
