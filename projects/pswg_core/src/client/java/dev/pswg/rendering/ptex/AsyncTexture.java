package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.Nullable;

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
public class AsyncTexture implements AutoCloseable
{
	/**
	 * The composed image if it is already ready.
	 */
	private volatile NativeImage _readyImage;

	/**
	 * Whether this async texture has been closed.
	 */
	private volatile boolean _closed;

	/**
	 * The composed image future.
	 */
	private final CompletableFuture<NativeImage> _future;

	/**
	 * The callback that will be run when the texture is closed.
	 */
	private final @Nullable Runnable _closeCallback;

	public AsyncTexture(CompletableFuture<NativeImage> future, @Nullable Runnable closeCallback)
	{
		_future = future.whenComplete((image, throwable) -> {
			if (throwable != null)
			{
				return;
			}

			if (_closed)
			{
				if (image != null && !image.isClosed())
				{
					image.close();
				}

				return;
			}

			_readyImage = image;
		});

		this._closeCallback = closeCallback;
	}

	/**
	 * Gets the texture image immediately if it is already available.
	 *
	 * @return The current image if it is ready.
	 */
	public Optional<NativeImage> getNow()
	{
		return Optional.ofNullable(_readyImage);
	}

	/**
	 * Gets a future that completes with the final texture image.
	 *
	 * @return The future image.
	 */
	public CompletableFuture<NativeImage> getFuture()
	{
		return _future;
	}

	/**
	 * Waits for the texture image to become available.
	 *
	 * @return The resolved image.
	 *
	 * @throws IOException If the texture cannot be produced.
	 */
	public NativeImage getBlocking() throws IOException
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
	public void close()
	{
		_closed = true;

		if (_readyImage != null && !_readyImage.isClosed())
		{
			_readyImage.close();
			_readyImage = null;
		}

		if (_closeCallback != null)
			_closeCallback.run();
	}
}
