package com.parzivail.toolchain.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilities for working with digests.
 */
public class DigestUtilities
{
	/**
	 * The hex encoder used for SHA-1 comparisons.
	 */
	private static final HexFormat HEX_FORMAT = HexFormat.of();

	/**
	 * Converts a byte array to a hexadecimal string.
	 *
	 * @param bytes the byte array
	 *
	 * @return the hexadecimal string
	 */
	public static String formatHex(byte[] bytes)
	{
		return HEX_FORMAT.formatHex(bytes);
	}

	/**
	 * Computes the SHA-1 digest of a file.
	 *
	 * @param path the file path
	 *
	 * @return the digest
	 *
	 * @throws NoSuchAlgorithmException if the SHA-1 algorithm is not available
	 * @throws IOException              if the file cannot be read
	 */
	public static MessageDigest computeSha1Digest(Path path) throws NoSuchAlgorithmException, IOException
	{
		var digest = MessageDigest.getInstance("SHA-1");

		try (var inputStream = Files.newInputStream(path))
		{
			var buffer = new byte[8192];
			int read;

			while ((read = inputStream.read(buffer)) >= 0)
			{
				digest.update(buffer, 0, read);
			}
		}

		return digest;
	}
}
