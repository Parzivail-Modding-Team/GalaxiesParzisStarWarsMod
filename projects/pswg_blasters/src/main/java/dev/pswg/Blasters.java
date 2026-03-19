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
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
		return Identifier.fromNamespaceAndPath(MODID, path);
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
			true,
			IdentifierUtil::isJsonFile,
			BlasterDatapackDefinition.CODEC
	);

	/**
	 * An item tag that contains all PSWG module and addon blasters
	 */
	public static final TagKey<Item> BLASTERS_TAG = TagKey.create(Registries.ITEM, id("blasters"));

	public static final Identifier DEFAULT_HUD = id("default");

	public static final Identifier BLASTER_ITEM_ID = id("blaster");
	public static final BlasterItem BLASTER_ITEM = Registrar.item(BLASTER_ITEM_ID, BlasterItem::new, BlasterItem.createSettings());

	public static final EntityType<BlasterBoltEntity> BLASTER_BOLT_ENTITY = Registrar.entityType(
			id("blaster_bolt"),
			EntityType.Builder.of(BlasterBoltEntity::new, MobCategory.MISC)
			                  .sized(0.4f, 0.4f)
			                  .eyeHeight(0.2f)
			                  .noLootTable()
			                  .clientTrackingRange(4)
			                  .updateInterval(20)
	);

	private static void addBlastersToTab(FabricCreativeModeTabOutput itemGroup)
	{
		for (var definition : DATAPACK_LOADER.getDefinitions().entrySet())
		{
			LOGGER.debug("Registering blaster definition: {}", definition.getKey());
			itemGroup.accept(BlasterItem.createStack(definition.getKey(), definition.getValue()));
		}
	}

	@Override
	public void onGalaxiesReady()
	{
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
		               .register(Blasters::addBlastersToTab);

		ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(DATAPACK_LOADER.getId(), DATAPACK_LOADER);

		BlasterSounds.register();

		LOGGER.info("Module initialized");
	}
}
