package dev.pswg.toolchain.source;

import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	 * The output streams keyed by Fernflower archive identity.
	 */
	private final Map<String, ZipOutputStream> _outputStreams;

	/**
	 * The single-threaded save executors keyed by Fernflower archive identity.
	 */
	private final Map<String, ExecutorService> _saveExecutors;

	/**
	 * Creates a saver for one source archive.
	 *
	 * @param sourcesArchive the destination archive
	 */
	public MinecraftSourceArchiveSaver(Path sourcesArchive)
	{
		_sourcesArchive = sourcesArchive;
		_outputStreams = new ConcurrentHashMap<>();
		_saveExecutors = new ConcurrentHashMap<>();
	}

	@Override
	public void createArchive(String path, String archiveName, Manifest manifest)
	{
		String key = archiveKey(path, archiveName);

		try
		{
			Files.createDirectories(_sourcesArchive.getParent());
			ZipOutputStream outputStream = manifest == null
				? new ZipOutputStream(Files.newOutputStream(_sourcesArchive))
				: new JarOutputStream(Files.newOutputStream(_sourcesArchive), manifest);

			_outputStreams.put(key, outputStream);
			_saveExecutors.put(key, Executors.newSingleThreadExecutor());
		}
		catch (IOException exception)
		{
			throw new UncheckedIOException("Failed to create Minecraft source archive.", exception);
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
		String key = archiveKey(path, archiveName);
		ExecutorService executor = _saveExecutors.get(key);

		executor.submit(() ->
		{
			ZipOutputStream outputStream = _outputStreams.get(key);

			try
			{
				outputStream.putNextEntry(new ZipEntry(entryName));

				if (content != null)
				{
					outputStream.write(content.getBytes(StandardCharsets.UTF_8));
				}

				outputStream.closeEntry();
			}
			catch (IOException exception)
			{
				DecompilerContext.getLogger().writeMessage("Cannot write entry " + entryName, exception);
			}
		});
	}

	@Override
	public void closeArchive(String path, String archiveName)
	{
		String key = archiveKey(path, archiveName);
		ExecutorService executor = _saveExecutors.get(key);
		Future<?> closeFuture = executor.submit(() ->
		{
			try
			{
				_outputStreams.get(key).close();
			}
			catch (IOException exception)
			{
				throw new UncheckedIOException("Failed to close Minecraft source archive.", exception);
			}
		});

		executor.shutdown();

		try
		{
			closeFuture.get();
		}
		catch (Exception exception)
		{
			throw new RuntimeException("Failed to finalize Minecraft source archive.", exception);
		}
		finally
		{
			_outputStreams.remove(key);
			_saveExecutors.remove(key);
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

	/**
	 * Builds the Fernflower archive key.
	 *
	 * @param path the save path
	 * @param archiveName the archive name
	 * @return the archive key
	 */
	private static String archiveKey(String path, String archiveName)
	{
		return path + "/" + archiveName;
	}
}
