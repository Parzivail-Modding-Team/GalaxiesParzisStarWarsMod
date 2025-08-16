package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.container.*;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.container.entity.LivingEntities;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * The main entrypoint for PSWG common-side gadget features
 */
public final class Gadgets implements GalaxiesAddon
{
	/**
	 * The mod ID assigned to this addon
	 */
	public static final String MODID = "pswg_gadgets";

	/**
	 * Creates a scoped {@link Identifier} whose domain is this
	 * mod's MODID
	 *
	 * @param path The path for the {@link Identifier}
	 *
	 * @return A scoped {@link Identifier}
	 */
	public static Identifier id(String path)
	{
		return Identifier.of(MODID, path);
	}

	/**
	 * A logger available only to PSWG module and addon gadgets
	 */
	public static final Logger LOGGER = Galaxies.createSubLogger("gadgets");

	public static final TagKey<Item> BANTHA_TEMPT = TagKey.of(RegistryKeys.ITEM, id("bantha_tempt"));

	@Override
	public void onGalaxiesReady()
	{
		GadgetsItems.register();
		GadgetsBlocks.register();
		GadgetsEntities.register();
		GadgetsSounds.register();
		GadgetsEffects.register();
		GadgetsParticleTypes.register();
		GadgetsDamage.register();
		GadgetsBlockEntities.register();
		GadgetsScreenHandlerTypes.register();
		GadgetsRecipeTypes.register();
		GadgetsRecipeSerializers.register();
		GadgetsItemGroups.register();
		GadgetsStructureTypes.register();
		LivingEntities.register();

		// TODO: how to differentiate different modules' versions?

		LOGGER.info("Module initialized");
	}
}