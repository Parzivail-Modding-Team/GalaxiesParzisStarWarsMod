package dev.pswg.rendering.ptex;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A parsed {@code ptex} identifier.
 */
public final class PtexTextureReference
{
	/**
	 * The expected namespace prefix for PSWG texture-service identifiers.
	 */
	private static final String PTEX_PREFIX = "ptex";

	/**
	 * The original unresolved identifier.
	 */
	private final Identifier _originalIdentifier;

	/**
	 * The real resource namespace encoded inside the virtual namespace.
	 */
	private final String _sourceNamespace;

	/**
	 * The parsed texture domain.
	 */
	private final PtexTextureDomain _domain;

	/**
	 * The ordered sampler or baked service chain.
	 */
	private final List<String> _services;

	/**
	 * The raw path payload that will be interpreted by the domain-specific
	 * service chain.
	 */
	private final String _rawPath;

	/**
	 * Creates a parsed texture reference.
	 *
	 * @param originalIdentifier The original unresolved identifier.
	 * @param sourceNamespace    The real source namespace.
	 * @param domain             The parsed domain.
	 * @param services           The ordered service chain.
	 * @param rawPath            The raw service payload path.
	 */
	private PtexTextureReference(Identifier originalIdentifier, String sourceNamespace, PtexTextureDomain domain, List<String> services, String rawPath)
	{
		_originalIdentifier = originalIdentifier;
		_sourceNamespace = sourceNamespace;
		_domain = domain;
		_services = List.copyOf(services);
		_rawPath = rawPath;
	}

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
		var namespaceSegments = identifier.getNamespace().split("\\.");

		if (namespaceSegments.length < 3)
		{
			return Optional.empty();
		}

		// If we don't own this namespace, fail
		if (!PTEX_PREFIX.equals(namespaceSegments[0]))
		{
			return Optional.empty();
		}

		int domainIndex = -1;
		PtexTextureDomain domain = null;

		// Figure out what service domain the ID is requesting
		for (int index = namespaceSegments.length - 1; index >= 1; index--)
		{
			domain = PtexTextureDomain.parse(namespaceSegments[index]);

			if (domain != null)
			{
				domainIndex = index;
				break;
			}
		}

		if (domainIndex <= 1 || domain == null)
		{
			return Optional.empty();
		}

		// Build the source namespace
		var sourceNamespaceBuilder = new StringBuilder(namespaceSegments[1]);
		for (int index = 2; index < domainIndex; index++)
		{
			sourceNamespaceBuilder.append('.').append(namespaceSegments[index]);
		}

		return Optional.of(new PtexTextureReference(
				identifier,
				sourceNamespaceBuilder.toString(),
				domain,
				new ArrayList<>(Arrays.asList(namespaceSegments).subList(domainIndex + 1, namespaceSegments.length)),
				identifier.getPath()
		));
	}

	/**
	 * Gets the original unresolved identifier.
	 *
	 * @return The original identifier.
	 */
	public Identifier getOriginalIdentifier()
	{
		return _originalIdentifier;
	}

	/**
	 * Gets the source namespace encoded in the virtual namespace.
	 *
	 * @return The real resource namespace.
	 */
	public String getSourceNamespace()
	{
		return _sourceNamespace;
	}

	/**
	 * Gets the parsed texture domain.
	 *
	 * @return The texture domain.
	 */
	public PtexTextureDomain getDomain()
	{
		return _domain;
	}

	/**
	 * Gets the ordered list of service names.
	 *
	 * @return The service chain.
	 */
	public List<String> getServices()
	{
		return _services;
	}

	/**
	 * Gets the raw service payload path.
	 *
	 * @return The raw path segment.
	 */
	public String getRawPath()
	{
		return _rawPath;
	}
}
