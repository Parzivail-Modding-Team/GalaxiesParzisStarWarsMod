package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A lazily produced texture image that may become available asynchronously.
 *
 * <p>The returned {@link NativeImage} instances are owned by the async texture
 * and must not be closed by callers.
 */
public interface PtexAsyncTexture extends AutoCloseable
{
	/**
	 * Gets the texture image immediately if it is already available.
	 *
	 * @return The current image if it is ready.
	 */
	Optional<NativeImage> getNow();

	/**
	 * Gets a future that completes with the final texture image.
	 *
	 * @return The future image.
	 */
	CompletableFuture<NativeImage> getFuture();

	/**
	 * Waits for the texture image to become available.
	 *
	 * @return The resolved image.
	 *
	 * @throws IOException If the texture cannot be produced.
	 */
	default NativeImage getBlocking() throws IOException
	{
		try
		{
			return getFuture().get();
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while waiting for async ptex texture", exception);
		}
		catch (ExecutionException exception)
		{
			if (exception.getCause() instanceof IOException ioException)
			{
				throw ioException;
			}

			throw new IOException("Failed to resolve async ptex texture", exception.getCause());
		}
	}

	/**
	 * Releases any images owned by this texture.
	 */
	@Override
	default void close()
	{
	}
}
