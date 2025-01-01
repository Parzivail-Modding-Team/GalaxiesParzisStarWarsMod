package dev.pswg;

import com.mojang.serialization.Codec;
import dev.pswg.api.GalaxiesAddon;
import dev.pswg.entity.FragmentationGrenadeEntity;
import dev.pswg.entity.ThermalDetonatorEntity;
import dev.pswg.item.FragmentationGrenadeItem;
import dev.pswg.item.GrenadeItem;
import dev.pswg.item.ThermalDetonatorItem;
import dev.pswg.registry.Registrar;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

	public static final EntityType<FragmentationGrenadeEntity> FRAGMENTATION_GRENADE_ENTITY = Registrar.entityType(
			id("fragmentation_grenade"),
			EntityType.Builder.create(FragmentationGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.4f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()

	);
	public static final EntityType<ThermalDetonatorEntity> THERMAL_DETONATOR_ENTITY = Registrar.entityType(
			id("thermal_detonator"),
			EntityType.Builder.create(ThermalDetonatorEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.2f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()

	);

	public static final ComponentType<Long> PRIMING_TIME = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MODID, "priming_time"),
			ComponentType.<Long>builder().codec(Codec.LONG).build()
	);

	public static final GrenadeItem THERMAL_DETONATOR_ITEM = Registrar.item(id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Settings());
	public static final GrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Settings());

	public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.of(RegistryKeys.BLOCK, id("fragmentation_destroy"));
	public static final TagKey<Block> DETONATES_GRENADE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MODID, "detonates_grenade"));

	public static final TagKey<Item> GRENADES_TAG = TagKey.of(RegistryKeys.ITEM, id("grenades"));

	public static final TagKey<DamageType> IGNITES_EXPLOSIVES = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MODID, "ignites_explosives"));

	public static final SimpleParticleType EXPLOSION_SMOKE_PARTICLE =  FabricParticleTypes.simple();


	@Override
	public void onGalaxiesReady()
	{
		// TODO: how to differentiate different modules' versions?
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MODID, "explosion_smoke"), EXPLOSION_SMOKE_PARTICLE);
		DispenserBlock.registerProjectileBehavior(Gadgets.THERMAL_DETONATOR_ITEM);
		LOGGER.info("Module initialized");
	}
}
