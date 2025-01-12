package dev.pswg;

import com.mojang.serialization.Codec;
import dev.pswg.api.GalaxiesAddon;
import dev.pswg.block.ThermalDetonatorBlock;
import dev.pswg.entity.FragmentationGrenadeEntity;
import dev.pswg.entity.ThermalDetonatorEntity;
import dev.pswg.item.FragmentationGrenadeItem;
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
import net.minecraft.sound.SoundEvent;
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

	public static final ThermalDetonatorItem THERMAL_DETONATOR_ITEM = Registrar.item(id("thermal_detonator"), ThermalDetonatorItem::new, new Item.Settings());
	public static final FragmentationGrenadeItem FRAGMENTATION_GRENADE_ITEM = Registrar.item(id("fragmentation_grenade"), FragmentationGrenadeItem::new, new Item.Settings());

	public static final ThermalDetonatorBlock THERMAL_DETONATOR_BLOCK = Registrar.block(id("thermal_detonator_block"), ThermalDetonatorBlock::new, Block.Settings.create());

	public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.of(RegistryKeys.BLOCK, id("fragmentation_destroy"));
	public static final TagKey<Block> DETONATES_GRENADE = TagKey.of(RegistryKeys.BLOCK, id("detonates_grenade"));

	public static final TagKey<Item> GRENADES_TAG = TagKey.of(RegistryKeys.ITEM, id("grenades"));

	public static final TagKey<DamageType> IGNITES_EXPLOSIVES = TagKey.of(RegistryKeys.DAMAGE_TYPE, id("ignites_explosives"));

	public static final SimpleParticleType EXPLOSION_SMOKE_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, id("explosion_smoke"), FabricParticleTypes.simple());
	public static final SimpleParticleType FRAGMENTATION_GRENADE_SPARK_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, id("fragmentation_grenade_spark"), FabricParticleTypes.simple());
	public static final SimpleParticleType FRAGMENTATION_GRENADE_WAVE_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, id("fragmentation_grenade_wave"), FabricParticleTypes.simple());
	public static final SimpleParticleType SMOKE_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, id("smoke"), FabricParticleTypes.simple());

	public static final SoundEvent ARM = registerSound(id("shared.arm"));
	public static final SoundEvent DISARM = registerSound(id("shared.disarm"));
	public static final SoundEvent THROW = registerSound(id("shared.throw"));
	public static final SoundEvent THERMAL_DETONATOR_BEEP = registerSound(id("thermaldetonator.beep"));
	public static final SoundEvent THERMAL_DETONATOR_EXPLOSION = registerSound(id("thermaldetonator.explode"));
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION1 = registerSound(id("fragmentationgrenade.explode1"));
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION2 = registerSound(id("fragmentationgrenade.explode2"));
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION3 = registerSound(id("fragmentationgrenade.explode3"));
	public static final SoundEvent FRAGMENTATION_GRENADE_EXPLOSION4 = registerSound(id("fragmentationgrenade.explode4"));
	public static final SoundEvent FRAGMENTATION_GRENADE_BEEP = registerSound(id("fragmentationgrenade.beep"));

	private static SoundEvent registerSound(Identifier id)
	{
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	@Override
	public void onGalaxiesReady()
	{
		// TODO: how to differentiate different modules' versions?

		DispenserBlock.registerProjectileBehavior(Gadgets.THERMAL_DETONATOR_ITEM);

		LOGGER.info("Module initialized");
	}
}
