package dev.pswg.rendering.ptex;

import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * The public entrypoint for PSWG {@code ptex} texture handling.
 */
public final class PtexTextures
{
	/**
	 * The singleton sampler texture manager.
	 */
	public static final PtexSamplerTextureManager LOADER = new PtexSamplerTextureManager();

	/**
	 * Attempts to parse a {@code ptex} identifier.
	 *
	 * @param identifier The identifier to parse.
	 *
	 * @return The parsed texture reference if the identifier uses the
	 *         {@code ptex} namespace grammar.
	 */
	public static Optional<PtexTextureReference> parse(Identifier identifier)
	{
		return PtexTextureReference.parse(identifier);
	}

	/**
	 * Resolves a supported {@code ptex} identifier to the concrete texture
	 * identifier that should be used for rendering.
	 *
	 * @param identifier The identifier to resolve.
	 *
	 * @return The resolved texture identifier, or the original identifier if it
	 *         does not use a currently supported {@code ptex} domain.
	 */
	public static Identifier resolve(Identifier identifier)
	{
		var parsedReference = parse(identifier);
		if (parsedReference.isEmpty())
		{
			return identifier;
		}

		if (parsedReference.get().getDomain() == PtexTextureDomain.SAMPLER)
		{
			return resolveSampler(identifier);
		}

		return identifier;
	}

	/**
	 * Resolves a sampler-domain identifier to a stable runtime texture id.
	 *
	 * @param identifier The identifier to resolve.
	 *
	 * @return The runtime identifier for supported sampler textures, or the
	 *         original identifier if it is not handled by the framework.
	 */
	public static Identifier resolveSampler(Identifier identifier)
	{
		return LOADER.resolve(identifier);
	}

	/**
	 * Registers a runtime sampler-domain texture service.
	 *
	 * @param service The service to register.
	 */
	public static void registerSamplerService(PtexSamplerTextureService service)
	{
		LOADER.registerService(service);
	}
}
