package dev.pswg.model.g3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;

/**
 * One fixed image/Ptex reference or a vanilla-style local texture slot (#base).
 * The string representation is shared by authoring and compiled material data.
 *
 * @param value The resource identifier or hash-prefixed slot name.
 */
public record G3dTextureReference(String value)
{
	/**
	 * Validates references through the codec rather than failing during rendering.
	 */
	private static DataResult<G3dTextureReference> decode(String value)
	{
		try
		{
			return DataResult.success(new G3dTextureReference(value));
		}
		catch (RuntimeException exception)
		{
			return DataResult.error(exception::getMessage);
		}
	}

	/**
	 * The existing texture string gains the same #slot syntax as vanilla models.
	 */
	public static final Codec<G3dTextureReference> CODEC = Codec.STRING.comapFlatMap(G3dTextureReference::decode, G3dTextureReference::value);

	/**
	 * Canonicalizes resources and rejects empty or malformed local slot names.
	 */
	public G3dTextureReference
	{
		if (value == null || value.isEmpty())
			throw new IllegalArgumentException("Texture reference must not be empty");
		if (value.startsWith("#"))
		{
			if (value.length() == 1 || !value.substring(1).matches("[a-zA-Z0-9_./-]+"))
				throw new IllegalArgumentException("Invalid texture slot " + value);
		}
		else
			value = Identifier.parse(value).toString();
	}

	/**
	 * Creates a fixed resource reference without string conversion at call sites.
	 */
	public G3dTextureReference(Identifier resource)
	{
		this(resource.toString());
	}

	/**
	 * Whether this material needs a binding supplied by its model or consumer.
	 */
	public boolean isSlot()
	{
		return value.startsWith("#");
	}

	/**
	 * Gets the local slot key, without its hash prefix.
	 */
	public String slot()
	{
		if (!isSlot())
			throw new IllegalStateException("Not a texture slot: " + value);
		return value.substring(1);
	}

	/**
	 * Gets the fixed resource identifier. Slot references must be resolved first.
	 */
	public Identifier resource()
	{
		if (isSlot())
			throw new IllegalStateException("Unbound texture slot: " + value);
		return Identifier.parse(value);
	}
}
