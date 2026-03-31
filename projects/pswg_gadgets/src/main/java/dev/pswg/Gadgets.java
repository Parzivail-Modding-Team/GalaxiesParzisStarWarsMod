package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.container.*;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.feature.brewing.BrewingMap;
import dev.pswg.feature.brewing.MixerBrewingPaths;
import dev.pswg.feature.brewing.MixerFoodColors;
import dev.pswg.networking.MixerSyncS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

import java.io.InputStream;

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
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	/**
	 * A logger available only to PSWG module and addon gadgets
	 */
	public static final Logger LOGGER = Galaxies.createSubLogger("gadgets");

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

		MixerFoodColors.init();
		MixerBrewingPaths.init();

		PayloadTypeRegistry.clientboundPlay().register(MixerSyncS2CPayload.TYPE, MixerSyncS2CPayload.CODEC);

		ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Gadgets.id("brewing_maps"), (ResourceManagerReloadListener)manager ->
		{
			for (Identifier id : manager.listResources("brewing_map", path -> true).keySet())
			{
				try (InputStream stream = manager.getResource(id).get().open())
				{
					BrewingMap.init(stream);
				}
				catch (Exception e)
				{
					Gadgets.LOGGER.error("Error occurred while loading brewing map " + id.toString(), e);
				}
			}
		});

		// TODO: how to differentiate different modules' versions?

		LOGGER.info("Module initialized");
	}
}
