package dev.pswg.datagen;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A collection of utilities for working with data providers during data generation
 */
public final class GalaxiesDataProvider
{
	/**
	 * Writes the specified value to the specified path using the specified codec
	 *
	 * @param writer The data writer to use to write the file
	 * @param path   The path to write the file to
	 * @param codec  The codec to use to encode the value
	 * @param value  The value to encode
	 *
	 * @return A completable future that completes when the file has been written
	 */
	public static <T> CompletableFuture<?> writeToPath(CachedOutput writer, Path path, StreamCodec<ByteBuf, T> codec, T value)
	{
		return CompletableFuture.runAsync(() -> {
			try
			{
				var byteArrayOutputStream = new ByteArrayOutputStream();
				var hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);

				var buf = Unpooled.buffer();
				codec.encode(buf, value);

				buf.readBytes(hashingOutputStream, buf.readableBytes());

				writer.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
			}
			catch (IOException ex)
			{
				DataProvider.LOGGER.error("Failed to save file to {}", path, ex);
			}
		}, Util.backgroundExecutor().forName("saveStable"));
	}

	/**
	 * If the file is a datagen-only asset, gets the location that the file should
	 * be placed after the file has been processed, i.e., the path of the file
	 * that does not end with {@code /datagen/} and without an extension.
	 *
	 * @param filename The filename to process
	 * @param baseName The base name of the file, or empty to use the filename's base name
	 *
	 * @return The new filename after the file has been processed
	 */
	public static String getNonDatagenPath(String filename, Optional<String> baseName)
	{
		String datagenPath = "/datagen/";

		var path = FilenameUtils.getPath(filename);

		if (path.endsWith(datagenPath))
			path = path.substring(0, path.length() - datagenPath.length() + 1);

		path += baseName.orElse(FilenameUtils.getBaseName(filename));

		return path;
	}
}
