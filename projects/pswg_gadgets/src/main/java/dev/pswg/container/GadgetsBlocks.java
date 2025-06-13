package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.block.FragmentationGrenadeBlock;
import dev.pswg.block.ThermalDetonatorBlock;
import dev.pswg.registry.Registrar;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class GadgetsBlocks
{
	public static final class Tags
	{
		public static final TagKey<Block> FRAGMENTATION_GRENADE_DESTROY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("fragmentation_destroy"));
		public static final TagKey<Block> DETONATES_GRENADE = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("detonates_grenade"));
		public static final TagKey<Block> BOUNCY = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("bouncy"));
		public static final TagKey<Block> GAS_PASS_THROUGH = TagKey.of(RegistryKeys.BLOCK, Gadgets.id("gas_pass_through"));
	}

	public static final ThermalDetonatorBlock THERMAL_DETONATOR_BLOCK = Registrar.block(Gadgets.id("thermal_detonator_block"), ThermalDetonatorBlock::new, Block.Settings.create());
	public static final FragmentationGrenadeBlock FRAGMENTATION_GRENADE_BLOCK = Registrar.block(Gadgets.id("fragmentation_grenade_block"), FragmentationGrenadeBlock::new, Block.Settings.create());

	public static void register()
	{
	}
}
