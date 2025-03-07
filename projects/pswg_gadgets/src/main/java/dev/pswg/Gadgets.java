package dev.pswg;

import dev.pswg.api.GalaxiesAddon;
import dev.pswg.block.FragmentationGrenadeBlock;
import dev.pswg.block.ThermalDetonatorBlock;
import dev.pswg.entity.grenades.FragmentationGrenadeEntity;
import dev.pswg.entity.NerveGasEntity;
import dev.pswg.entity.grenades.NerveGasGrenadeEntity;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.entity.effects.IntoxicatedEffect;
import dev.pswg.item.FragmentationGrenadeItem;
import dev.pswg.item.NerveGasGrenadeItem;
import dev.pswg.item.ThermalDetonatorItem;
import dev.pswg.registry.Registrar;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.GadgetsSounds;
import dev.pswg.container.entity.GadgetsDamage;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.container.entity.GadgetsEntities;
import dev.pswg.container.GadgetsItems;
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
		LivingEntities.register();

		// TODO: how to differentiate different modules' versions?

		LOGGER.info("Module initialized");
	}
}