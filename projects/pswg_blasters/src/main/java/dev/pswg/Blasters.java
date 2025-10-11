package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.configuration.BlastersConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import dev.pswg.data.BlasterDatapackDefinition;
import dev.pswg.data.CodecDataLoader;
import dev.pswg.data.IdentifierUtil;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.item.BlasterItem;
import dev.pswg.registry.Registrar;
import dev.pswg.sound.BlasterSounds;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * The main entrypoint for PSWG common-side blaster features
 */
public final class Blasters implements GalaxiesAddon
{
	/**
	 * The mod ID assigned to PSWG
	 */
	public static final String MODID = "pswg_blasters";

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
	 * A logger available only to PSWG module and addon blasters
	 */
	public static final Logger LOGGER = Galaxies.createSubLogger(MODID);

	/**
	 * The configuration file that controls the behavior of PSWG core
	 */
	public static final IConfigContainer<BlastersConfig> CONFIG = new MemoryConfigContainer<>(new BlastersConfig());

	public static final CodecDataLoader<BlasterDatapackDefinition> DATAPACK_LOADER = new CodecDataLoader<>(
			id("data"),
			"blasters",
			IdentifierUtil::isJsonFile,
			BlasterDatapackDefinition.CODEC
	);

	/**
	 * An item tag that contains all PSWG module and addon blasters
	 */
	public static final TagKey<Item> BLASTERS_TAG = TagKey.of(RegistryKeys.ITEM, id("blasters"));

	public static final Identifier DEFAULT_HUD = id("default");

	public static final Identifier BLASTER_ITEM_ID = id("blaster");
	public static final BlasterItem BLASTER_ITEM = Registrar.item(BLASTER_ITEM_ID, BlasterItem::new, BlasterItem.createSettings());

	public static final EntityType<BlasterBoltEntity> BLASTER_BOLT_ENTITY = Registrar.entityType(
			id("blaster_bolt"),
			EntityType.Builder.create(BlasterBoltEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.4f, 0.4f)
			                  .eyeHeight(0.2f)
			                  .dropsNothing()
			                  .maxTrackingRange(4)
			                  .trackingTickInterval(20)
	);

	private static void addBlastersToTab(FabricItemGroupEntries itemGroup)
	{
		for (var definition : DATAPACK_LOADER.getDefinitions().entrySet())
		{
			LOGGER.debug("Registering blaster definition: {}", definition.getKey());
			itemGroup.add(BlasterItem.createStack(definition.getKey(), definition.getValue()));
		}
	}

	@Override
	public void onGalaxiesReady()
	{
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
		               .register(Blasters::addBlastersToTab);

		ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(DATAPACK_LOADER.getId(), DATAPACK_LOADER);

		BlasterSounds.register();

		LOGGER.info("Module initialized");
	}
}
