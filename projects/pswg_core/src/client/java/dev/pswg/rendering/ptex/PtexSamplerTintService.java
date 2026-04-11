package dev.pswg.rendering.ptex;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A sampler-domain texture service that multiplies a source texture by a tint
 * color.
 */
public final class PtexSamplerTintService implements PtexSamplerTextureService
{
	/**
	 * The serialized service name used in the {@code ptex} namespace.
	 */
	private final String _serviceName;

	/**
	 * Creates a new sampler-domain tint service.
	 *
	 * @param serviceName The serialized service name.
	 */
	public PtexSamplerTintService(String serviceName)
	{
		_serviceName = serviceName;
	}

	@Override
	public String getServiceName()
	{
		return _serviceName;
	}

	@Override
	public NativeImage load(PtexTextureReference reference, ResourceManager resourceManager) throws IOException
	{
		var request = parseRequest(reference);

		var resource = resourceManager
				.getResource(request.sourceIdentifier)
				.orElseThrow(() -> new FileNotFoundException("Missing texture resource " + request.sourceIdentifier));

		try (
				var stream = resource.open();
				var sourceImage = NativeImage.read(stream)
		)
		{
			var tintedImage = new NativeImage(sourceImage.getWidth(), sourceImage.getHeight(), true);

			for (int y = 0; y < sourceImage.getHeight(); y++)
			{
				for (int x = 0; x < sourceImage.getWidth(); x++)
				{
					int pixel = sourceImage.getPixel(x, y);
					tintedImage.setPixel(x, y, multiplyTint(pixel, request.tintColor));
				}
			}

			return tintedImage;
		}
	}

	/**
	 * Parses the tint-specific request payload from the generic texture
	 * reference.
	 *
	 * @param reference The parsed texture reference.
	 *
	 * @return The parsed tint request.
	 *
	 * @throws IOException If the request payload does not contain a valid tint
	 *                     argument and source path.
	 */
	private static TintRequest parseRequest(PtexTextureReference reference) throws IOException
	{
		String rawPath = reference.getRawPath();
		int slash = rawPath.indexOf('/');
		if (slash <= 0 || slash == rawPath.length() - 1)
		{
			throw new IOException("Malformed tint ptex path '" + rawPath + "' for " + reference.getOriginalIdentifier());
		}

		String tintToken = rawPath.substring(0, slash);
		Integer parsedColor = parseColor(tintToken);
		if (parsedColor == null)
		{
			throw new IOException("Invalid tint color '" + tintToken + "' for " + reference.getOriginalIdentifier());
		}

		try
		{
			return new TintRequest(
					Identifier.fromNamespaceAndPath(reference.getSourceNamespace(), rawPath.substring(slash + 1)),
					parsedColor
			);
		}
		catch (IllegalArgumentException exception)
		{
			throw new IOException("Invalid tinted texture path '" + rawPath + "' for " + reference.getOriginalIdentifier(), exception);
		}
	}

	/**
	 * Parses a service tint color.
	 *
	 * @param value The serialized color value.
	 *
	 * @return The parsed RGB color, or {@code null} if the value is not a valid
	 *         tint argument.
	 */
	private static Integer parseColor(String value)
	{
		String normalized = value;

		// Identifiers can only contain lowercase characters and numbers
		// so we can't do, for example, hex prefixes

		if (normalized.startsWith("0x"))
		{
			normalized = normalized.substring(2);
		}

		// ARGB constants only
		if (!normalized.matches("[0-9a-f]{8}"))
		{
			return null;
		}

		return Integer.parseUnsignedInt(normalized, 16);
	}

	/**
	 * Multiplies a source pixel by a tint color while preserving alpha.
	 *
	 * @param pixel     The source pixel.
	 * @param tintColor The tint color to multiply into the pixel.
	 *
	 * @return The tinted pixel.
	 */
	private static int multiplyTint(int pixel, int tintColor)
	{
		int alpha = ARGB.alpha(pixel) * ARGB.alpha(tintColor) / 255;
		int red = ARGB.red(pixel) * ARGB.red(tintColor) / 255;
		int green = ARGB.green(pixel) * ARGB.green(tintColor) / 255;
		int blue = ARGB.blue(pixel) * ARGB.blue(tintColor) / 255;

		return ARGB.color(alpha, red, green, blue);
	}

	/**
	 * The fully parsed request for the tint service.
	 *
	 * @param sourceIdentifier The source texture to tint.
	 * @param tintColor        The tint color to apply.
	 */
	private record TintRequest(Identifier sourceIdentifier, int tintColor)
	{
	}
}
