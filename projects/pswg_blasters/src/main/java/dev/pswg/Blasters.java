package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.configuration.BlastersConfig;
import dev.pswg.configuration.IConfigContainer;
import dev.pswg.configuration.MemoryConfigContainer;
import dev.pswg.data.BlasterData;
import dev.pswg.data.BlasterImpactEffect;
import dev.pswg.entity.BlasterBoltEntity;
import dev.pswg.item.BlasterItem;
import dev.pswg.item.ChargedItem;
import dev.pswg.item.component.StoredCharge;
import dev.pswg.interaction.BlasterActions;
import dev.pswg.registry.Registrar;
import dev.pswg.sound.BlasterSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

	/**
	 * An item tag that contains all PSWG module and addon blasters
	 */
	public static final TagKey<Item> BLASTERS_TAG = TagKey.create(Registries.ITEM, id("blasters"));

	public static final Identifier DEFAULT_HUD = id("default");

	public static final Identifier BLASTER_ITEM_ID = id("blaster");
	public static final BlasterItem BLASTER_ITEM = Registrar.item(BLASTER_ITEM_ID, BlasterItem::new, BlasterItem.createSettings());

	/**
	 * Compact 100-unit pack.
	 */
	public static final Item SMALL_POWER_PACK = Registrar.item(
			id("small_power_pack"),
			ChargedItem::new,
			new Item.Properties()
					.stacksTo(1)
					.component(StoredCharge.COMPONENT, new StoredCharge(100, 100))
	);

	/**
	 * Standard 500-unit pack.
	 */
	public static final Item POWER_PACK = Registrar.item(
			id("power_pack"),
			ChargedItem::new,
			new Item.Properties()
					.stacksTo(1)
					.component(StoredCharge.COMPONENT, new StoredCharge(500, 500))
	);

	public static final EntityType<BlasterBoltEntity> BLASTER_BOLT_ENTITY = Registrar.entityType(
			id("blaster_bolt"),
			EntityType.Builder.of(BlasterBoltEntity::new, MobCategory.MISC)
			                  .sized(0.4f, 0.4f)
			                  .eyeHeight(0.2f)
			                  .noLootTable()
			                  .clientTrackingRange(4)
			                  .updateInterval(BlasterBoltEntity.UPDATE_INTERVAL_TICKS)
	);

	@Override
	public void onGalaxiesReady()
	{
		BlasterImpactEffect.register();
		BlasterData.register();
		BlasterActions.register();

		BlasterSounds.register();

		LOGGER.info("Module initialized");
	}

	@Override
	public void onGalaxiesFinalizing()
	{
		BlasterData.freezeTypes();
	}
}
