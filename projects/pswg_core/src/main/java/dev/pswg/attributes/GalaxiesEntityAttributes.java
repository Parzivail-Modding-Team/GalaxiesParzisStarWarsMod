package dev.pswg.attributes;

import dev.pswg.Galaxies;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * A collection of common entity attributes used in PSWG modules and addons
 */
public final class GalaxiesEntityAttributes
{
	public static final String I18N_ATTR_MULTIPLIER = "pswg.attribute.modifier.multiplier";

	/**
	 * The ID of the {@link GalaxiesEntityAttributes#FIELD_OF_VIEW_ZOOM} attribute modifier
	 */
	public static final Identifier FIELD_OF_VIEW_ZOOM_ID = Galaxies.id("field_of_view_zoom");

	/**
	 * An entity attribute that can modify the entity's field of view. Units
	 * are zoom multipliers (e.g. 2x, 5x, 10x zoom) and not field of view angle
	 * multipliers.
	 */
	public static final RegistryEntry<EntityAttribute> FIELD_OF_VIEW_ZOOM = register(
			FIELD_OF_VIEW_ZOOM_ID,
			(translationKey) -> new UnclampedEntityAttribute(translationKey, 1).setTracked(true)
	);

	/**
	 * Registers a new entity attribute with a specific ID.
	 *
	 * @param id                   The ID of the entity attribute to register
	 * @param attributeConstructor A function to construct the entity attribute using a translation key derived from the ID
	 *
	 * @return The registered entity attribute entry
	 */
	public static RegistryEntry<EntityAttribute> register(Identifier id, Function<String, EntityAttribute> attributeConstructor)
	{
		return Registry.registerReference(Registries.ATTRIBUTE, id, attributeConstructor.apply("%s.attribute.name.%s".formatted(id.getNamespace(), id.getPath())));
	}
}
