package dev.pswg.rendering.g3d;

import com.mojang.blaze3d.platform.Transparency;
import dev.pswg.Galaxies;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The small, replaceable boundary between symbolic G3D layers and Minecraft.
 */
public final class G3dLayers
{
	/**
	 * Resolves the block/item transparency used by vanilla baked materials.
	 */
	public static Transparency transparency(Identifier id)
	{
		if (id.getNamespace().equals("minecraft"))
		{
			return switch (id.getPath())
			{
				case "block/solid", "item/solid" -> Transparency.NONE;
				case "block/cutout", "item/cutout" -> Transparency.TRANSPARENT;
				case "block/translucent", "item/translucent" -> Transparency.TRANSLUCENT;
				default -> fallback(id);
			};
		}

		return fallback(id);
	}

	/**
	 * Chooses a native sampled surface without storing a RenderType in the asset.
	 */
	public static RenderType sampled(Identifier id, Identifier texture, boolean doubleSided, boolean foil)
	{
		if (id.getNamespace().equals("minecraft"))
		{
			return switch (id.getPath())
			{
				case "item/solid", "item/cutout" -> foil ? RenderTypes.itemCutoutGlint(texture) : RenderTypes.itemCutout(texture);
				case "item/translucent" -> foil ? RenderTypes.itemTranslucentGlint(texture) : RenderTypes.itemTranslucent(texture);
				case "entity/solid" -> RenderTypes.entitySolid(texture);
				case "entity/cutout" -> foil ? RenderTypes.entitySolidGlint(texture) : doubleSided ? RenderTypes.entityCutout(texture) : RenderTypes.entityCutoutCull(texture);
				case "entity/cutout_no_cull" -> foil ? RenderTypes.entitySolidGlint(texture) : RenderTypes.entityCutout(texture);
				case "entity/translucent" -> doubleSided ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityTranslucentCull(texture);
				case "entity/translucent_emissive" -> RenderTypes.entityTranslucentEmissive(texture);
				default -> sampledFallback(id, texture);
			};
		}

		return sampledFallback(id, texture);
	}

	/**
	 * Pass through the vanilla armor renderers and put G3D armors as sampled textures
	 */
	public static RenderType armor(Identifier id, Identifier texture, boolean doubleSided, boolean foil)
	{
		if (id.getNamespace().equals("minecraft") && (id.getPath().equals("entity/cutout") || id.getPath().equals("entity/cutout_no_cull")))
			return foil ? RenderTypes.armorCutoutNoCullGlint(texture) : RenderTypes.armorCutoutNoCull(texture);

		return sampled(id, texture, doubleSided, foil);
	}

	/**
	 * Only culling native layers need a reversed triangle for a two-sided surface.
	 */
	public static boolean needsBackFaces(Identifier id, boolean doubleSided)
	{
		return doubleSided && id.getNamespace().equals("minecraft") && (id.getPath().startsWith("item/") || id.getPath().equals("entity/solid"));
	}

	/**
	 * Logs an unsupported symbol without rejecting secure resource-pack input.
	 */
	private static Transparency fallback(Identifier id)
	{
		if (WARNED.add(id))
			Galaxies.LOGGER.warn("Unknown G3D layer {}; using native cutout", id);
		return Transparency.TRANSPARENT;
	}

	/**
	 * Uses the native no-cull fallback so authoring errors remain visible.
	 */
	private static RenderType sampledFallback(Identifier id, Identifier texture)
	{
		fallback(id);
		return RenderTypes.entityCutout(texture);
	}

	/**
	 * Unknown choices are diagnosed once, then use the safe native cutout default.
	 */
	private static final Set<Identifier> WARNED = ConcurrentHashMap.newKeySet();

	/**
	 * Prevents construction of this native layer catalog.
	 */
	private G3dLayers()
	{
	}
}
