package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.container.*;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.container.structure.GalaxiesStructureKeys;
import dev.pswg.container.structure.GalaxiesStructurePieces;
import dev.pswg.container.structure.GalaxiesStructureTypes;
import dev.pswg.feature.brewing.BrewingMap;
import dev.pswg.feature.brewing.MixerFoodColors;
import dev.pswg.packet.MixerSyncS2CPayload;
import dev.pswg.packet.PreciseVelocityParticleS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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
		return Identifier.of(MODID, path);
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
		GalaxiesStructurePieces.register();
		GalaxiesStructureTypes.register();
		GalaxiesStructureKeys.register();
		GalaxiesLootTables.register();

		MixerFoodColors.init();

		PayloadTypeRegistry.playS2C().register(MixerSyncS2CPayload.ID, MixerSyncS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(PreciseVelocityParticleS2CPayload.ID, PreciseVelocityParticleS2CPayload.CODEC);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener()
		{
			@Override
			public Identifier getFabricId()
			{
				return Gadgets.id("brewing_maps");
			}

			@Override
			public void reload(ResourceManager manager)
			{
				for (Identifier id : manager.findResources("brewing_map", path -> true).keySet())
				{
					try (InputStream stream = manager.getResource(id).get().getInputStream())
					{
						BrewingMap.init(stream);
					}
					catch (Exception e)
					{
						Gadgets.LOGGER.error("Error occurred while loading brewing map " + id.toString(), e);
					}
				}
			}
		});

		// TODO: how to differentiate different modules' versions?

		LOGGER.info("Module initialized");
	}
}